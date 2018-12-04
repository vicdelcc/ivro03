package komponenten.spielverwaltung.impl;

import komponenten.karten.export.IKarten;
import komponenten.spielverwaltung.export.ISpielverwaltung;
import model.*;
import model.enums.*;
import model.exceptions.MauMauException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.SpielRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Komponent, der ein Spiel verwaltet bzw. einzelne Spielrunde erstellt
 */
@Service
public class SpielverwaltungImpl implements ISpielverwaltung {


    @Autowired
    private IKarten kartenService;

    @Autowired
    private SpielRepository spielRepository;


    public Spiel starteNeuesSpiel(SpielTyp spielTyp, RegelKompTyp regelKompTyp) throws MauMauException {
        if(spielTyp == null || regelKompTyp == null) {
            throw new MauMauException("Fehler");
        }
        Spiel spiel = new Spiel(spielTyp, regelKompTyp);
        // Falls mehrere Nutzer auf verschiedenen Rechner ein Spiel spielen würde, müsste das Spiel bei der
        // Erstellung persistiert werden, damit der 2. Spieler das 1. erstellte Spiel nutzt
//        this.spielRepository.save(spiel);
        return spiel;
    }

    //TODO ask Vic where the first player is setted --> done
    //TODO ask Vic why list < 3 --> geändert zu 2
    //TODO Vic - erste Karte für die aufgelegte Stapel soll verteilt werden --> done
    public Spielrunde starteSpielrunde(List<Spieler> spielerListe, Spiel spiel) throws MauMauException {
        if(spielerListe.size() < 2 || spiel == null) {
            throw new MauMauException("Fehler");
        }
        // Random spieler wird als Erster gewählt
        int ersterSpieler = (int)(Math.random()*(spielerListe.size()-1));
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
        aufgelegterStapel.add(spielrunde.getVerdeckteStapel().get((int)Math.random()*((spielrunde.getVerdeckteStapel().size()-1))));
        spielrunde.setAufgelegtStapel(aufgelegterStapel);

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
        return spielrunde;
    }

    public List<Ergebnis> beendeSpielrunde(Spielrunde spielrunde) throws MauMauException {
        if(spielrunde == null) {
            throw new MauMauException("Fehler");
        }
        // Dauer
        Duration duration = Duration.between(spielrunde.getStart().toInstant(), Instant.now());
        // TODO transform to minutes
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

        return spielrunde.getErgebnisListe();
    }

    public Spiel beendeSpiel(Spiel spiel) throws MauMauException {
        if(spiel == null) {
            throw new MauMauException("Fehler");
        }
//        spiel = this.spielRepository.findById(spiel.getId()) .orElse(null);
        // Dauer
        Duration duration = Duration.between(spiel.getBeginn().toInstant(), Instant.now());
        // TODO Soll zu Minutes angepasst werden
        spiel.setDauer(duration.getSeconds());

        this.spielRepository.save(spiel);
        return spiel;
    }
}
