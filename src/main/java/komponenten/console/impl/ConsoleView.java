package komponenten.console.impl;


import com.github.javafaker.Faker;
import komponenten.console.exceptions.FachlicheException;
import komponenten.karten.export.Blatttyp;
import komponenten.karten.export.Spielkarte;
import komponenten.spielverwaltung.export.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class ConsoleView {

    static Scanner sc = new Scanner(System.in);

    public List<Spieler> spielerEingabe(List<Spieler> spielerList) {
        boolean nochSpieler = true;
//        sc.nextLine();
        System.out.println("Bitte geben Sie die Namen der Spieler:");
        do {
            System.out.print((spielerList.size() + 1) + ". Spieler: ");
            String name = sc.next();
            spielerList.add(new Spieler(name, false));

            System.out.println("Wollen Sie noch einen Spieler ins Spiel eintragen? (j|n)");
            String antwort = sc.next();
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

    public RegelKompTyp regelWahl() {
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

    public SpielTyp spielTypWahl() {

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

    public String eingabeWaehlen(Spieler spieler) {
        String wahl;
        boolean repeat = true;
        do {
            wahl = sc.next();
            try {

                istEingabeRichtig(wahl, spieler.getHand().size());

                repeat = false;
            } catch (FachlicheException e) {
                System.out.println(">>> Die Eingabe war falsch! Bitte geben Sie 'm','z' oder eine Zahl <<<");
                repeat = true;
            }

        } while (repeat);

        return wahl;
    }

    public boolean istEingabeRichtig(String eingabe, int size) throws FachlicheException {
        if (StringUtils.isNumeric(eingabe)) {
            int intEingabe = Integer.parseInt(eingabe);
            if (intEingabe < size && intEingabe >= 0) {
                return true;
            } else {
                throw new FachlicheException(new IllegalArgumentException());
            }
        } else {
            if (eingabe.toLowerCase().equals("m") || eingabe.toLowerCase().equals("z")) {
                return true;
            } else {
                throw new FachlicheException(new IllegalArgumentException());
            }
        }
    }

    private <T> void printChoices(T[] values, String msg) {
        int counter = 0;
        if (msg != null) {
            System.out.println(msg);
        }
        for (T typ : values) {
            System.out.println("[" + counter + "] " + typ.toString());
            counter++;
        }
    }

    public void printZugDetails(Spielrunde spielrunde, Spieler spieler) {
        Spielkarte letzteKarte = spielrunde.getAufgelegtStapel().getSpielkarten().get(spielrunde.getAufgelegtStapel().getSpielkarten().size() - 1);
        System.out.println("-----------------------------------");
        System.out.println("    Spieler daran:  " + spieler.getName());
        if(spielrunde.getRundeFarbe()!= null) {
            System.out.println("  Spielrundefarbe:  " + spielrunde.getRundeFarbe());
            System.out.println("-----------------------------------");
        } else {
            System.out.println(" Aufgelegte Karte:  " + letzteKarte.toString());
            System.out.println("-----------------------------------");
        }
        if (spielrunde.getZuZiehnKartenAnzahl() != null && spielrunde.getZuZiehnKartenAnzahl() > 0) {
            System.out.println("### " + spielrunde.getZuZiehnKartenAnzahl() + " Karten sollen gezogen werden ###\n");
        }

    }

    public void printHand(Spieler spieler) {
        System.out.println(spieler.getName() + " hat folgenden karten in der Hand:");
        int counter = 0;
        for (Spielkarte spielkarte : spieler.getHand()) {
            System.out.println("[" + counter + "] " + spielkarte.toString());
            counter++;
        }

        System.out.println("\nALTERNATIVEN:");
        System.out.println("[m] Maumau aufrufen");
        System.out.println("[z] Karten ziehen");
        System.out.print("\nAuswahl: ");
    }

    public void printFarben() {
        System.out.println("Bitte wählen Sie eine Farbe:");
        int counter = 0;
        for (Blatttyp value : Blatttyp.values()) {
            System.out.println("[" + counter + "] " + value.toString());
            counter++;
        }

    }

    public Blatttyp farbeWaehlen() {
        int i = sc.nextInt();
        return Blatttyp.values()[i];
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    public void mauMauNichtgerufenMsg() {
        System.out.println("### Sie haben MauMau nicht gerufen. ###");
    }

    public void mauMauRufenMsg() {
        System.out.println("### Mau Mau! ###");
    }

    public void spielBeendetMsg() {
        System.out.println("### Spielrunde beendet! ###");
    }

    public void nichtLegbareKarteMsg() {
        System.out.println("### Karte kann nicht gelegt werden! ###");
    }

    public void mauMauNichtAufrufenMsg() {
        System.out.println("### Sie müssen MauMau nicht aufrufen ###");
    }

    public void karteGezogenMsg(int anzhalZiehen, Spieler spieler) {
        if (!spieler.isVirtuellerSpieler()) {
            if (anzhalZiehen == 0) {
                System.out.println("### Eine Karte wurde gezogen ###");
            } else {
                System.out.println("### " + anzhalZiehen + " Karten wurden gezogen ###");

            }
        }
    }

    public void zeigeErgebnisse(Spielrunde spielrunde) {
        System.out.println("### Spielrunde wurde beendet. Gewinner: " + spielrunde.getGewinnerName());
        Collections.sort(spielrunde.getErgebnisListe(), (Ergebnis e1, Ergebnis e2) -> e1.getPunkte() - e2.getPunkte());
        int platz = 1;
        for (Ergebnis ergebnis : spielrunde.getErgebnisListe()) {
            System.out.println(platz + ". Platz: " + ergebnis.getSpieler().getName() + " mit " + ergebnis.getPunkte() + " Punkte.");
            platz++;
        }
    }

    public boolean nochEineRunde() {
        return frageJaOderNein(">>> Wollen Sie noch eine Runde spielen? (j|n) <<<");
    }

    private boolean istEingabeRichtigJoderN(String wahl) {
        return wahl.toLowerCase().equals("j") || wahl.toLowerCase().equals("n");

    }

    private boolean frageJaOderNein(String frage) {
        System.out.println(frage);
        String wahl;
        do {
            wahl = sc.next();
            if (!istEingabeRichtigJoderN(wahl)) {
                System.out.println(">>> Die Eingabe war falsch! Bitte geben Sie 'm','z' oder eine Zahl <<<");
            }
        } while (!istEingabeRichtigJoderN(wahl));
        return wahl.toLowerCase().equals("j");
    }

    public void zeigeSpielID(Spiel spiel) {
        System.out.println("### Der Spiel-ID lautet: " + spiel.getIdentity() + " ###");
    }

    public int spielFortfuehren(boolean weiter) {
        if (weiter) {
            System.out.println("### Spiel-ID nicht vorhanden ###");
        }
        if (frageJaOderNein(">>> Wollen Sie ein altes Spiel fortführen? (j|n) <<<")) {
            System.out.println("Bitte geben Sie den Spiel-ID ein: ");
            while (!sc.hasNextInt()) {
                System.out.println(">>> Nur ganze Zahlen erlaubt! <<<");
                sc.next();
            }
            return sc.nextInt();
        } else {
            return 0;
        }

    }


    public List<Spieler> virtuellerSpielerAuswahl() {
        List<Spieler> spielerList = new ArrayList<>();
        if (frageJaOderNein(">>> Wollen Sie mit virtuellen Spieler spielen? (j|n) <<<")) {
            System.out.println("Bitte geben Sie die Anzahl von virtueller Spieler: ");
            int anzahl = 0;
            while (!sc.hasNextInt()) {
                System.out.println(">>> Nur ganze Zahlen erlaubt! <<<");
                sc.next();
            }
            anzahl = sc.nextInt();
            for (int i = 1; i <= anzahl; i++) {
                spielerList.add(createFakeSpieler());
            }
        }
        return spielerList;
    }

    private Spieler createFakeSpieler() {
        Faker faker = new Faker(new Locale("de"));
        return new Spieler(faker.name().firstName(), true);
    }

    public void printAntwortVirtuellerSpieler(String antwort, Spieler spieler, Spielkarte spielkarte, int anzahlKarten) {
        System.out.println("-------------------------------------------------------------------------");
        if (StringUtils.isNumeric(antwort)) {
            System.out.println("Virtueller Spieler " + spieler.getName() + " hat eine Karte gespielt: " + spielkarte.toString());
        } else {
            if (antwort.equals("m")) {
                System.out.println("Virtueller Spieler " + spieler.getName() + " hat MauMau aufgerufen");
            }
            if (antwort.equals("z")) {
                if (anzahlKarten == 0) {
                    System.out.println("Virtueller Spieler " + spieler.getName() + " hat eine Karte gezogen");
                } else {
                    System.out.println("Virtueller Spieler " + spieler.getName() + " hat " + anzahlKarten + " Karten gezogen");
                }

            }
        }
        System.out.println("-------------------------------------------------------------------------");
    }

}
