package komponenten.spielregel.impl;

import model.Spieler;
import model.Spielkarte;
import model.enums.Blatttyp;
import model.enums.Blattwert;
import model.exceptions.MauMauException;
import model.hilfsklassen.RegelComponentUtil;
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
    public boolean istKarteLegbar(Spielkarte vorherigeSpielkarte, Spielkarte aktuelleSpielkarte, Blatttyp blatttyp) throws MauMauException {
        // TODO wie behandeln wir exceptions?
        if (vorherigeSpielkarte == null || aktuelleSpielkarte == null) {
            throw new MauMauException("Fehler");
        }
        // Basic-Prüfung ohne SonderRegel
        boolean istLegbar = super.istKarteLegbar(vorherigeSpielkarte, aktuelleSpielkarte, blatttyp);
        // Wenn Blatttyp im Zug davor gewählt, dann kann man nur Blattyp spielen aber nicht noch eine Bube
        if (blatttyp != null) {
            if (aktuelleSpielkarte.getBlattwert() == Blattwert.Bube) {
                istLegbar = false;
            } else if(aktuelleSpielkarte.getBlatttyp() == blatttyp) {
                istLegbar = true;
            }
        } else {
            // Bube darf man auf jede Farbe spielen (also wenn keine Bube der sieben davor gespielt wurde)
            if(aktuelleSpielkarte.getBlattwert() == Blattwert.Bube) {
                istLegbar = true;
            }
            // Sonderprüfung wegen Zwei_ziehen
            switch (vorherigeSpielkarte.getBlattwert()) {
                case Sieben:
                    if(aktuelleSpielkarte.getBlattwert() != Blattwert.Sieben) {
                        istLegbar = false;
                    }
                    break;
                case Ass:
                    istLegbar = false;
                    break;
            }
        }
        return istLegbar;
    }

    @Override
    public RegelComponentUtil holeAuswirkungVonKarte(Spielkarte aktuelleSpielkarte, List<Spieler> spielerListe) throws MauMauException {
        // TODO wie behandeln wir Exceptions?
        if (aktuelleSpielkarte == null || spielerListe == null) {
            throw new MauMauException("Fehler");
        }


        RegelComponentUtil util = null;
        switch (aktuelleSpielkarte.getBlattwert()) {
            case Sieben:
                util = super.holeAuswirkungVonKarte(aktuelleSpielkarte, spielerListe);
                util.setAnzahlKartenZuZiehen(2);
                break;
            case Ass:
//                if(spielerListe.size() == 2){
//                    util = new RegelComponentUtil(spielerListe, 0);
//                } else {
                    int indexSpielend = 0;
                    for (Spieler spieler : spielerListe) {
                        if (spieler.isSpielend()) {
                            indexSpielend = spielerListe.indexOf(spieler);
                            if (indexSpielend == spielerListe.size() - 1) {
                                spielerListe.get(1).setSpielend(true);
                            } else if (indexSpielend == spielerListe.size() - 2) {
                                spielerListe.get(0).setSpielend(true);
                            } else {
                                spielerListe.get(indexSpielend + 2).setSpielend(true);
                            }
                            break;
                        }
                    }
                    if(spielerListe.size() != 2) {
                        spielerListe.get(indexSpielend).setSpielend(false);
                    }
                    util = new RegelComponentUtil(spielerListe, 0);
//                }
                break;
            default:
                util = super.holeAuswirkungVonKarte(aktuelleSpielkarte, spielerListe);
        }
        return util;
    }
}
