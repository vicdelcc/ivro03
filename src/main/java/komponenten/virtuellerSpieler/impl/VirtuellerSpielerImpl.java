package komponenten.virtuellerSpieler.impl;

import komponenten.karten.export.Blatttyp;
import komponenten.karten.export.Spielkarte;
import komponenten.spielregel.export.ISpielregel;
import komponenten.spielregel.export.RegelComponentUtil;
import komponenten.spielverwaltung.export.RegelKompTyp;
import komponenten.spielverwaltung.export.Spieler;
import komponenten.spielverwaltung.export.Spielrunde;
import komponenten.virtuellerSpieler.export.IVirtuellerSpieler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import util.exceptions.TechnischeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    public String spieleKarte(Spielrunde spielrunde, Spieler spieler, RegelKompTyp gewaehlteSpielregel) {

        if(spielrunde == null) {
            throw new TechnischeException("Spielrunde ist nicht initialisiert");
        } else if(spieler == null) {
            throw new TechnischeException("Spieler nicht initialisiert");
        } else if(gewaehlteSpielregel == null) {
            throw new TechnischeException("Spieltyp nicht initialisiert");
        }

        String antwort;

        int gespielteKarteIndex = -1;

        ISpielregel gewaehlteSpielRegelK = holeImpl(gewaehlteSpielregel);

        // Nimmt die 1. spielbare Karte
        for (Spielkarte spielkarte : spieler.getHand()) {
            if (gewaehlteSpielRegelK.istKarteLegbar(spielrunde.getAufgelegtStapel().getSpielkarten().get(spielrunde.getAufgelegtStapel().getSpielkarten().size() - 1), spielkarte, spielrunde.getRundeFarbe(), spielrunde.getZuZiehnKartenAnzahl() != 0)) {
                gespielteKarteIndex = spieler.getHand().indexOf(spielkarte);
                break;
            }
        }
        // Falls es keine Karte spielbar ist, dann soll kartenZiehen
        if (gespielteKarteIndex == -1) {
            antwort = "z";
        } else {
            antwort = String.valueOf(gespielteKarteIndex);
            // Falls der virtuelle Spieler nur eine Spielkarte hat, dann soll maumauAufrufen
            if (spieler.getHand().size() == 1) {
                antwort = "m";
            }
        }
        return antwort;
    }

    @Override
    public Blatttyp sucheBlatttypAus(Spieler spieler) {
        if(spieler == null) {
            throw new TechnischeException("Spieler ist nicht initialisiert");
        }
        // Alle Blatttypen vom Spieler speichern
        List<Blatttyp> blattypenInHand = new ArrayList<>();
        for (Spielkarte spielkarte : spieler.getHand()) {
            if (!blattypenInHand.contains(spielkarte.getBlatttyp())) {
                blattypenInHand.add(spielkarte.getBlatttyp());
            }
        }
        int indexBlatttyp = new Random().nextInt(blattypenInHand.size());
        return blattypenInHand.get(indexBlatttyp);
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
