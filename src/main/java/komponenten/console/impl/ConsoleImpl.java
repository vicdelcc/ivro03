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
import model.exceptions.FachlicheException;
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

    static ConsoleView consoleView = new ConsoleView();

    @Override
    public void run() throws MauMauException {

        SpielTyp spielTyp = consoleView.spielTypWahl(sc);

        RegelKompTyp gewaehlteSpielregel = consoleView.regelWahl(sc);

        Spiel spiel = spielverwaltung.starteNeuesSpiel(spielTyp, gewaehlteSpielregel);

        ArrayList<Spieler> spielerListe = consoleView.spielerEingabe(sc);

        Spielrunde spielrunde = spielverwaltung.starteSpielrunde(spielerListe, spiel);

        Spieler spieler = spielsteuerung.fragWerDranIst(spielrunde.getSpielerListe());

        boolean vollerHand;

        do{

            consoleView.printZugDetails(spielrunde, spieler);

            String wahl;
            do{
                wahl = sc.nextLine();
                if(!consoleView.istEingabeRichtig(wahl, spieler.getHand().size())){
                    System.out.println("Die Eingabe war false! Bitte geben Sie 'm','z' oder eine Zahl");
                }
            } while (!consoleView.istEingabeRichtig(wahl, spieler.getHand().size()));

            boolean sollMauAufgerufen = spielsteuerung.sollMauMauAufrufen(spieler);

            if(wahl.equals("m")){
                System.out.println("Mau Mau!");
            }

            if (sollMauAufgerufen) {
                if(wahl.toLowerCase().equals("m")){
                    Spielkarte spielkarte = spieler.getHand().get(0);
                    boolean karteValid = spielsteuerung.spieleKarte(spieler, spielkarte, spielrunde, gewaehlteSpielregel);
                    if(karteValid){
                        System.out.println("Spiel beendet!");
                    } else {
                        System.out.println("Karte kann nicht gelegt werden! Eine Karte wurde gezogen");
                        spielsteuerung.zieheKartenVomStapel(spieler,1,spielrunde);
                    }
                } else {
                    spielsteuerung.zieheKartenVomStapel(spieler,1,spielrunde);
                    throw new FachlicheException("Sie haben MauMau nicht gerufen. Sie bekommen eine Karte");
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
                boolean karteValid = spielsteuerung.spieleKarte(spieler, spielkarte, spielrunde, gewaehlteSpielregel);

                if(!karteValid || Integer.parseInt(wahl) > spieler.getHand().size()){
                    do{
                        System.out.println("Die Karte kann nicht aufgelegt werden! Spielen Sie eine andere Karte");
                        wahl = sc.nextLine();
                        spielkarte = spieler.getHand().get(Integer.parseInt(wahl));
                        karteValid = spielsteuerung.spieleKarte(spieler, spielkarte, spielrunde, gewaehlteSpielregel);
                    } while (!karteValid);
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
