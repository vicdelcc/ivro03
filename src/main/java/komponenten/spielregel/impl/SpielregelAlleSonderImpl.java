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
 * Richtungswechsel(9), Stopper(8) und Allesleger(10)
 */
@Service
@Qualifier("alleSonder")
public class SpielregelAlleSonderImpl extends SpielregelBasicSonderImpl {


    @Override
    public boolean istKarteLegbar(Spielkarte vorherigeSpielkarte, Spielkarte aktuelleSpielkarte, Blatttyp blatttyp) throws MauMauException {
        // TODO wie behandeln wir exceptions?
        if (vorherigeSpielkarte == null || aktuelleSpielkarte == null) {
            throw new MauMauException("Fehler");
        }
        // Prüfung von BasicSonderRegel
        boolean istLegbar = super.istKarteLegbar(vorherigeSpielkarte, aktuelleSpielkarte, blatttyp);
        // Stopper
        if (vorherigeSpielkarte.getBlattwert() == Blattwert.Sieben && aktuelleSpielkarte.getBlattwert() == Blattwert.Acht) {
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
            case Neun:
                int indexSpielend = 0;
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
                spielerListe.get(indexSpielend).setSpielend(false);
                util = new RegelComponentUtil(spielerListe, 0);
                break;
            default:
                util = super.holeAuswirkungVonKarte(aktuelleSpielkarte, spielerListe);
        }


        return util;
    }
}
