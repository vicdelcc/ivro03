package komponenten.spielverwaltung.impl;

import komponenten.karten.export.Blatttyp;
import komponenten.karten.export.Blattwert;
import komponenten.karten.export.IKarten;
import komponenten.karten.export.Spielkarte;
import komponenten.karten.repositories.KartenRepository;
import komponenten.spielverwaltung.export.*;
import komponenten.spielverwaltung.repositories.ErgebnisRepository;
import komponenten.spielverwaltung.repositories.SpielRepository;
import komponenten.spielverwaltung.repositories.SpielrundeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.exceptions.TechnischeException;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toList;

/**
 * Komponent, der ein Spiel bzw. Spielrunden verwaltet
 */
@Service
public class SpielverwaltungImpl implements ISpielverwaltung {

    @Autowired
    private IKarten kartenService;

    @Autowired
    private SpielRepository spielRepository;

    @Autowired
    private KartenRepository kartenRepository;

    @Autowired
    private SpielrundeRepository spielrundeRepository;

    @Autowired
    private ErgebnisRepository ergebnisRepository;


    public Spiel starteNeuesSpiel(SpielTyp spielTyp, RegelKompTyp regelKompTyp) {
        if (spielTyp == null) {
            throw new TechnischeException("Spieltyp ist nicht initialisiert");
        } else if (regelKompTyp == null) {
            throw new TechnischeException("Regelkomponententyp ist nicht initialisiert");
        }
        // Spielkarten anlegen, um diese zu persistieren
        List<Blattwert> blattwertNicht = new ArrayList<>();
        if (spielTyp == SpielTyp.MauMau) {
            blattwertNicht.add(Blattwert.Joker);
        }
        List<Blatttyp> blatttypNicht = new ArrayList<>();
        List<Spielkarte> spielkartenToSave = kartenService.baueStapel(blatttypNicht, blattwertNicht);
        List<Spielkarte> spielkartenCloneToSave = spielkartenToSave.stream().collect(toList());

        List<Spielkarte> spielkartenDB = kartenRepository.findAll();
        for (Spielkarte spielkarte : spielkartenToSave) {
            for (Spielkarte spielkarteDB : spielkartenDB) {
                if (spielkarte.getBlatttyp() == spielkarteDB.getBlatttyp() && spielkarte.getBlattwert() == spielkarteDB.getBlattwert()) {
                    spielkartenCloneToSave.remove(spielkarte);
                }
            }
        }

        this.kartenRepository.saveAll(spielkartenCloneToSave);

        Spiel spiel = new Spiel(spielTyp, regelKompTyp);
        spiel = this.spielRepository.save(spiel);
        return spiel;
    }

    public Spielrunde starteSpielrunde(List<Spieler> spielerListe, Spiel spiel) {
        if (spielerListe.size() < 2) {
            throw new TechnischeException("Es müssen mindestens 2 Spieler registriert sein");
        } else if (spiel == null) {
            throw new TechnischeException("Übergebene Spiel war null");
        }
        // Für ab 2. Spielrunde muss man aller Spieler auf nicht spielend setzen
        for (Spieler spieler : spielerListe) {
            spieler.setSpielend(false);
        }
        // Random spieler wird als Erster gewählt
        int ersterSpieler = new Random().nextInt(spielerListe.size());
        spielerListe.get(ersterSpieler).setSpielend(true);

        // Spielrunde wird generiert
        Spielrunde spielrunde = new Spielrunde(spiel, spielerListe);
        // Verdeckter Stapel wird generiert
        List<Blattwert> blattwertNicht = new ArrayList<>();
        if (spiel.getSpielTyp() == SpielTyp.MauMau) {
            blattwertNicht.add(Blattwert.Joker);
        }
        List<Blatttyp> blatttypNicht = new ArrayList<>();
        List<Spielkarte> spielkarteStapel = kartenService.baueStapel(blatttypNicht, blattwertNicht);
        List<Spielkarte> spielkartenDB = kartenRepository.findAll();
        List<Spielkarte> spielkartenDBToPlay = new ArrayList<>();
        for (Spielkarte spielkarte : spielkarteStapel) {
            for (Spielkarte spielkarteDB : spielkartenDB) {
                if (spielkarte.getBlatttyp() == spielkarteDB.getBlatttyp() && spielkarte.getBlattwert() == spielkarteDB.getBlattwert()) {
                    spielkartenDBToPlay.add(spielkarteDB);
                }
            }
        }
        spielrunde.setVerdeckteStapel(new Stapel(spielkartenDBToPlay));

        // Aufgelegter Stapel mit initialer Spielkarte wird generiert
        List<Spielkarte> aufgelegterStapel = new ArrayList<>();
        int indexInitialKarte = new Random().nextInt(spielrunde.getVerdeckteStapel().getSpielkarten().size());
        aufgelegterStapel.add(spielrunde.getVerdeckteStapel().getSpielkarten().get(indexInitialKarte));
        spielrunde.setAufgelegtStapel(new Stapel(aufgelegterStapel));
        spielrunde.getVerdeckteStapel().getSpielkarten().remove(indexInitialKarte);

        // Verteile Initialkarten 6
        for (Spieler spieler : spielrunde.getSpielerListe()) {
            spieler.setSpielrunde(spielrunde);
            List<Spielkarte> liste = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                int result = (int) (Math.random() * (spielrunde.getVerdeckteStapel().getSpielkarten().size() - 1));
                spieler.getHand().add(spielrunde.getVerdeckteStapel().getSpielkarten().get(result));
                spielrunde.getVerdeckteStapel().getSpielkarten().remove(result);
            }
        }
        spiel.getSpielrunden().add(spielrunde);
        // ZuZiehendenKarte default auf 0
        spielrunde.setZuZiehnKartenAnzahl(0);
        spielrunde = this.spielrundeRepository.save(spielrunde);
        return spielrunde;
    }

    public Spielrunde beendeSpielrunde(Spielrunde spielrunde) {
        if (spielrunde == null) {
            throw new TechnischeException("Spielrunde ist nicht initialisiert");
        }
        // Dauer
        Duration duration = Duration.between(spielrunde.getStart().toInstant(), Instant.now());
        spielrunde.setDauer(duration.getSeconds());

        // Ergebnisse
        for (Spieler spieler : spielrunde.getSpielerListe()) {

            // Gewinner

            if (spieler.getHand().size() == 0) {
                spielrunde.setGewinnerName(spieler.getName());
            }

            // Punkte Rest
            int punkte = 0;

            for (Spielkarte spielkarte : spieler.getHand()) {
                switch (spielrunde.getSpiel().getSpielTyp()) {
                    case MauMau:
                        punkte += PunkteMauMau.valueOf(spielkarte.getBlattwert().name()).getPunkte();
                        break;
                }
            }
            Ergebnis ergebnis = new Ergebnis(punkte, spielrunde, spieler);

            this.ergebnisRepository.save(ergebnis);
            spielrunde.getErgebnisListe().add(ergebnis);
        }

        this.spielrundeRepository.save(spielrunde);

        return spielrunde;
    }

    @SuppressWarnings("squid:S2583")
    public Spiel beendeSpiel(Spiel spiel) {
        if (spiel == null) {
            throw new TechnischeException("Spiel ist nicht initialisiert");
        }
        // Dauer
        Duration duration = Duration.between(spiel.getBeginn().toInstant(), Instant.now());
        spiel.setDauer(duration.getSeconds());

        Spiel spielSaved = this.spielRepository.save(spiel);

        if (spielSaved == null) {
            throw new TechnischeException("Spiel konnte nicht gespeichert werden");
        }

        return spiel;
    }
}
