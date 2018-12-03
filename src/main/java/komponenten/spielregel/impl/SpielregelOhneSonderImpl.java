package komponenten.spielregel.impl;

import komponenten.spielregel.export.ISpielregel;
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
 * Implementierung vom spielregel-Komponent ohne Sonderregel
 */
@Service
@Qualifier("ohneSonder")
public class SpielregelOhneSonderImpl implements ISpielregel {


    public boolean istKarteLegbar(Spielkarte vorherigeSpielkarte, Spielkarte aktuelleSpielkarte, Blatttyp blatttyp) throws MauMauException {
        // TODO wie behandeln wir exceptions?
        if (vorherigeSpielkarte == null || aktuelleSpielkarte == null) {
            throw new MauMauException("Fehler");
        }
        boolean istLegbar = false;
        if (aktuelleSpielkarte.getBlatttyp() == vorherigeSpielkarte.getBlatttyp() ||
                aktuelleSpielkarte.getBlattwert() == vorherigeSpielkarte.getBlattwert()) {
            istLegbar = true;

        }
        return istLegbar;
    }


    public RegelComponentUtil holeAuswirkungVonKarte(Spielkarte aktuelleSpielkarte, List<Spieler> spielerListe) throws MauMauException {
        // TODO wie behandeln wir Exceptions?
        if (aktuelleSpielkarte == null || spielerListe == null) {
            throw new MauMauException("Fehler");
        }
        for (Spieler spieler : spielerListe) {
            if (spieler.isSpielend()) {
                int indexSpielend = spielerListe.indexOf(spieler);
                if (indexSpielend == spielerListe.size() - 1) {
                    spielerListe.get(0).setSpielend(true);
                } else {
                    spielerListe.get(indexSpielend + 1).setSpielend(true);
                }
                break;
            }
        }
        return new RegelComponentUtil(spielerListe, 0);
    }


    public boolean pruefeObWuenscher(Spielkarte spielkarte) throws MauMauException {
        // TODO wie behandeln wie exceptions
        if (spielkarte == null) {
            throw new MauMauException("Fehler");
        }
        return spielkarte.getBlattwert() == Blattwert.Bube;
    }
}
