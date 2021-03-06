package komponenten.spielregel.impl;

import komponenten.spielverwaltung.entities.Spieler;
import komponenten.karten.entities.Spielkarte;
import komponenten.karten.entities.Blatttyp;
import komponenten.karten.entities.Blattwert;
import komponenten.spielverwaltung.entities.Spielrunde;
import util.exceptions.TechnischeException;
import komponenten.spielregel.entities.RegelComponentUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementierung von spielregel-Komponent mit Zwei_ziehen(7), Aussetzen(Ass) und Wünscher(Bube)
 */
@Service
@Qualifier("basicSonder")
public class SpielregelBasicSonderImpl extends SpielregelOhneSonderImpl {


    @Override
    public boolean istKarteLegbar(Spielkarte vorherigeSpielkarte, Spielkarte aktuelleSpielkarte, Blatttyp blatttyp, boolean sindKartenZuZiehen) {
        if (vorherigeSpielkarte == null) {
            throw new TechnischeException("Vorherige Spielkarte ist nicht initialisiert");
        } else if (aktuelleSpielkarte == null) {
            throw new TechnischeException("Aktuelle Spielkarte ist nicht initialisiert");
        }
        boolean istLegbar = false;
        if (blatttyp != null) {
        // Wenn Blatttyp im Zug davor gewählt, dann kann man nur Blattyp spielen aber nicht noch eine Bube

            if (aktuelleSpielkarte.getBlattwert() == Blattwert.Bube) {
                istLegbar = false;
            } else if (aktuelleSpielkarte.getBlatttyp() == blatttyp) {
                istLegbar = true;
            }
        } else {
            // Basic-Prüfung ohne SonderRegel
                istLegbar = super.istKarteLegbar(vorherigeSpielkarte, aktuelleSpielkarte, blatttyp, sindKartenZuZiehen);
            // Bube darf man auf jede Farbe spielen (also wenn keine Bube der sieben davor gespielt wurde)
            if (aktuelleSpielkarte.getBlattwert() == Blattwert.Bube) {
                istLegbar = true;
            }
            // Sonderprüfung wegen Zwei_ziehen
            switch (vorherigeSpielkarte.getBlattwert()) {
                case Sieben:
                    if (sindKartenZuZiehen && aktuelleSpielkarte.getBlattwert() != Blattwert.Sieben) {
                        istLegbar = false;
                    }
                    break;
            }
        }
        return istLegbar;
    }

    @Override
    public RegelComponentUtil holeAuswirkungVonKarte(Spielkarte aktuelleSpielkarte, List<Spieler> spielerListe, Spielrunde spielrunde) {
        if (aktuelleSpielkarte == null) {
            throw new TechnischeException("Aktuelle Spielkarte ist nicht initialisiert");
        } else if (spielerListe == null) {
            throw new TechnischeException("Spielerliste ist nicht initialisiert");
        }

        RegelComponentUtil util = null;
        switch (aktuelleSpielkarte.getBlattwert()) {
            case Sieben:
                util = super.holeAuswirkungVonKarte(aktuelleSpielkarte, spielerListe,  spielrunde);
                util.setAnzahlKartenZuZiehen(2+spielrunde.getZuZiehnKartenAnzahl());
                break;
            case Ass:
                int indexSpielend = 0;
                for (Spieler spieler : spielerListe) {
                    if (spieler.isSpielend()) {
                        indexSpielend = spielerListe.indexOf(spieler);
                        if(spielrunde.isUhrzeiger()) {
                            if (indexSpielend == spielerListe.size() - 1) {
                                spielerListe.get(1).setSpielend(true);
                            } else if (indexSpielend == spielerListe.size() - 2) {
                                spielerListe.get(0).setSpielend(true);
                            } else {
                                spielerListe.get(indexSpielend + 2).setSpielend(true);
                            }
                            break;
                        } else {
                            if (indexSpielend == 0) {
                                spielerListe.get(spielerListe.size()-2).setSpielend(true);
                            } else if (indexSpielend == 1) {
                                spielerListe.get(spielerListe.size()-1).setSpielend(true);
                            } else {
                                spielerListe.get(indexSpielend - 2).setSpielend(true);
                            }
                            break;
                        }
                    }
                }
                if (spielerListe.size() != 2) {
                    spielerListe.get(indexSpielend).setSpielend(false);
                }
                util = new RegelComponentUtil(spielerListe, 0);
                break;
            default:
                util = super.holeAuswirkungVonKarte(aktuelleSpielkarte, spielerListe, spielrunde);
        }
        return util;
    }

    public boolean pruefeObWuenscher(Spielkarte spielkarte) {
        if (spielkarte == null) {
            throw new TechnischeException("Spielkarte ist nicht initialisiert");
        }
        return spielkarte.getBlattwert() == Blattwert.Bube;
    }
}
