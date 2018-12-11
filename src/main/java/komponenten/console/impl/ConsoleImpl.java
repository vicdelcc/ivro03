package komponenten.console.impl;


import komponenten.console.export.IConsole;
import komponenten.spielsteuerung.export.ISpielsteuerung;
import komponenten.spielverwaltung.export.ISpielverwaltung;
import model.Spiel;
import model.Spieler;
import model.Spielkarte;
import model.Spielrunde;
import model.enums.Blatttyp;
import model.enums.RegelKompTyp;
import model.enums.SpielTyp;
import model.exceptions.FachlicheException;
import model.exceptions.MauMauException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Scanner;

@Service
public class ConsoleImpl implements IConsole {

    @Autowired
    private ISpielverwaltung spielverwaltung;

    @Autowired
    private ISpielsteuerung spielsteuerung;

    static Scanner sc = new Scanner(System.in);

    static ConsoleView consoleView = new ConsoleView();

    @Override
    public void run() throws MauMauException {

        SpielTyp spielTyp = consoleView.spielTypWahl(sc);

        RegelKompTyp gewaehlteSpielregel = consoleView.regelWahl(sc);

        Spiel spiel = spielverwaltung.starteNeuesSpiel(spielTyp, gewaehlteSpielregel);

        ArrayList<Spieler> spielerListe = consoleView.spielerEingabe(sc);

        Spielrunde spielrunde = spielverwaltung.starteSpielrunde(spielerListe, spiel);

        Spieler spieler = spielsteuerung.fragWerDranIst(spielrunde.getSpielerListe());

        boolean vollerHand = false;

        do{

            consoleView.printZugDetails(spielrunde, spieler);

            String wahl = consoleView.eingabgeWaehlen(sc, spieler);

            boolean sollMauAufgerufen = spielsteuerung.sollMauMauAufrufen(spieler);

            boolean sollKartenZiehen = spielsteuerung.checkZuZiehendenKarten(spielrunde) > 0;

            //Benutzer hat 1 Karte und MauMau gerufen
            if(wahl.toLowerCase().equals("m") && sollMauAufgerufen){
                System.out.println("Mau Mau!");
                Spielkarte spielkarte = spieler.getHand().get(0);
                boolean karteValid = spielsteuerung.spieleKarte(spieler, spielkarte, spielrunde, gewaehlteSpielregel);
                if(karteValid){
                    System.out.println("### Spiel beendet! ###");
                } else {
                    System.out.println("### Karte kann nicht gelegt werden! Eine Karte wurde gezogen ###");
                    spielsteuerung.zieheKartenVomStapel(spieler,1,spielrunde);
                }
            }

            //Benutzer hat 1 Karte aber hat MauMau nicht gerufen
            if(!wahl.toLowerCase().equals("m") && sollMauAufgerufen){
                System.out.println("### Sie haben MauMau nicht gerufen. Sie bekommen eine Karte ###");
                spielsteuerung.zieheKartenVomStapel(spieler,1,spielrunde);
            }

            //Benutzer hat mehr als 1 Karte und MauMau gerufen
            if(wahl.toLowerCase().equals("m") && !sollMauAufgerufen){
                do{
                    System.out.println("### Sie haben mehr als eine Karte im Hand, Bitte wählen Sie 'z' oder eine Zahl ###");
                    wahl = consoleView.eingabgeWaehlen(sc, spieler);
                } while (wahl.equalsIgnoreCase("m"));
            }

            //Benutzer hat gewählt eine Karte zu ziehen
            if(wahl.toLowerCase().equals("z")){
                int anzhalZiehen = spielsteuerung.checkZuZiehendenKarten(spielrunde);
                if(anzhalZiehen==0){
                    System.out.println("### Eine Karte wurde gezogen ###");
                    spielsteuerung.zieheKartenVomStapel(spieler,1,spielrunde);
                } else {
                    System.out.println("### " + anzhalZiehen+" Karten wurden gezogen ###");
                    spielsteuerung.zieheKartenVomStapel(spieler,anzhalZiehen,spielrunde);
                }
            }



//                int anzhalZiehen = spielsteuerung.checkZuZiehendenKarten(spielrunde);
//                wahl = consoleView.eingabgeWaehlen(sc, spieler);
//                if(wahl.equalsIgnoreCase("z")){
//                    System.out.println("### " +anzhalZiehen+" Karten wurden gezogen ###");
//                    spielsteuerung.zieheKartenVomStapel(spieler,anzhalZiehen,spielrunde);
//                }


            //Benutzer spielt eine Karte - int kann nicht out of bounds sein
            if(StringUtils.isNumeric(wahl) && !sollMauAufgerufen) {
                Spielkarte spielkarte = spieler.getHand().get(Integer.parseInt(wahl));
                boolean istWuenscher = spielsteuerung.pruefeObWuenscher(spielkarte, gewaehlteSpielregel);
                boolean karteValid = spielsteuerung.spieleKarte(spieler, spielkarte, spielrunde, gewaehlteSpielregel);

                if (!karteValid) {
                    do {
                        System.out.println("### Die Karte kann nicht aufgelegt werden! Spielen Sie eine andere Karte ###");
                        consoleView.printZugDetails(spielrunde, spieler);
                        wahl = consoleView.eingabgeWaehlen(sc, spieler);
                        spielkarte = spieler.getHand().get(Integer.parseInt(wahl));
                        karteValid = spielsteuerung.spieleKarte(spieler, spielkarte, spielrunde, gewaehlteSpielregel);
                    } while (!karteValid);
                }

                if (istWuenscher) {
                    consoleView.printFarben();
                    Blatttyp blatttyp = consoleView.farbeWawhlen(sc);
                    spielsteuerung.bestimmeBlatttyp(blatttyp, spielrunde);
                }
            }

            vollerHand = !spieler.getHand().isEmpty();

            spieler = spielsteuerung.fragWerDranIst(spielrunde.getSpielerListe());

        } while(vollerHand);


//        spielsteuerung.spieleKarte(spieler, new Spielkarte(Blattwert.Bube, Blatttyp.Herz), spielrunde);






//        spielVerwaltung.beendeSpielrunde(spielrunde);
//
//        spielVerwaltung.beendeSpiel(spiel);

    }
}
