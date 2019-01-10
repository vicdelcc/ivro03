package virtuellerSpieler;

import config.AppConfig;
import komponenten.karten.export.Blatttyp;
import komponenten.karten.export.Blattwert;
import komponenten.karten.export.Spielkarte;
import komponenten.spielregel.export.ISpielregel;
import komponenten.spielverwaltung.export.RegelKompTyp;
import komponenten.spielverwaltung.export.Spieler;
import komponenten.spielverwaltung.export.Spielrunde;
import komponenten.spielverwaltung.export.Stapel;
import komponenten.virtuellerSpieler.export.IVirtuellerSpieler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import util.exceptions.TechnischeException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppConfig.class)
public class VirtuellerSpielerTest {

    @Autowired
    private IVirtuellerSpieler virtuellerSpielerService;

    @MockBean
    @Qualifier("ohneSonder")
    private ISpielregel spielregelOhneSonder;

    @MockBean
    @Qualifier("basicSonder")
    private ISpielregel spielregelBasicSonder;

    @MockBean
    @Qualifier("alleSonder")
    private ISpielregel spielregelAlleSonder;

    /**
     * Test für den erfolgreichen SucheBlatttypAus
     */
    @Test
    public void testSucheBlatttypAusSuccess() {

        // Spieler mit Hand mit mehreren Blatttypen anlegen
        Spieler spieler = new Spieler("Tom", true);
        List<Spielkarte> hand = new ArrayList<>();
        hand.add(new Spielkarte(Blattwert.Sechs, Blatttyp.Herz));
        hand.add(new Spielkarte(Blattwert.Sechs, Blatttyp.Kreuz));
        hand.add(new Spielkarte(Blattwert.Sechs, Blatttyp.Karo));
        spieler.setHand(hand);

        // Service aufrufen
        Blatttyp blatttyp = virtuellerSpielerService.sucheBlatttypAus(spieler);

        assertNotNull(blatttyp);

        List<Blatttyp> blatttypInHand = new ArrayList<>();
        for (Spielkarte spielkarte : hand) {
            if (!blatttypInHand.contains(spielkarte.getBlatttyp())) {
                blatttypInHand.add(spielkarte.getBlatttyp());
            }
        }
        assertTrue(blatttypInHand.contains(blatttyp));

    }

    /**
     * Test für den gescheiterten SucheBlatttypAus, weil der übergebene Spieler null ist
     */
    @Test(expected = TechnischeException.class)
    public void testSucheBlatttypAusFailed() {
        // Kein Spieler anlegen
        virtuellerSpielerService.sucheBlatttypAus(null);
    }

    /**
     * Test für den erfolgreichen SpieleKarte - OhneSonder
     */
    @Test
    public void testSpieleKarteSuccessOhneSonder() {
        // Spieler mit Hand anlegen
        Spieler spieler = new Spieler("Tom", true);
        List<Spielkarte> hand = new ArrayList<>();
        hand.add(new Spielkarte(Blattwert.Sechs, Blatttyp.Herz));
        hand.add(new Spielkarte(Blattwert.Sieben, Blatttyp.Herz));
        spieler.setHand(hand);
        // RegelKompTyp
        RegelKompTyp regelKompTyp = RegelKompTyp.MIT_BASIC_SONDER_REGEL;
        // Spielrunde mit aufgelegten Stapel, Rundefarbe und zuZiehendeKarten
        Spielrunde spielrunde = new Spielrunde();
        List<Spielkarte> aufgelegterStapel = new ArrayList<>();
        aufgelegterStapel.add(new Spielkarte(Blattwert.Sechs, Blatttyp.Kreuz));
        spielrunde.setAufgelegtStapel(new Stapel(aufgelegterStapel));
        spielrunde.setRundeFarbe(null);
        spielrunde.setZuZiehnKartenAnzahl(0);
        // Mock SpielRegel-Aufruf
        Mockito.when(spielregelBasicSonder.istKarteLegbar(spielrunde.getAufgelegtStapel().getSpielkarten().get(0), spieler.getHand().get(0), spielrunde.getRundeFarbe(), spielrunde.getZuZiehnKartenAnzahl() != 0)).thenReturn(true);
        String antwort = virtuellerSpielerService.spieleKarte(spielrunde, spieler, regelKompTyp);
        assertNotNull(antwort);
        assertEquals(0, Integer.parseInt(antwort));
    }

    /**
     * Test für den erfolgreichen SpieleKarte - BasicSonder
     */
    @Test
    public void testSpieleKarteSuccessBasicSonder() {
        // Spieler mit Hand anlegen
        Spieler spieler = new Spieler("Tom", true);
        List<Spielkarte> hand = new ArrayList<>();
        hand.add(new Spielkarte(Blattwert.Sechs, Blatttyp.Herz));
        hand.add(new Spielkarte(Blattwert.Sieben, Blatttyp.Herz));
        spieler.setHand(hand);
        // RegelKompTyp
        RegelKompTyp regelKompTyp = RegelKompTyp.ALLE_SONDER_REGEL;
        // Spielrunde mit aufgelegten Stapel, Rundefarbe und zuZiehendeKarten
        Spielrunde spielrunde = new Spielrunde();
        List<Spielkarte> aufgelegterStapel = new ArrayList<>();
        aufgelegterStapel.add(new Spielkarte(Blattwert.Sechs, Blatttyp.Kreuz));
        spielrunde.setAufgelegtStapel(new Stapel(aufgelegterStapel));
        spielrunde.setRundeFarbe(null);
        spielrunde.setZuZiehnKartenAnzahl(0);
        // Mock SpielRegel-Aufruf
        Mockito.when(spielregelAlleSonder.istKarteLegbar(spielrunde.getAufgelegtStapel().getSpielkarten().get(0), spieler.getHand().get(0), spielrunde.getRundeFarbe(), spielrunde.getZuZiehnKartenAnzahl() != 0)).thenReturn(true);
        String antwort = virtuellerSpielerService.spieleKarte(spielrunde, spieler, regelKompTyp);
        assertNotNull(antwort);
        assertEquals(0, Integer.parseInt(antwort));
    }

    /**
     * Test für den erfolgreichen SpieleKarte - AlleSonder
     */
    @Test
    public void testSpieleKarteSuccessAlleSonder() {
        // Spieler mit Hand anlegen
        Spieler spieler = new Spieler("Tom", true);
        List<Spielkarte> hand = new ArrayList<>();
        hand.add(new Spielkarte(Blattwert.Sechs, Blatttyp.Herz));
        hand.add(new Spielkarte(Blattwert.Sieben, Blatttyp.Herz));
        spieler.setHand(hand);
        // RegelKompTyp
        RegelKompTyp regelKompTyp = RegelKompTyp.OHNE_SONDER_REGEL;
        // Spielrunde mit aufgelegten Stapel, Rundefarbe und zuZiehendeKarten
        Spielrunde spielrunde = new Spielrunde();
        List<Spielkarte> aufgelegterStapel = new ArrayList<>();
        aufgelegterStapel.add(new Spielkarte(Blattwert.Sechs, Blatttyp.Kreuz));
        spielrunde.setAufgelegtStapel(new Stapel(aufgelegterStapel));
        spielrunde.setRundeFarbe(null);
        spielrunde.setZuZiehnKartenAnzahl(0);
        // Mock SpielRegel-Aufruf
        Mockito.when(spielregelOhneSonder.istKarteLegbar(spielrunde.getAufgelegtStapel().getSpielkarten().get(0), spieler.getHand().get(0), spielrunde.getRundeFarbe(), spielrunde.getZuZiehnKartenAnzahl() != 0)).thenReturn(true);
        String antwort = virtuellerSpielerService.spieleKarte(spielrunde, spieler, regelKompTyp);
        assertNotNull(antwort);
        assertEquals(0, Integer.parseInt(antwort));
    }

    /**
     * Test für den erfolgreichen SpieleKarte und MauMau-Aufruf
     */
    @Test
    public void testSpieleKarteSuccessMauMauRufen() {
        // Spieler mit Hand anlegen
        Spieler spieler = new Spieler("Tom", true);
        List<Spielkarte> hand = new ArrayList<>();
        hand.add(new Spielkarte(Blattwert.Sechs, Blatttyp.Herz));
        spieler.setHand(hand);
        // RegelKompTyp
        RegelKompTyp regelKompTyp = RegelKompTyp.OHNE_SONDER_REGEL;
        // Spielrunde mit aufgelegten Stapel, Rundefarbe und zuZiehendeKarten
        Spielrunde spielrunde = new Spielrunde();
        List<Spielkarte> aufgelegterStapel = new ArrayList<>();
        aufgelegterStapel.add(new Spielkarte(Blattwert.Sechs, Blatttyp.Kreuz));
        spielrunde.setAufgelegtStapel(new Stapel(aufgelegterStapel));
        spielrunde.setRundeFarbe(null);
        spielrunde.setZuZiehnKartenAnzahl(0);
        // Service-Aufruf
        Mockito.when(spielregelOhneSonder.istKarteLegbar(spielrunde.getAufgelegtStapel().getSpielkarten().get(0), spieler.getHand().get(0), spielrunde.getRundeFarbe(), spielrunde.getZuZiehnKartenAnzahl() != 0)).thenReturn(true);
        String antwort = virtuellerSpielerService.spieleKarte(spielrunde, spieler, regelKompTyp);
        assertNotNull(antwort);
        assertEquals("m", antwort);
    }

    /**
     * Test für den erfolgreichen SpieleKarte --> Karte ziehen
     */
    @Test
    public void testSpieleKarteSuccessZiehen() {
        // Spieler mit Hand anlegen
        Spieler spieler = new Spieler("Tom", true);
        List<Spielkarte> hand = new ArrayList<>();
        hand.add(new Spielkarte(Blattwert.Sechs, Blatttyp.Herz));
        hand.add(new Spielkarte(Blattwert.Sieben, Blatttyp.Kreuz));
        hand.add(new Spielkarte(Blattwert.Neun, Blatttyp.Karo));
        spieler.setHand(hand);
        // RegelKompTyp
        RegelKompTyp regelKompTyp = RegelKompTyp.OHNE_SONDER_REGEL;
        // Spielrunde mit aufgelegten Stapel, Rundefarbe und zuZiehendeKarten
        Spielrunde spielrunde = new Spielrunde();
        List<Spielkarte> aufgelegterStapel = new ArrayList<>();
        aufgelegterStapel.add(new Spielkarte(Blattwert.Acht, Blatttyp.Pik));
        spielrunde.setAufgelegtStapel(new Stapel(aufgelegterStapel));
        spielrunde.setRundeFarbe(null);
        spielrunde.setZuZiehnKartenAnzahl(0);
        for (Spielkarte spielkarte : spieler.getHand()) {
            Mockito.when(spielregelOhneSonder.istKarteLegbar(spielrunde.getAufgelegtStapel().getSpielkarten().get(0), spielkarte, spielrunde.getRundeFarbe(), spielrunde.getZuZiehnKartenAnzahl() != 0)).thenReturn(false);
        }
        String antwort = virtuellerSpielerService.spieleKarte(spielrunde, spieler, regelKompTyp);
        assertNotNull(antwort);
        assertEquals("z", antwort);
    }

    /**
     * Test für den gescheiterten SpieleKarte wegen Spielrunde null
     */
    @Test(expected = TechnischeException.class)
    public void testSpieleKarteFailedSpielrundeNull() {
        Spieler spieler = new Spieler("Tom", true);
        RegelKompTyp regelKompTyp = RegelKompTyp.OHNE_SONDER_REGEL;
        virtuellerSpielerService.spieleKarte(null, spieler, regelKompTyp);
    }

    /**
     * Test für den gescheiterten SpieleKarte wegen Spieler null
     */
    @Test(expected = TechnischeException.class)
    public void testSpieleKarteFailedSpielerNull() {
        RegelKompTyp regelKompTyp = RegelKompTyp.OHNE_SONDER_REGEL;
        Spielrunde spielrunde = new Spielrunde();
        virtuellerSpielerService.spieleKarte(spielrunde, null, regelKompTyp);
    }

    /**
     * Test für den gescheiterten SpieleKarte wegen Spieltyp null
     */
    @Test(expected = TechnischeException.class)
    public void testSpieleKarteFailedSpieltypNull() {
        Spieler spieler = new Spieler("Tom", true);
        Spielrunde spielrunde = new Spielrunde();
        virtuellerSpielerService.spieleKarte(spielrunde, spieler, null);
    }
}
