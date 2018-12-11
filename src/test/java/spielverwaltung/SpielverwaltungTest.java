package spielverwaltung;

import config.AppConfig;
import komponenten.karten.export.IKarten;
import komponenten.spielverwaltung.export.ISpielverwaltung;
import model.*;
import model.enums.*;
import model.exceptions.MauMauException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import repositories.SpielRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppConfig.class)
public class SpielverwaltungTest {
    @Autowired
    private ISpielverwaltung spielVerwaltungService;

    @MockBean
    private IKarten kartenService;

    @MockBean
    private SpielRepository spielRepository;


    private List<Spielkarte> stapel;

    @Before
    public void initialize() {
        stapel = new ArrayList<>();
        for (Blatttyp blatttyp : Blatttyp.values()) {
            for (Blattwert blattwert : Blattwert.values()) {
                if (blattwert != Blattwert.Joker) {
                    stapel.add(new Spielkarte(blattwert, blatttyp));
                }
            }
        }
    }

    /**
     * Test für den erfolgreichen Start eines Spiels
     *
     * @throws MauMauException
     */
    @Test
    public void testStarteSpielSuccess() throws MauMauException {

        // Spieltyp und RegelKompTyp definieren
        SpielTyp spielTyp = SpielTyp.MauMau;
        RegelKompTyp regelKompTyp = RegelKompTyp.OHNE_SONDER_REGEL;

        // Spiel erstellen
        Spiel spiel = spielVerwaltungService.starteNeuesSpiel(spielTyp, regelKompTyp);

        // Spiel sollte nicht null sein
        assertNotNull(spiel);
        // Das Feld "Beginn" soll gesetzt sein
        assertNotNull(spiel.getBeginn());
        // Das Spiel sollte den richtign Spieltyp haben
        assertEquals(spielTyp, spiel.getSpielTyp());
        // Das Spiel sollte den richtigen RegelKompTyp haben
        assertEquals(regelKompTyp, spiel.getRegelKompTyp());

    }

    /**
     * Test für den gescheiterten Start eines Spiels
     *
     * @throws MauMauException
     */
    @Test(expected = MauMauException.class)
    public void testStarteSpielFailed() throws MauMauException {
        SpielTyp spielTyp = null;
        RegelKompTyp regelKompTyp = null;

        spielVerwaltungService.starteNeuesSpiel(spielTyp, regelKompTyp);
    }

    /**
     * Test für die erfolgreiche Erstellung einer Spielrunde
     *
     * @throws MauMauException
     */
    @Test
    public void testStarteSpielRundeSuccess() throws MauMauException {

        // Spiel anlegen
        SpielTyp spielTyp = SpielTyp.MauMau;
        RegelKompTyp regelKompTyp = RegelKompTyp.OHNE_SONDER_REGEL;
        Spiel spiel = spielVerwaltungService.starteNeuesSpiel(spielTyp, regelKompTyp);

        // Spieler erstellen
        List<Spieler> spielerListe = new ArrayList<Spieler>();
        spielerListe.add(new Spieler("Ido"));
        spielerListe.add(new Spieler("Victor"));
        spielerListe.add(new Spieler("Lucas"));

        // Spielrunde erstellen
        List<Blatttyp> blatttypNicht = new ArrayList<>();
        List<Blattwert> blattwertNicht = new ArrayList<>();
        if (spiel.getSpielTyp() == SpielTyp.MauMau) {
            blattwertNicht.add(Blattwert.Joker);
        }
        Mockito.when(kartenService.baueStapel(blatttypNicht, blattwertNicht)).thenReturn(stapel);
        Spielrunde spielrunde = spielVerwaltungService.starteSpielrunde(spielerListe, spiel);

        // Spielrunde sollte nicht null sein
        assertNotNull(spielrunde);

        // Spielrunde sollte einen Start haben
        assertNotNull(spielrunde.getStart());

        // Die Spieler sollten in der Spielrunde registriert sein
        for (Spieler spieler : spielerListe) {
            assertTrue(spielrunde.getSpielerListe().contains(spieler));
        }
        // Die Spielrunde sollte einen Kartenstapel haben
        assertNotNull(spielrunde.getVerdeckteStapel());

        // Stapel sollte 52 karten haben
        assertEquals(34, spielrunde.getVerdeckteStapel().size());

        // Kartenservice muss einmal aufgerufen worden sein
        Mockito.verify(kartenService, Mockito.times(1)).baueStapel(blatttypNicht, blattwertNicht);

    }

    /**
     * Test für die gescheiterte Erstellung einer Spielrunde wegen eines unbekannten Spiels
     *
     * @throws MauMauException
     */
    @Test(expected = MauMauException.class)
    public void testStarteSpielRundeSpielerUnbekanntFailed() throws MauMauException {

        // Spiel anlegen
        SpielTyp spielTyp = SpielTyp.MauMau;
        RegelKompTyp regelKompTyp = RegelKompTyp.OHNE_SONDER_REGEL;
        Spiel spiel = spielVerwaltungService.starteNeuesSpiel(spielTyp, regelKompTyp);

        // unbekannte Spieler
        List<Spieler> spielerListe = new ArrayList<Spieler>();

        // Spielrunde erstellen
        spielVerwaltungService.starteSpielrunde(spielerListe, spiel);

        // Kartenservice soll nicht aufgerufen worden sein
        List<Blatttyp> blatttypNicht = new ArrayList<>();
        List<Blattwert> blattwertNicht = new ArrayList<>();
        Mockito.verify(kartenService, Mockito.times(0)).baueStapel(blatttypNicht, blattwertNicht);
    }

    /**
     * Test für die gescheiterte Erstellung einer Spielrunde wegen unbekannten Spieler
     *
     * @throws MauMauException
     */
    @Test(expected = MauMauException.class)
    public void testStarteSpielRundeSpielUnbekanntFailed() throws MauMauException {
        // Spiel unbekannt
        Spiel spiel = null;

        // Spieler erstellen
        List<Spieler> spielerListe = new ArrayList<Spieler>();
        spielerListe.add(new Spieler("Ido"));
        spielerListe.add(new Spieler("Victor"));
        spielerListe.add(new Spieler("Lucas"));

        // Spielrunde erstellen
        spielVerwaltungService.starteSpielrunde(spielerListe, spiel);

        // Kartenservice soll nicht aufgerufen worden sein
        List<Blatttyp> blatttypNicht = new ArrayList<>();
        List<Blattwert> blattwertNicht = new ArrayList<>();
        Mockito.verify(kartenService, Mockito.times(0)).baueStapel(blatttypNicht, blattwertNicht);
    }

    /**
     * Test für das erfolgreiche Beenden einer Spielrunde
     *
     * @throws MauMauException
     */
    @Test
    public void testBeendeSpielrundeSuccess() throws MauMauException {

        // Spiel anlegen
        SpielTyp spielTyp = SpielTyp.MauMau;
        RegelKompTyp regelKompTyp = RegelKompTyp.OHNE_SONDER_REGEL;
        Spiel spiel = spielVerwaltungService.starteNeuesSpiel(spielTyp, regelKompTyp);

        // Spieler erstellen
        List<Spieler> spielerListe = new ArrayList<Spieler>();
        spielerListe.add(new Spieler("Martin"));
        spielerListe.add(new Spieler("Pedro"));
        spielerListe.add(new Spieler("Antonio"));

        // Spielrunde erstellen
        List<Blatttyp> blatttypNicht = new ArrayList<>();
        List<Blattwert> blattwertNicht = new ArrayList<>();
        if (spiel.getSpielTyp() == SpielTyp.MauMau) {
            blattwertNicht.add(Blattwert.Joker);
        }
        Mockito.when(kartenService.baueStapel(blatttypNicht, blattwertNicht)).thenReturn(stapel);
        Spielrunde spielrunde = spielVerwaltungService.starteSpielrunde(spielerListe, spiel);

        // Spielrunde beenden
        List<Ergebnis> ergebnisse = spielVerwaltungService.beendeSpielrunde(spielrunde);

        // Dauer der Spielrunde soll berechnet worden sein
        assertNotNull(spielrunde.getDauer());

        // Ergebnisliste sollte nicht leer sein
        assertNotNull(ergebnisse);

        // Ein Ergebnis pro Spieler
        assertEquals(ergebnisse.size(), spielerListe.size());

        // Punkten prüfen
        for (Spieler spieler : spielrunde.getSpielerListe()) {
            int punkte = 0;
            for (Spielkarte spielkarte : spieler.getHand()) {
                punkte += PunkteMauMau.valueOf(spielkarte.getBlattwert().name()).getPunkte();
            }
            for (Ergebnis ergebnis : ergebnisse) {
                if (ergebnis.getSpieler() == spieler) {
                    assertEquals(punkte, ergebnis.getPunkte());
                }
            }
        }

        // Kartenservice muss einmal aufgerufen worden sein
        Mockito.verify(kartenService, Mockito.times(1)).baueStapel(blatttypNicht, blattwertNicht);

    }

    /**
     * Test für das gescheiterte Beenden einer Spielrunde
     *
     * @throws MauMauException
     */
    @Test(expected = MauMauException.class)
    public void testBeendeSpielrundeFailed() throws MauMauException {

        // Spielrunde wird nicht erstellt

        // Spieler erstellen
        List<Spieler> spielerListe = new ArrayList<Spieler>();
        spielerListe.add(new Spieler("Martin"));
        spielerListe.add(new Spieler("Pedro"));
        spielerListe.add(new Spieler("Antonio"));

        // Versuchen eine Spielrunde zu erstellen
        spielVerwaltungService.starteSpielrunde(spielerListe, null);
    }

    /**
     * Test für das ergolgreiche Beenden eines Spiels
     *
     * @throws MauMauException
     */
    @Test
    public void testBeendeSpielSuccess() throws MauMauException {
        // Spiel anlegen
        SpielTyp spielTyp = SpielTyp.MauMau;
        RegelKompTyp regelKompTyp = RegelKompTyp.OHNE_SONDER_REGEL;
        Spiel spiel = spielVerwaltungService.starteNeuesSpiel(spielTyp, regelKompTyp);

        // Spiel beenden
        Mockito.when(spielRepository.save(spiel)).thenReturn(spiel);
        spielVerwaltungService.beendeSpiel(spiel);

        // Dauer des Spiels soll berechnet worden sein
        assertNotNull(spiel.getDauer());
    }

    /**
     * Test für das gescheiterte Beenden eines Spiels
     */
    @Test(expected = MauMauException.class)
    public void testBeendeSpielFailed() throws MauMauException {

        // Spiel leer
        Spiel spiel = null;

        // Spiel beenden
        spielVerwaltungService.beendeSpiel(spiel);

    }
}
