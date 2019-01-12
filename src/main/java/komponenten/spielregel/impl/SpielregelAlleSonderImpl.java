package komponenten.spielregel.impl;

import komponenten.spielverwaltung.export.Spieler;
import komponenten.karten.export.Spielkarte;
import komponenten.karten.export.Blatttyp;
import komponenten.karten.export.Blattwert;
import komponenten.spielverwaltung.export.Spielrunde;
import util.exceptions.TechnischeException;
import komponenten.spielregel.export.RegelComponentUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementierung von spielregel-Komponent mit Zwei_ziehen(7), Aussetzen(Ass) und Wünscher(Bube)
 * Richtungswechsel(9), Stopper(8) und Allesleger(10)
 */
@Service
@Qualifier("alleSonder")
public class SpielregelAlleSonderImpl extends SpielregelBasicSonderImpl {

    @Override
    public boolean istKarteLegbar(Spielkarte vorherigeSpielkarte, Spielkarte aktuelleSpielkarte, Blatttyp blatttyp, boolean sindKartenZuZiehen) {
        if (vorherigeSpielkarte == null) {
            throw new TechnischeException("Vorherige Spielkarte ist nicht initialisiert");
        } else if (aktuelleSpielkarte == null) {
            throw new TechnischeException("Aktuelle Spielkarte ist nicht initialisiert");
        }
        // Prüfung von BasicSonderRegel
        boolean istLegbar = super.istKarteLegbar(vorherigeSpielkarte, aktuelleSpielkarte, blatttyp, sindKartenZuZiehen);
        // Stopper
        if (sindKartenZuZiehen && vorherigeSpielkarte.getBlattwert() == Blattwert.Sieben && aktuelleSpielkarte.getBlattwert() == Blattwert.Acht) {
            istLegbar = true;
        }
        // Allesleger (10) darf nur auf nicht Funktionskarte gelegt werden
        if ((vorherigeSpielkarte.getBlattwert() == Blattwert.Sieben ||
                vorherigeSpielkarte.getBlattwert() == Blattwert.Bube ||
                vorherigeSpielkarte.getBlattwert() == Blattwert.Neun ||
                vorherigeSpielkarte.getBlattwert() == Blattwert.Acht ||
                vorherigeSpielkarte.getBlattwert() == Blattwert.Zehn)
                && aktuelleSpielkarte.getBlattwert() == Blattwert.Zehn) {
            istLegbar = false;
        } else if (aktuelleSpielkarte.getBlattwert() == Blattwert.Zehn) {
            istLegbar = true;
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
            case Neun:
                int indexSpielend = 0;
                if(spielrunde.isUhrzeiger()) {
                    if (spielerListe.size() != 2) {
                        for (Spieler spieler : spielerListe) {
                            if (spieler.isSpielend()) {
                                indexSpielend = spielerListe.indexOf(spieler);
                                if (indexSpielend == 0) {
                                    spielerListe.get(spielerListe.size() - 1).setSpielend(true);
                                } else {
                                    spielerListe.get(indexSpielend - 1).setSpielend(true);
                                }
                                break;
                            }
                        }
                        if (spielerListe.size() != 2) {
                            spielerListe.get(indexSpielend).setSpielend(false);
                        }
                    }
                } else {
                    if (spielerListe.size() != 2) {
                        for (Spieler spieler : spielerListe) {
                            if (spieler.isSpielend()) {
                                indexSpielend = spielerListe.indexOf(spieler);
                                if (indexSpielend == spielerListe.size()-1) {
                                    spielerListe.get(0).setSpielend(true);
                                } else {
                                    spielerListe.get(indexSpielend +1).setSpielend(true);
                                }
                                break;
                            }
                        }
                        if (spielerListe.size() != 2) {
                            spielerListe.get(indexSpielend).setSpielend(false);
                        }
                    }
                }

                util = new RegelComponentUtil(spielerListe, 0);
                break;
            case Acht:
                spielrunde.setZuZiehnKartenAnzahl(0);
                util = super.holeAuswirkungVonKarte(aktuelleSpielkarte, spielerListe, spielrunde);
                break;
            default:
                util = super.holeAuswirkungVonKarte(aktuelleSpielkarte, spielerListe, spielrunde);
        }
        return util;
    }
}
