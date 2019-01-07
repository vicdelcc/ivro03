package komponenten.console.impl;


import komponenten.console.export.IConsole;
import komponenten.karten.export.Blatttyp;
import komponenten.karten.export.Spielkarte;
import komponenten.spielsteuerung.export.ISpielsteuerung;
import komponenten.spielverwaltung.export.*;
import komponenten.spielverwaltung.repositories.SpielRepository;
import komponenten.spielverwaltung.repositories.SpielrundeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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

    static ConsoleView consoleView = new ConsoleView();

    @Override
    public void run() {

        boolean nochEineRunde = false;

        Spiel spiel = null;

        SpielTyp spielTyp = null;

        RegelKompTyp gewaehlteSpielregel = null;

        ArrayList<Spieler> spielerListe = new ArrayList<>();

        int spielID = consoleView.spielFortfuehren();

        if (spielID == 0) {
            spielTyp = consoleView.spielTypWahl();

            gewaehlteSpielregel = consoleView.regelWahl();

            spiel = spielverwaltung.starteNeuesSpiel(spielTyp, gewaehlteSpielregel);


        } else {
            spiel = this.spielRepository.findById(Long.valueOf(spielID)).get();
        }

        consoleView.zeigeSpielID(spiel);

        do {
            Spielrunde spielrunde = null;

            Spieler spieler = null;

            // Neue Spielrunde wird angelegt, wenn es kein altes Spiel geladen wurde
            if (spielID == 0 || nochEineRunde) {

                spielerListe = consoleView.spielerEingabe();

                spielrunde = spielverwaltung.starteSpielrunde(spielerListe, spiel);

                spieler = spielsteuerung.fragWerDranIst(spielrunde.getSpielerListe());
            } else {
                // TODO
            }

            boolean vollerHand = false;

            do {

                consoleView.printZugDetails(spielrunde, spieler);

                boolean sollMauAufgerufen = spielsteuerung.sollMauMauAufrufen(spielrunde, spieler, gewaehlteSpielregel);

                boolean zugErfolgreich = false;

                do {
                    if (!spielsteuerung.checkeObSpielerAusgesetztWird(spielrunde, spieler, gewaehlteSpielregel)) {
                        String wahl = consoleView.eingabeWaehlen(spieler, spielrunde);
                        zugErfolgreich = spieleWahl(wahl, spieler, spielrunde, sollMauAufgerufen, gewaehlteSpielregel);
                    } else {
                        consoleView.printMessage("### Sie können weder Karten spielen noch ziehen. Sie werden ausgesetzt ###");
                        zugErfolgreich = false;
                    }
                } while (!zugErfolgreich);

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
