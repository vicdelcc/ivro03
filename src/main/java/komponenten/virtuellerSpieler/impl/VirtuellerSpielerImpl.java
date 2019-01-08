package komponenten.virtuellerSpieler.impl;

import komponenten.karten.export.Spielkarte;
import komponenten.spielregel.export.ISpielregel;
import komponenten.spielregel.export.RegelComponentUtil;
import komponenten.spielverwaltung.export.Hand;
import komponenten.spielverwaltung.export.RegelKompTyp;
import komponenten.spielverwaltung.export.Spielrunde;
import komponenten.virtuellerSpieler.export.IVirtuellerSpieler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import util.exceptions.TechnischeException;

/**
 * Komponent, der ein virtueller Spieler realisiert
 */
@Service
public class VirtuellerSpielerImpl implements IVirtuellerSpieler {

    @Autowired
    @Qualifier("ohneSonder")
    private ISpielregel spielregelohneSonder;

    @Autowired
    @Qualifier("basicSonder")
    private ISpielregel spielregelBasicSonder;

    @Autowired
    @Qualifier("alleSonder")
    private ISpielregel spielregelAlleSonder;

    @Override
    public String spieleKarte(Spielrunde spielrunde, Hand hand, RegelKompTyp gewaehlteSpielregel) {

        String antwort = null;

        RegelComponentUtil regelComponentUtil;

        int gespielteKarteIndex = -1;

        Spielkarte gespielteKarte = null;

        ISpielregel gewaehlteSpielRegelK = holeImpl(gewaehlteSpielregel);

        // Nimmt die 1. spielbare Karte
        for (Spielkarte spielkarte : hand.getSpielkarten()) {
            if (gewaehlteSpielRegelK.istKarteLegbar(spielrunde.getAufgelegtStapel().getSpielkarten().get(spielrunde.getAufgelegtStapel().getSpielkarten().size() - 1), spielkarte, spielrunde.getRundeFarbe(), spielrunde.getZuZiehnKartenAnzahl() != 0)) {
                gespielteKarte = spielkarte;
                gespielteKarteIndex = hand.getSpielkarten().indexOf(spielkarte);
                break;
            }
        }
        // Falls es keine Karte spielbar ist, dann soll kartenZiehen
        if (gespielteKarteIndex == -1) {
            antwort = "z";
        } else {
            antwort = String.valueOf(gespielteKarteIndex);
            // Falls der virtuelle Spieler nur eine Spielkarte hat, dann soll maumauAufrufen
            if (hand.getSpielkarten().size() == 1) {
                antwort = "m";
            }
        }
        return antwort;
    }

    private ISpielregel holeImpl(RegelKompTyp gewaehlteSpielregel) {
        switch (gewaehlteSpielregel) {
            case OHNE_SONDER_REGEL:
                return spielregelohneSonder;
            case MIT_BASIC_SONDER_REGEL:
                return spielregelBasicSonder;
            case ALLE_SONDER_REGEL:
                return spielregelAlleSonder;
            default:
                throw new TechnischeException("unbekannte SpielRegelKomponente wurde übergeben");
        }
    }

}
