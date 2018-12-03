package console;

import config.AppConfig;
import komponenten.spielregel.export.ISpielregel;
import komponenten.spielsteuerung.impl.SpielsteuerungImpl;
import komponenten.spielverwaltung.impl.SpielverwaltungImpl;
import model.Spiel;
import model.Spieler;
import model.Spielrunde;
import model.enums.RegelKompTyp;
import model.enums.SpielTyp;
import model.exceptions.MauMauException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.Scanner;

public class MainApp {

    //    // Die spezifische Implementierung muss definiert werden
//    @Autowired
//    @Qualifier("ohneSonder")
    private static ISpielregel spielregelohneSonder;
    //
//    @Autowired
//    @Qualifier("basicSonder")
    private static ISpielregel spielregelBasicSonder;
    //
//    @Autowired
//    @Qualifier("alleSonder")
    private static ISpielregel spielregelAlleSonder;

    static Scanner sc = new Scanner(System.in);

    static ConsoleUtil consoleUtil = new ConsoleUtil();

    public static void main(String[] args) throws MauMauException {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        SpielverwaltungImpl spielVerwaltung = context.getBean(SpielverwaltungImpl.class);

        SpielsteuerungImpl spielSteuerung = context.getBean(SpielsteuerungImpl.class);

        SpielTyp spielTyp = consoleUtil.spielTypWahl(sc);

        RegelKompTyp gewaehlteSpielegel = consoleUtil.regelWahl
                (sc, spielregelohneSonder, spielregelBasicSonder, spielregelAlleSonder);

        Spiel spiel = spielVerwaltung.starteNeuesSpiel(spielTyp, gewaehlteSpielegel);

        ArrayList<Spieler> spielerListe = consoleUtil.spielerEingabe(sc);

        Spielrunde spielrunde = spielVerwaltung.starteSpielrunde(spielerListe, spiel);

        Spieler spieler = spielSteuerung.fragWerDranIst(spielrunde.getSpielerListe());

        consoleUtil.printZugDetails(spielrunde, spieler);

        String wahl = sc.nextLine();

        if(wahl.toLowerCase().equals("m")){
            spielSteuerung.sollMauMauAufrufen(spieler);
        } else if (wahl.toLowerCase().equals("z")){

        } else {

        }

//        spielVerwaltung.beendeSpielrunde(spielrunde);
//
//        spielVerwaltung.beendeSpiel(spiel);






    }
}
