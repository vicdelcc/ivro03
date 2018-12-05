package komponenten.console.impl;


import model.Spieler;
import model.Spielkarte;
import model.Spielrunde;
import model.enums.RegelKompTyp;
import model.enums.SpielTyp;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleUtil {

    public ArrayList<Spieler> spielerEingabe(Scanner sc) {
        ArrayList<Spieler> spielerList = new ArrayList<>(0);
        boolean nochSpieler = true;
        sc.nextLine();
        System.out.println("Bitte geben Sie die Namen der Spieler:");
        do {
            System.out.println("Bitte geben Sie den " + (spielerList.size() + 1) + "er Namen:");
            String name = sc.nextLine();
            spielerList.add(new Spieler(name));

            System.out.println("Wollen Sie noch einen Spieler ins Spiel eintragen? (y|n)");
            String antwort = sc.nextLine();
            if (!antwort.equals("y")) {
                if (spielerList.size() > 1) {
                    nochSpieler = false;
                } else {
                    System.out.println("Ein Spiel muss mindestens 2 Spieler haben");
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
                System.out.println("Bitte geben Sie einen der zulässigen Werte");
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
                System.out.println("Bitte geben Sie einen der zulässigen Werte");
                richtigeEingabe = false;
            } else {
                richtigeEingabe = true;
            }
        } while (!richtigeEingabe);

        return SpielTyp.values()[wahl];
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
            System.out.println("Wählen Sie " + counter + " für " + typ.toString());
            counter++;
        }
    }

    public void printZugDetails(Spielrunde spielrunde, Spieler spieler) {
        System.out.println("-----------------------------------");
        System.out.println("Der jetzige Spieler ist " + spieler.getName());
        System.out.println("Die aufgelegte Karte ist " + spielrunde.getAufgelegtStapel().get(spielrunde.getAufgelegtStapel().size()-1).toString());
        System.out.println(spielrunde.getZuZiehnKartenAnzahl() + " Karten sollen gezugen werden");
        printHand(spieler);
        System.out.println("Wenn Sie MauMau aufrufen wollen, geben Sie 'm' ein");
        System.out.println("Wenn Sie Karten ziehen wollen, geben Sie 'z' ein");
    }

    private Spielkarte wahleKarte(Scanner sc, List<Spielkarte> hand) {
        System.out.println("Mit welcher Karte wollen Sie spielen? (Waehlen Sie ein Index)");
        int i = sc.nextInt();
        return hand.get(i);
    }

    private void printHand(Spieler spieler) {
        System.out.println(spieler.getName() + "shand enthaelt die folgenden karten:");
        int counter = 0;
        for (Spielkarte spielkarte : spieler.getHand()) {
            System.out.println("[" + counter + "] " + spielkarte.toString());
            counter++;
        }
    }
}
