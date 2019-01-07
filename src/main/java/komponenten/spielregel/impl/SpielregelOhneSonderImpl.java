package komponenten.spielregel.impl;

import komponenten.spielregel.export.ISpielregel;
import komponenten.spielverwaltung.export.Spieler;
import komponenten.karten.export.Spielkarte;
import komponenten.karten.export.Blatttyp;
import komponenten.karten.export.Blattwert;
import util.exceptions.TechnischeException;
import komponenten.spielregel.export.RegelComponentUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementierung vom spielregel-Komponent ohne Sonderregel
 */
@Service
@Qualifier("ohneSonder")
public class SpielregelOhneSonderImpl implements ISpielregel {


    public boolean istKarteLegbar(Spielkarte vorherigeSpielkarte, Spielkarte aktuelleSpielkarte, Blatttyp blatttyp, boolean sindKartenZuZiehen) {
        if (vorherigeSpielkarte == null) {
            throw new TechnischeException("Vorherige Spielkarte ist nicht initialisiert");
        } else if (aktuelleSpielkarte == null) {
            throw new TechnischeException("Aktuelle Spielkarte ist nicht initialisiert");
        }
        boolean istLegbar = false;
        if (aktuelleSpielkarte.getBlatttyp() == vorherigeSpielkarte.getBlatttyp() ||
                aktuelleSpielkarte.getBlattwert() == vorherigeSpielkarte.getBlattwert()) {
            istLegbar = true;

        }
        return istLegbar;
    }


    public RegelComponentUtil holeAuswirkungVonKarte(Spielkarte aktuelleSpielkarte, List<Spieler> spielerListe, int anzahlZuZiehendenKarten) {
        if (aktuelleSpielkarte == null) {
            throw new TechnischeException("Aktuelle Spielkarte ist nicht initialisiert");
        } else if (spielerListe == null) {
            throw new TechnischeException("Spielerliste ist nicht initialisiert");
        }
        for (Spieler spieler : spielerListe) {
            if (spieler.isSpielend()) {
                int indexSpielend = spielerListe.indexOf(spieler);
                if (indexSpielend == spielerListe.size() - 1) {
                    spielerListe.get(0).setSpielend(true);
                } else {
                    spielerListe.get(indexSpielend + 1).setSpielend(true);
                }
                spielerListe.get(indexSpielend).setSpielend(false);
                break;
            }
        }
        return new RegelComponentUtil(spielerListe, 0);
    }


    public boolean pruefeObWuenscher(Spielkarte spielkarte) {
        if (spielkarte == null) {
            throw new TechnischeException("Spielkarte ist nicht initialisiert");
        }
        return spielkarte.getBlattwert() == Blattwert.Bube;
    }
}
