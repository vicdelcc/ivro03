package komponenten.console.impl;


import komponenten.console.export.IConsole;
import komponenten.spielregel.export.ISpielregel;
import komponenten.spielsteuerung.export.ISpielsteuerung;
import komponenten.spielverwaltung.export.ISpielverwaltung;
import model.Spiel;
import model.Spieler;
import model.Spielrunde;
import model.enums.RegelKompTyp;
import model.enums.SpielTyp;
import model.exceptions.MauMauException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Scanner;

@Service
public class ConsoleImpl implements IConsole {

    @Autowired
    private ISpielverwaltung spielverwaltung;

    @Autowired
    @Qualifier("ohneSonder")
    private static ISpielregel spielregelohneSonder;

    @Autowired
    @Qualifier("basicSonder")
    private static ISpielregel spielregelBasicSonder;

    @Autowired
    @Qualifier("alleSonder")
    private static ISpielregel spielregelAlleSonder;

    @Autowired
    private static ISpielsteuerung spielsteuerung;


    static Scanner sc = new Scanner(System.in);

    static ConsoleUtil consoleUtil = new ConsoleUtil();

    @Override
    public void run() throws MauMauException {

        SpielTyp spielTyp = consoleUtil.spielTypWahl(sc);

        RegelKompTyp gewaehlteSpielegel = consoleUtil.regelWahl
                (sc, spielregelohneSonder, spielregelBasicSonder, spielregelAlleSonder);

        Spiel spiel = spielverwaltung.starteNeuesSpiel(spielTyp, gewaehlteSpielegel);

        ArrayList<Spieler> spielerListe = consoleUtil.spielerEingabe(sc);

        Spielrunde spielrunde = spielverwaltung.starteSpielrunde(spielerListe, spiel);

        Spieler spieler = spielsteuerung.fragWerDranIst(spielrunde.getSpielerListe());

        consoleUtil.printZugDetails(spielrunde, spieler);

        String wahl = sc.nextLine();

        if(wahl.toLowerCase().equals("m")){
            spielsteuerung.sollMauMauAufrufen(spieler);
        } else if (wahl.toLowerCase().equals("z")){

        } else {

        }

//        spielVerwaltung.beendeSpielrunde(spielrunde);
//
//        spielVerwaltung.beendeSpiel(spiel);

    }
}
