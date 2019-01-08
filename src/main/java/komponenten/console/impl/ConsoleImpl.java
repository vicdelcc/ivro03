package komponenten.console.impl;


import komponenten.console.export.IConsole;
import komponenten.karten.export.Blatttyp;
import komponenten.karten.export.Spielkarte;
import komponenten.spielsteuerung.export.ISpielsteuerung;
import komponenten.spielverwaltung.export.*;
import komponenten.spielverwaltung.repositories.SpielRepository;
import komponenten.spielverwaltung.repositories.SpielrundeRepository;
import komponenten.virtuellerSpieler.export.IVirtuellerSpieler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ConsoleImpl implements IConsole {

    @Autowired
    private ISpielverwaltung spielverwaltung;

    @Autowired
    private ISpielsteuerung spielsteuerung;

    @Autowired
    private SpielrundeRepository spielrundeRepository;

    @Autowired
    private SpielRepository spielRepository;

    @Autowired
    private IVirtuellerSpieler virtuellerSpieler;

    static ConsoleView consoleView = new ConsoleView();

    @Override
    public void run() {

        boolean nochEineRunde = false;

        Spiel spiel = null;

        SpielTyp spielTyp;

        RegelKompTyp gewaehlteSpielregel = null;

        List<Spieler> spielerListe;

        boolean weiter = false;
        int spielID;
        do {
            spielID = consoleView.spielFortfuehren(weiter);
            if (spielID == 0) {
                spielTyp = consoleView.spielTypWahl();
                gewaehlteSpielregel = consoleView.regelWahl();
                spiel = spielverwaltung.starteNeuesSpiel(spielTyp, gewaehlteSpielregel);
                consoleView.zeigeSpielID(spiel);
            } else {
                try {
                    spiel = this.spielRepository.findById(Long.valueOf(spielID)).get();
                    weiter = false;
                    // Wenn alle Spielrunden schon beendet wurden, wird beim selben Spiel eine neue Spielrunde gestartet
                    boolean atLeastOneUnfinished = spiel.getSpielrunden().stream().map(Spielrunde::getGewinnerName).anyMatch(gewinnerName -> gewinnerName == null);
                    if (!atLeastOneUnfinished) {
                        consoleView.printMessage("### Spiel mit ID: " + spielID + " hat keine offene Spielrunden. Es wird eine neue Runde gestartet ###");
                        spielID = 0;
                        gewaehlteSpielregel = spiel.getRegelKompTyp();
                    }
                } catch (NoSuchElementException e) {
                    weiter = true;
                }
            }
        } while (weiter);


        do {
            Spielrunde spielrunde = null;

            Spieler spieler;

            // Neue Spielrunde wird angelegt, wenn es kein altes Spiel geladen wurde
            if (spielID == 0 || nochEineRunde) {

                spielerListe = consoleView.virtuellerSpielerAuswahl();

                spielerListe = consoleView.spielerEingabe(spielerListe);

                spielrunde = spielverwaltung.starteSpielrunde(spielerListe, spiel);


            } else {
                // Wenn ein altes Spiel geladen wurde,wird die Spielrunde gesucht, die nicht beendet wurde
                for (Spielrunde spielrundeDB : spiel.getSpielrunden()) {
                    if (spielrundeDB.getGewinnerName() == null) {
                        spielrunde = spielrundeDB;
                        gewaehlteSpielregel = spiel.getRegelKompTyp();
                    }
                }
            }

            spieler = spielsteuerung.fragWerDranIst(spielrunde.getSpielerListe());

            boolean vollerHand = false;

            do {

                if (!spieler.isVirtuellerSpieler()) {

                    consoleView.printZugDetails(spielrunde, spieler);
                }

                boolean sollMauAufgerufen = spielsteuerung.sollMauMauAufrufen(spielrunde, spieler, gewaehlteSpielregel);

                boolean zugErfolgreich;

                String antwortPC = null;
                Spielkarte spielkarteVonPC = null;
                do {
                    if (!spielsteuerung.checkeObSpielerAusgesetztWird(spielrunde, spieler, gewaehlteSpielregel)) {
                        String wahl = null;
                        if (!spieler.isVirtuellerSpieler()) {
                            wahl = consoleView.eingabeWaehlen(spieler, spielrunde);
                        } else {
                            for (Hand hand : spieler.getHands()) {
                                if (hand.getSpielrunde().getIdentity() == spielrunde.getIdentity()) {
                                    wahl = virtuellerSpieler.spieleKarte(spielrunde, hand, gewaehlteSpielregel);
                                    antwortPC = wahl;
                                    if (StringUtils.isNumeric(wahl)) {
                                        spielkarteVonPC = hand.getSpielkarten().get(Integer.valueOf(wahl));
                                    }
                                    break;
                                }
                            }
                        }
                        zugErfolgreich = spieleWahl(wahl, spieler, spielrunde, sollMauAufgerufen, gewaehlteSpielregel);
                    } else {
                        consoleView.printMessage("### Sie können weder Karten spielen noch ziehen. Sie werden ausgesetzt ###");
                        zugErfolgreich = false;
                    }
                } while (!zugErfolgreich);

                if (spieler.isVirtuellerSpieler()) {
                    consoleView.printAntwortVirtuellerSpieler(antwortPC, spieler, spielkarteVonPC);
                }
                for (Hand hand : spieler.getHands()) {
                    if (hand.getSpielrunde().getIdentity() == spielrunde.getIdentity()) {
                        vollerHand = !hand.getSpielkarten().isEmpty();
                    }
                }

                spieler = spielsteuerung.fragWerDranIst(spielrunde.getSpielerListe());

                this.spielrundeRepository.save(spielrunde);

            } while (vollerHand);


            spielrunde = spielverwaltung.beendeSpielrunde(spielrunde);

            consoleView.zeigeErgebnisse(spielrunde);

            nochEineRunde = consoleView.nochEineRunde();
        } while (nochEineRunde);

        spielverwaltung.beendeSpiel(spiel);

    }


    private boolean spieleWahl(String wahl,
                               Spieler spieler,
                               Spielrunde spielrunde,
                               boolean sollMauMauGerufen,
                               RegelKompTyp gewaehlteSpielregel) {

        if (sollMauMauGerufen) {
            if (wahl.equalsIgnoreCase("m")) {
                mauMauRufen(gewaehlteSpielregel, spielrunde, spieler);
                return true;
            } else {
                mauMauNichtGerufen(spieler, spielrunde);
                return true;
            }
        } else {
            if (wahl.equalsIgnoreCase("z")) {
                ziehKarte(spielrunde, spieler);
                return true;
            } else if (wahl.equalsIgnoreCase("m")) {
                consoleView.mauMauNichtAufrufenMsg();
                return false;
            } else {
                Spielkarte karte = getKarteVomHand(spieler, wahl, spielrunde);
                boolean valid = spieleKarte(spieler, spielrunde, karte, gewaehlteSpielregel);
                boolean istWuenscher = spielsteuerung.pruefeObWuenscher(karte, gewaehlteSpielregel);
                if (valid) {
                    if (istWuenscher) {
                        spieleWuenscher(spielrunde);
                        return true;
                    } else {
                        return true;
                    }
                } else {
                    consoleView.nichtLegbareKarteMsg();
                    return false;
                }
            }
        }
    }

    private void spieleWuenscher(Spielrunde spielrunde) {
        consoleView.printFarben();
        Blatttyp blatttyp = consoleView.farbeWaehlen();
        spielsteuerung.bestimmeBlatttyp(blatttyp, spielrunde);
    }

    private boolean spieleKarte(Spieler spieler,
                                Spielrunde spielrunde,
                                Spielkarte spielkarte,
                                RegelKompTyp gewaehlteSpielregel) {
        return spielsteuerung.spieleKarte(spieler, spielkarte, spielrunde, gewaehlteSpielregel);
    }

    private Spielkarte getKarteVomHand(Spieler spieler, String wahl, Spielrunde spielrunde) {
        Spielkarte spielkarte = null;
        for (Hand hand : spieler.getHands()) {
            if (hand.getSpielrunde().getIdentity() == spielrunde.getIdentity()) {
                spielkarte = hand.getSpielkarten().get(Integer.parseInt(wahl));
            }
        }
        return spielkarte;
    }

    private void mauMauRufen(RegelKompTyp gewaehlteSpielregel, Spielrunde spielrunde, Spieler spieler) {
        consoleView.mauMauRufenMsg();
        Spielkarte spielkarte = null;
        for (Hand hand : spieler.getHands()) {
            if (hand.getSpielrunde().getIdentity() == spielrunde.getIdentity()) {
                spielkarte = hand.getSpielkarten().get(0);
            }
        }
        boolean karteValid = spielsteuerung.spieleKarte(spieler, spielkarte, spielrunde, gewaehlteSpielregel);
        if (karteValid) {
            consoleView.spielBeendetMsg();
        } else {
            consoleView.nichtLegbareKarteMsg();
            ziehKarte(spielrunde, spieler);
        }
    }

    private void mauMauNichtGerufen(Spieler spieler, Spielrunde spielrunde) {
        consoleView.mauMauNichtgerufenMsg();
        ziehKarte(spielrunde, spieler);
    }

    private void ziehKarte(Spielrunde spielrunde, Spieler spieler) {
        int anzhalZiehen = spielsteuerung.checkZuZiehendenKarten(spielrunde);
        if (anzhalZiehen == 0) {
            consoleView.karteGezogenMsg(anzhalZiehen);
            spielsteuerung.zieheKartenVomStapel(spieler, 1, spielrunde);
        } else {
            spielsteuerung.zieheKartenVomStapel(spieler, anzhalZiehen, spielrunde);
        }
    }
}
