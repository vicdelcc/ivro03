package komponenten.spielverwaltung.impl;

import komponenten.karten.export.IKarten;
import komponenten.spielverwaltung.export.ISpielverwaltung;
import model.*;
import model.enums.*;
import model.exceptions.TechnischeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.SpielRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Komponent, der ein Spiel bzw. Spielrunden verwaltet
 */
@Service
public class SpielverwaltungImpl implements ISpielverwaltung {

    @Autowired
    private IKarten kartenService;

    @Autowired
    private SpielRepository spielRepository;


    public Spiel starteNeuesSpiel(SpielTyp spielTyp, RegelKompTyp regelKompTyp) {
        if(spielTyp == null) {
            throw new TechnischeException("Spieltyp ist nicht initialisiert");
        } else if(regelKompTyp == null) {
            throw new TechnischeException("Regelkomponententyp ist nicht initialisiert");
        }
        Spiel spiel = new Spiel(spielTyp, regelKompTyp);
        return spiel;
    }

    public Spielrunde starteSpielrunde(List<Spieler> spielerListe, Spiel spiel) {
        if(spielerListe.size() < 2) {
            throw new TechnischeException("Es müssen mindestens 2 Spieler registriert sein");
        } else if (spiel == null) {
            throw new TechnischeException("Übergebene Spiel war null");
        }
        // Für ab 2. Spielrunde muss man aller SPieler auf nicht spielend setzen
        for(Spieler spieler : spielerListe) {
            spieler.setSpielend(false);
        }
        // Random spieler wird als Erster gewählt
        int ersterSpieler = new Random().nextInt(spielerListe.size());
        spielerListe.get(ersterSpieler).setSpielend(true);

        // Spielrunde wird generiert
        Spielrunde spielrunde = new Spielrunde(spiel, spielerListe);
        // Verdeckter Stapel wird generiert
        List<Blattwert> blattwertNicht = new ArrayList<>();
        if(spiel.getSpielTyp() == SpielTyp.MauMau) {
            blattwertNicht.add(Blattwert.Joker);
        }
        List<Blatttyp> blatttypNicht = new ArrayList<>();
        spielrunde.setVerdeckteStapel(kartenService.baueStapel(blatttypNicht, blattwertNicht));

        // Aufgelegter Stapel mit initialer Spielkarte wird generiert
        List<Spielkarte> aufgelegterStapel = new ArrayList<>();
        int indexInitialKarte = new Random().nextInt(spielrunde.getVerdeckteStapel().size());
        aufgelegterStapel.add(spielrunde.getVerdeckteStapel().get(indexInitialKarte));
        spielrunde.setAufgelegtStapel(aufgelegterStapel);
        spielrunde.getVerdeckteStapel().remove(indexInitialKarte);

        // Verteile Initialkarten 6
        for(Spieler spieler : spielrunde.getSpielerListe()) {
            spieler.setSpielrunde(spielrunde);
            for(int i = 0; i<6; i++) {
                int result = (int)(Math.random()*(spielrunde.getVerdeckteStapel().size()-1));
                spieler.getHand().add(spielrunde.getVerdeckteStapel().get(result));
                spielrunde.getVerdeckteStapel().remove(result);
            }

        }
        spiel.getSpielrunden().add(spielrunde);
        // ZuZiehendenKarte default auf 0
        spielrunde.setZuZiehnKartenAnzahl(0);
        return spielrunde;
    }

    public Spielrunde beendeSpielrunde(Spielrunde spielrunde) {
        if(spielrunde == null) {
            throw new TechnischeException("Spielrunde ist nicht initialisiert");
        }
        // Dauer
        Duration duration = Duration.between(spielrunde.getStart().toInstant(), Instant.now());
        spielrunde.setDauer(duration.getSeconds());

        // Ergebnisse
        for(Spieler spieler : spielrunde.getSpielerListe()) {

            // Gewinner
            if(spieler.getHand().size() == 0) {
                spielrunde.setGewinnerName(spieler.getName());
            }
            // Punkte Rest
            int punkte = 0;
            for(Spielkarte spielkarte : spieler.getHand()) {
                switch (spielrunde.getSpiel().getSpielTyp()) {
                    case MauMau:
                        punkte += PunkteMauMau.valueOf(spielkarte.getBlattwert().name()).getPunkte();
                        break;
                }
            }
            Ergebnis ergebnis = new Ergebnis(punkte, spielrunde, spieler);
            spielrunde.getErgebnisListe().add(ergebnis);
        }

        return spielrunde;
    }

    @SuppressWarnings("squid:S2583")
    public Spiel beendeSpiel(Spiel spiel) {
        if(spiel == null) {
            throw new TechnischeException("Spiel ist nicht initialisiert");
        }
        // Dauer
        Duration duration = Duration.between(spiel.getBeginn().toInstant(), Instant.now());
        spiel.setDauer(duration.getSeconds());

        Spiel spielSaved= this.spielRepository.save(spiel);

        if(spielSaved == null) {
            throw new TechnischeException("Spiel konnte nicht gespeichert werden");
        }
        return spiel;
    }
}
