package komponenten.console.impl;


import komponenten.console.export.IConsole;
import komponenten.spielsteuerung.export.ISpielsteuerung;
import komponenten.spielverwaltung.export.ISpielverwaltung;
import model.Spiel;
import model.Spieler;
import model.Spielkarte;
import model.Spielrunde;
import model.enums.RegelKompTyp;
import model.enums.SpielTyp;
import model.exceptions.MauMauException;
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

    static ConsoleUtil consoleUtil = new ConsoleUtil();

    @Override
    public void run() throws MauMauException {

        SpielTyp spielTyp = consoleUtil.spielTypWahl(sc);

        RegelKompTyp gewaehlteSpielregel = consoleUtil.regelWahl(sc);

        Spiel spiel = spielverwaltung.starteNeuesSpiel(spielTyp, gewaehlteSpielregel);

        ArrayList<Spieler> spielerListe = consoleUtil.spielerEingabe(sc);

        Spielrunde spielrunde = spielverwaltung.starteSpielrunde(spielerListe, spiel);

        // TODO ADDED --> Regeltyp wird über eine neue Methode gesetzt
        spielsteuerung.setzteSpielregelTyp(gewaehlteSpielregel);

        Spieler spieler = spielsteuerung.fragWerDranIst(spielrunde.getSpielerListe());

        do{

            consoleUtil.printZugDetails(spielrunde, spieler);

            String wahl;
            do{
                wahl = sc.nextLine();
                if(!consoleUtil.istEingabeRichtig(wahl, spieler.getHand().size())){
                    System.out.println("Die Eingabe war false! Bitte geben Sie 'm','z' oder eine Zahl");
                }
            } while (!consoleUtil.istEingabeRichtig(wahl, spieler.getHand().size()));

            boolean sollMauAufgerufen = spielsteuerung.sollMauMauAufrufen(spieler);

            if(wahl.equals("m")){
                System.out.println("Mau Mau!");
            }

            if (sollMauAufgerufen) {
                if(wahl.toLowerCase().equals("m")){
                    Spielkarte spielkarte = spieler.getHand().get(0);
                    boolean karteValid = spielsteuerung.spieleKarte(spieler, spielkarte, spielrunde);
                    if(karteValid){
                        System.out.println("Spiel beendet!");
                    } else {
                        System.out.println("Karte köönte nicht geegt werden! Eine Karte wurde gezogen");
                        spielsteuerung.zieheKartenVomStapel(spieler,1,spielrunde);
                    }
                } else {
                    System.out.println("Sie haben MauMau nicht gerufen. Sie bekommen eine Karte");
                    spielsteuerung.zieheKartenVomStapel(spieler,1,spielrunde);
                }
            } else if (wahl.toLowerCase().equals("z")) {
                int anzhalZiehen = spielsteuerung.checkZuZiehendenKarten(spielrunde);
                if(anzhalZiehen==0){
                    System.out.println("Eine Karte wurde gezogen");
                    spielsteuerung.zieheKartenVomStapel(spieler,1,spielrunde);
                } else {
                    System.out.println(anzhalZiehen+" Karten wurden gezogen");
                    spielsteuerung.zieheKartenVomStapel(spieler,anzhalZiehen,spielrunde);
                }
            } else {
                //TODO Prüf ob ZweiZiehen
                //TODO if karte Wünscher
                Spielkarte spielkarte = spieler.getHand().get(Integer.parseInt(wahl));
                boolean karteValid = spielsteuerung.spieleKarte(spieler, spielkarte, spielrunde);

                if(!karteValid || Integer.parseInt(wahl) > spieler.getHand().size()){
                    do{
                        System.out.println("Die Karte Könnte nicht aufgelegt werden! Spielen Sie eine andere Karte");
                        wahl = sc.nextLine();
                        spielkarte = spieler.getHand().get(Integer.parseInt(wahl));
                        karteValid = spielsteuerung.spieleKarte(spieler, spielkarte, spielrunde);
                    } while (!karteValid);
                }
            }

            spieler = spielsteuerung.fragWerDranIst(spielrunde.getSpielerListe());
        } while(!spieler.getHand().isEmpty());


//        spielsteuerung.spieleKarte(spieler, new Spielkarte(Blattwert.Bube, Blatttyp.Herz), spielrunde);






//        spielVerwaltung.beendeSpielrunde(spielrunde);
//
//        spielVerwaltung.beendeSpiel(spiel);

    }
}
