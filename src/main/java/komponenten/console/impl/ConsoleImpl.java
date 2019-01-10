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
import komponenten.spielverwaltung.export.Protokoll;
import komponenten.spielverwaltung.repositories.ProtokollRepository;

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

    @Autowired
    private ProtokollRepository protokollRepository;

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
                // Create Protokoll
                Protokoll protokoll = new Protokoll();
                protokoll.setSpielID(spiel.getIdentity());
                protokoll.setSpielrundeID(spielrunde.getIdentity());
                protokoll.setSpielerName(spieler.getName());
                protokoll.setSpielkarteDavor(spielrunde.getAufgelegtStapel().getSpielkarten().get(spielrunde.getAufgelegtStapel().getSpielkarten().size()-1).toString());
                protokoll.setRundefarbeDavor(spielrunde.getRundeFarbe());
                protokoll.setZieheKarteDavor(spielrunde.getZuZiehnKartenAnzahl());
                protokoll.setUhrzeigerDavor(spielrunde.isUhrzeiger());
                protokoll.setAnzahlKartenInHandDavor(spieler.getHand().size());

                consoleView.printZugDetails(spielrunde, spieler);
                if (!spieler.isVirtuellerSpieler()) {
                    consoleView.printHand(spieler);
                }

                boolean sollMauAufgerufen = spielsteuerung.sollMauMauAufrufen(spielrunde, spieler, gewaehlteSpielregel);

                boolean zugErfolgreich;

                String antwortPC = null;
                Spielkarte spielkarteVonPC = null;
                int kartenZuZiehen;
                String wahl = null;
                do {
                    kartenZuZiehen = spielrunde.getZuZiehnKartenAnzahl();
                    if (!spielsteuerung.checkeObSpielerAusgesetztWird(spielrunde, spieler, gewaehlteSpielregel)) {

                        if (!spieler.isVirtuellerSpieler()) {
                            wahl = consoleView.eingabeWaehlen(spieler);
                        } else {
                            wahl = virtuellerSpieler.spieleKarte(spielrunde, spieler, gewaehlteSpielregel);
                            antwortPC = wahl;
                            if (StringUtils.isNumeric(wahl)) {
                                spielkarteVonPC = spieler.getHand().get(Integer.valueOf(wahl));
                            }

                        }
                        zugErfolgreich = spieleWahl(wahl, spieler, spielrunde, sollMauAufgerufen, gewaehlteSpielregel);
                    } else {
                        consoleView.printMessage("### Sie können weder Karten spielen noch ziehen. Sie werden ausgesetzt ###");
                        zugErfolgreich = false;
                    }
                } while (!zugErfolgreich);

                // Protokoll nach erfolgreichen Auswahl
                protokoll.setRundefarbeDanach(spielrunde.getRundeFarbe());
                protokoll.setZieheKarteDanach(spielrunde.getZuZiehnKartenAnzahl());
                protokoll.setUhrzeigerDanach(spielrunde.isUhrzeiger());
                protokoll.setAnzahlKartenInHandDanach(spieler.getHand().size());
                if(StringUtils.isNumeric(wahl)) {
                    protokoll.setAuswahlSpieler(spielrunde.getAufgelegtStapel().getSpielkarten().get(spielrunde.getAufgelegtStapel().getSpielkarten().size()-1).toString());
                } else {
                    protokoll.setAuswahlSpieler(wahl);
                }

                if (spieler.isVirtuellerSpieler()) {
                    consoleView.printAntwortVirtuellerSpieler(antwortPC, spieler, spielkarteVonPC, kartenZuZiehen);
                }

                vollerHand = !spieler.getHand().isEmpty();


                spieler = spielsteuerung.fragWerDranIst(spielrunde.getSpielerListe());

                this.spielrundeRepository.save(spielrunde);

                this.protokollRepository.save(protokoll);
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
                Spielkarte karte = getKarteVomHand(spieler, wahl);
                boolean valid = spieleKarte(spieler, spielrunde, karte, gewaehlteSpielregel);
                boolean istWuenscher = spielsteuerung.pruefeObWuenscher(karte, gewaehlteSpielregel);
                if (valid) {
                    if (istWuenscher && !spieler.isVirtuellerSpieler()) {
                        spieleWuenscher(spielrunde);
                        return true;
                    } else if(istWuenscher && spieler.isVirtuellerSpieler()) {
                       Blatttyp blatttypVonPC= virtuellerSpieler.sucheBlatttypAus(spieler);
                       spielsteuerung.bestimmeBlatttyp(blatttypVonPC, spielrunde);
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

    private Spielkarte getKarteVomHand(Spieler spieler, String wahl) {
        Spielkarte spielkarte = null;

        spielkarte = spieler.getHand().get(Integer.parseInt(wahl));

        return spielkarte;
    }

    private void mauMauRufen(RegelKompTyp gewaehlteSpielregel, Spielrunde spielrunde, Spieler spieler) {
        consoleView.mauMauRufenMsg();
        Spielkarte spielkarte = null;

        spielkarte = spieler.getHand().get(0);

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
            consoleView.karteGezogenMsg(anzhalZiehen, spieler);
            spielsteuerung.zieheKartenVomStapel(spieler, 1, spielrunde);
        } else {
            consoleView.karteGezogenMsg(anzhalZiehen, spieler);
            spielsteuerung.zieheKartenVomStapel(spieler, anzhalZiehen, spielrunde);
        }
    }
}
