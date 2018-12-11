package komponenten.console.impl;


import model.Spieler;
import model.Spielkarte;
import model.Spielrunde;
import model.enums.Blatttyp;
import model.enums.RegelKompTyp;
import model.enums.SpielTyp;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleView {

    public ArrayList<Spieler> spielerEingabe(Scanner sc) {
        ArrayList<Spieler> spielerList = new ArrayList<>(0);
        boolean nochSpieler = true;
        sc.nextLine();
        System.out.println("Bitte geben Sie die Namen der Spieler:");
        do {
            System.out.print((spielerList.size() + 1) + ". Spieler: ");
            String name = sc.nextLine();
            spielerList.add(new Spieler(name));

            System.out.println("Wollen Sie noch einen Spieler ins Spiel eintragen? (j|n)");
            String antwort = sc.nextLine();
            if (!antwort.equals("j")) {
                if (spielerList.size() > 1) {
                    nochSpieler = false;
                } else {
                    System.out.println("### Ein Spiel muss mindestens 2 Spieler haben ###");
                }
            }
        } while (nochSpieler);

        return spielerList;
    }

    public RegelKompTyp regelWahl(Scanner sc) {
        int wahl;
        boolean richtigeEingabe;

        printChoices(RegelKompTyp.values(), "Mit welchen Regeln wollen Sie spielen?");

        do {
            wahl = sc.nextInt();
            if (wahl > RegelKompTyp.values().length || wahl < 0) {
                System.out.println(">>> Bitte geben Sie einen der zulässigen Werte <<<");
                richtigeEingabe = false;
            } else {
                richtigeEingabe = true;
            }
        } while (!richtigeEingabe);

        return (RegelKompTyp.values()[wahl]);

    }

    public SpielTyp spielTypWahl(Scanner sc) {

        int wahl;
        boolean richtigeEingabe;

        printChoices(SpielTyp.values(), "Welches Spiel wollen Sie spielen?");

        do {
            wahl = sc.nextInt();
            if (wahl > SpielTyp.values().length || wahl < 0) {
                System.out.println(">>> Bitte geben Sie einen der zulässigen Werte <<<");
                richtigeEingabe = false;
            } else {
                richtigeEingabe = true;
            }
        } while (!richtigeEingabe);

        return SpielTyp.values()[wahl];
    }

    public String eingabgeWaehlen(Scanner sc, Spieler spieler){
        String wahl;
        do{
            wahl = sc.nextLine();
            if(!istEingabeRichtig(wahl, spieler.getHand().size())){
                System.out.println(">>> Die Eingabe war falsch! Bitte geben Sie 'm','z' oder eine Zahl <<<");
            }
        } while (!istEingabeRichtig(wahl, spieler.getHand().size()));

        return wahl;
    }

    public boolean istEingabeRichtig(String eingabe, int size){
        if(StringUtils.isNumeric(eingabe)){
            int intEingabe = Integer.parseInt(eingabe);
            return intEingabe < size && intEingabe >= 0;
        }
        return eingabe.toLowerCase().equals("m") || eingabe.toLowerCase().equals("z");

    }

    private <T> void printChoices(T[] values, String msg) {
        int counter = 0;
        if (msg != null) {
            System.out.println(msg);
        }
        for (T typ : values) {
            System.out.println("["+ counter + "] " + typ.toString());
            counter++;
        }
    }

    public void printZugDetails(Spielrunde spielrunde, Spieler spieler) {
        Spielkarte letzteKarte = spielrunde.getAufgelegtStapel().get(spielrunde.getAufgelegtStapel().size() - 1);
        System.out.println("-----------------------------------");
        System.out.println("    Spieler daran:  " + spieler.getName());
        if(letzteKarte.getBlatttyp().equals(spielrunde.getRundeFarbe()) || spielrunde.getRundeFarbe() == null){
            System.out.println(" Aufgelegte Karte:  " + letzteKarte.toString());
            System.out.println("-----------------------------------");
        } else {
            if(spielrunde.getRundeFarbe() != null){
                System.out.println("  Spielrundefarbe:  " + spielrunde.getRundeFarbe());
                System.out.println("-----------------------------------");
            }
        }
        if(spielrunde.getZuZiehnKartenAnzahl() != null && spielrunde.getZuZiehnKartenAnzahl() > 0){
            System.out.println("### " +spielrunde.getZuZiehnKartenAnzahl() + " Karten sollen gezogen werden ###");
        }
        printHand(spieler);
        System.out.println("\nALTERNATIVEN:");
        System.out.println("[m] Maumau aufrufen");
        System.out.println("[z] Karten ziehen");
        System.out.print("\nAuswahl: ");
    }

    private Spielkarte wahleKarte(Scanner sc, List<Spielkarte> hand) {
        System.out.println("Mit welcher Karte wollen Sie spielen? (Wählen Sie ein Index)");
        int i = sc.nextInt();
        return hand.get(i);
    }

    private void printHand(Spieler spieler) {
        System.out.println(spieler.getName() + " hat folgenden karten in der Hand:");
        int counter = 0;
        for (Spielkarte spielkarte : spieler.getHand()) {
            System.out.println("[" + counter + "] " + spielkarte.toString());
            counter++;
        }
    }

    public void printFarben(){
        System.out.println("Bitte wählen Sie eine Farbe:");
        int counter = 0;
        for (Blatttyp value : Blatttyp.values()) {
            System.out.println("[" + counter + "] " + value.toString());
            counter++;
        }

    }

    public Blatttyp farbeWawhlen(Scanner sc) {
        int i = sc.nextInt();
        sc.nextLine();
        return Blatttyp.values()[i];
    }
}
