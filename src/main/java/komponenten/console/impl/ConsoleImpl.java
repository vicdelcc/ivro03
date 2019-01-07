package komponenten.console.impl;


import komponenten.console.export.IConsole;
import komponenten.spielsteuerung.export.ISpielsteuerung;
import komponenten.spielverwaltung.export.ISpielverwaltung;
import komponenten.spielverwaltung.export.Spiel;
import komponenten.spielverwaltung.export.Spieler;
import komponenten.karten.export.Spielkarte;
import komponenten.spielverwaltung.export.Spielrunde;
import komponenten.karten.export.Blatttyp;
import komponenten.spielverwaltung.export.RegelKompTyp;
import komponenten.spielverwaltung.export.SpielTyp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ConsoleImpl implements IConsole {

    @Autowired
    private ISpielverwaltung spielverwaltung;

    @Autowired
    private ISpielsteuerung spielsteuerung;

    static ConsoleView consoleView = new ConsoleView();

    @Override
    public void run() {

        SpielTyp spielTyp = consoleView.spielTypWahl();

        RegelKompTyp gewaehlteSpielregel = consoleView.regelWahl();

        Spiel spiel = spielverwaltung.starteNeuesSpiel(spielTyp, gewaehlteSpielregel);

        ArrayList<Spieler> spielerListe = consoleView.spielerEingabe();

        do {

            Spielrunde spielrunde = spielverwaltung.starteSpielrunde(spielerListe, spiel);

            Spieler spieler = spielsteuerung.fragWerDranIst(spielrunde.getSpielerListe());

            boolean vollerHand;

            do {

                consoleView.printZugDetails(spielrunde, spieler);

                boolean sollMauAufgerufen = spielsteuerung.sollMauMauAufrufen(spielrunde, spieler, gewaehlteSpielregel);

                boolean zugErfolgreich = false;

                do {
                    if (!spielsteuerung.checkeObSpielerAusgesetztWird(spielrunde, spieler, gewaehlteSpielregel)) {
                        String wahl = consoleView.eingabgeWaehlen(spieler);
                        zugErfolgreich = spieleWahl(wahl, spieler, spielrunde, sollMauAufgerufen, gewaehlteSpielregel);
                    } else {
                        consoleView.printMessage("### Sie können weder Karten spielen noch ziehen. Sie werden ausgesetzt ###");
                        zugErfolgreich = false;
                    }
                } while (!zugErfolgreich);

                vollerHand = !spieler.getHand().isEmpty();

                spieler = spielsteuerung.fragWerDranIst(spielrunde.getSpielerListe());

            } while (vollerHand);


            spielrunde = spielverwaltung.beendeSpielrunde(spielrunde);

            consoleView.zeigeErgebnisse(spielrunde);

        } while (consoleView.nochEineRunde());

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
        Blatttyp blatttyp = consoleView.farbeWawhlen();
        spielsteuerung.bestimmeBlatttyp(blatttyp, spielrunde);
    }

    private boolean spieleKarte(Spieler spieler,
                                Spielrunde spielrunde,
                                Spielkarte spielkarte,
                                RegelKompTyp gewaehlteSpielregel) {
        return spielsteuerung.spieleKarte(spieler, spielkarte, spielrunde, gewaehlteSpielregel);
    }

    private Spielkarte getKarteVomHand(Spieler spieler, String wahl) {
        return spieler.getHand().get(Integer.parseInt(wahl));
    }

    private void mauMauRufen(RegelKompTyp gewaehlteSpielregel, Spielrunde spielrunde, Spieler spieler) {
        consoleView.mauMauRufenMsg();
        Spielkarte spielkarte = spieler.getHand().get(0);
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
