package spielsteuerung;

import config.AppConfig;
import komponenten.spielregel.export.ISpielregel;
import komponenten.spielregel.impl.SpielregelOhneSonderImpl;
import komponenten.spielsteuerung.export.ISpielsteuerung;
import komponenten.spielverwaltung.export.Spieler;
import komponenten.karten.export.Spielkarte;
import komponenten.spielverwaltung.export.Spielrunde;
import komponenten.karten.export.Blatttyp;
import komponenten.karten.export.Blattwert;
import komponenten.spielverwaltung.export.RegelKompTyp;
import util.exceptions.TechnischeException;
import komponenten.spielregel.export.RegelComponentUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppConfig.class)
public class SpielsteuerungTest {

    private Spieler spieler1;
    private Spielrunde spielrunde;
    private Spielkarte vorherigeKarte;

    @Autowired
    private ISpielsteuerung spielsteuerung;

    @MockBean
    @Qualifier("ohneSonder")
    private SpielregelOhneSonderImpl spielregelOhneSonderImpl;

    @MockBean
    @Qualifier("basicSonder")
    private ISpielregel spielregelBasicSonder;

    @MockBean
    @Qualifier("alleSonder")
    private ISpielregel spielregelAlleSonder;

    private static RegelComponentUtil regelComponentUtil;

    @Before
    public void initialize() {

        List<Spielkarte> hand = new ArrayList<>(1);
        hand.add(new Spielkarte(Blattwert.Drei, Blatttyp.Herz));
        spieler1 = new Spieler(hand, "spieler1", true);

        Spieler spieler2 = new Spieler(new ArrayList<>(0), "spieler2", false);

        spielrunde = new Spielrunde();
        ArrayList<Spieler> spielerliste = new ArrayList<>(0);
        spielerliste.add(spieler1);
        spielerliste.add(spieler2);
        spielrunde.setSpielerListe(spielerliste);
        spielrunde.setRundeFarbe(Blatttyp.Pik);

        vorherigeKarte = new Spielkarte(Blattwert.Fuenf, Blatttyp.Herz);

        List<Spielkarte> verdeckterStapel = new ArrayList<>();
        verdeckterStapel.addAll(Arrays.asList(new Spielkarte(Blattwert.Sieben, Blatttyp.Herz),
                new Spielkarte(Blattwert.Fuenf, Blatttyp.Karo), new Spielkarte(Blattwert.Dame, Blatttyp.Karo)));

        spielrunde.setVerdeckteStapel(verdeckterStapel);

        List<Spielkarte> aufgelegterStapel = new ArrayList<>();
        aufgelegterStapel.addAll(Arrays.asList(new Spielkarte(Blattwert.Vier, Blatttyp.Herz),
                new Spielkarte(Blattwert.Fuenf, Blatttyp.Pik), new Spielkarte(Blattwert.Dame, Blatttyp.Pik)));

        spielrunde.setAufgelegtStapel(aufgelegterStapel);

        spielrunde.setZuZiehnKartenAnzahl(0);

        regelComponentUtil = new RegelComponentUtil(spielrunde.getSpielerListe(), 0);
    }

    /**
     * Wenn ein Spieler eine Karte im Hand hat soll MauMau aufgerufen werden
     */
    @Test
    public void testSollMauMauAufrufenLetzteKarte() {

        //Spieler hat nur noch eine Karte im Hand
        assertEquals(1, spieler1.getHand().size());

        assertTrue(!spielrunde.getAufgelegtStapel().isEmpty());

        Spielkarte letzteAufgelegteKarte = spielrunde.getAufgelegtStapel().get(spielrunde.getAufgelegtStapel().size() - 1);

        Mockito.when(spielregelOhneSonderImpl.istKarteLegbar(letzteAufgelegteKarte, spieler1.getHand().get(0), letzteAufgelegteKarte.getBlatttyp(), false)).thenReturn(true);

        assertTrue(spielsteuerung.sollMauMauAufrufen(spielrunde, spieler1, RegelKompTyp.OHNE_SONDER_REGEL));
    }

    /**
     * Wenn ein Spieler mehr als eine Karte im Hand hat soll MauMau nicht aufgerufen werden
     */
    @Test
    public void testSollMauMauAufrufenMehrAlsEineKarteImHand() {

        //Fügt eine neue Karte zum Hand
        spieler1.getHand().add(new Spielkarte(Blattwert.Fuenf, Blatttyp.Herz));

        //Checkt ob der Hand mehr als eine Karte hat
        assertTrue(spieler1.getHand().size() > 1);

        assertFalse(spielsteuerung.sollMauMauAufrufen(spielrunde, spieler1, RegelKompTyp.OHNE_SONDER_REGEL));
    }

    /**
     * Wenn Spieler null ist, soll eine Exception geworfen werden.
     */
    @Test (expected = TechnischeException.class)
    public void testSollMauMauAufrufenSpielerNull() {

        assertFalse(spielsteuerung.sollMauMauAufrufen(spielrunde, null, RegelKompTyp.OHNE_SONDER_REGEL));
    }

    /**
     * Wenn Spielershand null ist, soll eine Exception geworfen werden.
     */
    @Test (expected = TechnischeException.class)
    public void testSollMauMauAufrufenSpielershandNull() {

        spieler1.setHand(null);

        assertFalse(spielsteuerung.sollMauMauAufrufen(spielrunde, spieler1, RegelKompTyp.OHNE_SONDER_REGEL));
    }

    /**
     * Wenn Spielershand leer ist, soll eine Exception geworfen werden.
     */
    @Test (expected = TechnischeException.class)
    public void testSollMauMauAufrufenSpielershandLeer() {

        spieler1.setHand(new ArrayList<>(0));

        assertFalse(spielsteuerung.sollMauMauAufrufen(spielrunde, spieler1, RegelKompTyp.OHNE_SONDER_REGEL));
    }

    /**
     * Check wie viele Karten soll vom verdeckten Stapel gezogen werden
     */
    @Test
    public void testCheckZuziehendenKarten() {
        int anzahlZuZiehendeKarten = 0;

        spielrunde.setZuZiehnKartenAnzahl(anzahlZuZiehendeKarten);

        assertEquals(anzahlZuZiehendeKarten, spielsteuerung.checkZuZiehendenKarten(spielrunde));
    }

    /**
     * Wenn ein Spielrunde == null übergeben wird, soll TechnischeException zurückgegeben werden
     */
    @Test(expected = TechnischeException.class)
    public void testCheckZuziehendenKartenSpielRundeNull() {
        spielsteuerung.checkZuZiehendenKarten(null);
    }

    /**
     * Wenn zwei Spieler oder mehr sind in einer Spielrunde angemeldet, soll den Spieler zurück gegeben werden,
     * der gerade daran ist.
     */
    @Test
    public void testfragWerDaranIstSpieler1Daran() {

        assertEquals(spieler1, spielsteuerung.fragWerDranIst(spielrunde.getSpielerListe()));
    }

    /**
     * Wenn keiner oder nur ein Spieler in einer Spielrunde angemeldet ist, soll die Methode Exception werfen
     */
    @Test(expected = TechnischeException.class)
    public void testfragWerDaranIstSpielendIstNichtGesetzt() {
        List<Spieler> spielerListe = new ArrayList<Spieler>();
        spielerListe.add(new Spieler("Ido"));
        spielerListe.add(new Spieler("Victor"));
        spielerListe.add(new Spieler("Lucas"));
        spielsteuerung.fragWerDranIst(spielerListe);
    }

    /**
     * Wenn keiner oder nur ein Spieler in einer Spielrunde angemeldet ist, soll die Methode Exception werfen
     */
    @Test(expected = TechnischeException.class)
    public void testfragWerDaranIstNurEinSpieler() {
        List<Spieler> spielerListe = new ArrayList<Spieler>();
        spielerListe.add(new Spieler(new ArrayList<>(0), "Ido", true));
        spielsteuerung.fragWerDranIst(spielerListe);
    }

    /**
     * test ob die gespielte Karte anlegbar ist.
     */
    @Test
    public void testSpieleKarteErlaubteKarte() {

        Spielkarte aktuelleKarte = prepareSpieleKarte();

        assertTrue(spielsteuerung.spieleKarte(spieler1, aktuelleKarte, spielrunde, RegelKompTyp.OHNE_SONDER_REGEL));

    }

    /**
     * test ob die gespielte Karte anlegbar ist - Wenn Spieler Null ist, soll eine Exception gworfen werden
     */
    @Test(expected = TechnischeException.class)
    public void testSpieleKarteSpielerNULL() {

        Spielkarte aktuelleKarte = prepareSpieleKarte();

        assertTrue(spielsteuerung.spieleKarte(null, aktuelleKarte, spielrunde, RegelKompTyp.OHNE_SONDER_REGEL));

    }

    /**
     * test ob die gespielte Karte anlegbar ist - Wenn Spielershand Null ist, soll eine Exception gworfen werden
     */
    @Test(expected = TechnischeException.class)
    public void testSpieleKarteSpielershandNULL() {

        Spielkarte aktuelleKarte = prepareSpieleKarte();

        spieler1.setHand(null);

        spielsteuerung.spieleKarte(spieler1, aktuelleKarte, spielrunde, RegelKompTyp.OHNE_SONDER_REGEL);

    }

    /**
     * test ob die gespielte Karte anlegbar ist - Wenn die aktuelle Karte Null ist, soll eine Exception gworfen werden
     */
    @Test(expected = TechnischeException.class)
    public void testSpieleKarteAktuelleKarteNULL() {

        spielsteuerung.spieleKarte(spieler1, null, spielrunde, RegelKompTyp.OHNE_SONDER_REGEL);

    }

    /**
     * test ob die gespielte Karte anlegbar ist - Wenn die Spielrunde Null ist, soll eine Exception gworfen werden
     */
    @Test(expected = TechnischeException.class)
    public void testSpieleKarteSpielrundeNULL() {

        Spielkarte aktuelleKarte = prepareSpieleKarte();


        spielsteuerung.spieleKarte(spieler1, aktuelleKarte, null, RegelKompTyp.OHNE_SONDER_REGEL);

    }

    /**
     * test ob die gespielte Karte anlegbar ist - Wenn die gewählte SpielRegel Null ist, soll eine Exception gworfen werden
     */
    @Test(expected = NullPointerException.class)
    public void testSpieleKarteRegelKompTypNULL() {

        Spielkarte aktuelleKarte = prepareSpieleKarte();

        spielsteuerung.spieleKarte(spieler1, aktuelleKarte, spielrunde, null);

    }

    private Spielkarte prepareSpieleKarte() {
        Spielkarte aktuelleKarte = new Spielkarte(Blattwert.Fuenf, Blatttyp.Karo);

        Spielkarte letzteAufgelegteKarte = spielrunde.getAufgelegtStapel().get(spielrunde.getAufgelegtStapel().size() - 1);

        spielrunde.setRundeFarbe(letzteAufgelegteKarte.getBlatttyp());

        Mockito.when(spielregelOhneSonderImpl.istKarteLegbar(letzteAufgelegteKarte, aktuelleKarte, letzteAufgelegteKarte.getBlatttyp(), false)).thenReturn(true);

        Mockito.when(spielregelOhneSonderImpl.holeAuswirkungVonKarte(aktuelleKarte, spielrunde.getSpielerListe(), 0)).thenReturn(regelComponentUtil);
        return aktuelleKarte;
    }

    /**
     * test ob die gespielte Karte anlegbar ist.
     */
    @Test
    public void testSpieleKarteUnerlaubteKarte() {

        Spielkarte aktuelleKarte = new Spielkarte(Blattwert.Sieben, Blatttyp.Karo);

        Mockito.when(spielregelOhneSonderImpl.istKarteLegbar(vorherigeKarte, aktuelleKarte, vorherigeKarte.getBlatttyp(), false)).thenReturn(false);

        assertFalse(spielsteuerung.spieleKarte(spieler1, aktuelleKarte, spielrunde, RegelKompTyp.OHNE_SONDER_REGEL));
    }

    /**
     * test ob eine gewuenschte Blatttyp in der Spielrunde aktualisiert ist
     */
    @Test
    public void testBestimmeBlatttyp() {

        Blatttyp gewuenschteBlatttyp = Blatttyp.Herz;

        assertNotEquals(gewuenschteBlatttyp, spielrunde.getRundeFarbe());

        spielsteuerung.bestimmeBlatttyp(gewuenschteBlatttyp, spielrunde);

        assertEquals(gewuenschteBlatttyp, spielrunde.getRundeFarbe());
    }

    /**
     * test die Methode BestimmeBlatttyp - wenn der übergebene Blatttyp null ist
     */
    @Test(expected = TechnischeException.class)
    public void testBestimmeBlatttypBlatttypNull() {

        Blatttyp gewuenschteBlatttyp = null;

        spielsteuerung.bestimmeBlatttyp(gewuenschteBlatttyp, spielrunde);
    }

    /**
     * test die Methode BestimmeBlatttyp - wenn die Spielrunde null ist
     */
    @Test(expected = TechnischeException.class)
    public void testBestimmeBlatttypSpielrundeNull() {

        Blatttyp gewuenschteBlatttyp = Blatttyp.Herz;

        spielsteuerung.bestimmeBlatttyp(gewuenschteBlatttyp, null);
    }

    /**
     * test das Ziehen-Prozess vom verdeckten Stapel in den Hand
     */
    @Test
    public void testZieheKartenVomStapel() {

        int anzahlZuZiehendeKarten = 2;

        int anzahlKartenImHand = spieler1.getHand().size();

        int anzahlKartenImVerdeckteStapel = spielrunde.getVerdeckteStapel().size();

        assertEquals(anzahlKartenImHand + anzahlZuZiehendeKarten,
                spielsteuerung.zieheKartenVomStapel(spieler1, anzahlZuZiehendeKarten, spielrunde).getHand().size());

        assertEquals(anzahlKartenImVerdeckteStapel - anzahlZuZiehendeKarten,
                spielrunde.getVerdeckteStapel().size());
    }

    /**
     * test das Ziehen-Prozess vom verdeckten Stapel in den Hand
     */
    @Test
    public void testZieheKartenVomStapelReloadVerdecktenStapel() {

        spielrunde.setVerdeckteStapel(new ArrayList<>(0));

        int anzahlZuZiehendeKarten = 2;

        int anzahlKartenImHand = spieler1.getHand().size();

        assertEquals(anzahlKartenImHand + anzahlZuZiehendeKarten,
                spielsteuerung.zieheKartenVomStapel(spieler1, anzahlZuZiehendeKarten, spielrunde).getHand().size());

        assertEquals(1, spielrunde.getAufgelegtStapel().size());

        assertEquals(0, spielrunde.getVerdeckteStapel().size());
    }

    /**
     * test das Ziehen-Prozess vom verdeckten Stapel in den Hand, wenn Spieler null ist
     */
    @Test(expected = TechnischeException.class)
    public void testZieheKartenVomStapelSpielerNULL() {

        spielsteuerung.zieheKartenVomStapel(null, 2, spielrunde);
    }

    /**
     * test das Ziehen-Prozess vom verdeckten Stapel in den Hand, wenn Spieler null ist
     */
    @Test(expected = TechnischeException.class)
    public void testZieheKartenVomStapelSpielershandNULL() {

        spieler1.setHand(null);

        spielsteuerung.zieheKartenVomStapel(spieler1, 2, spielrunde);
    }

    /**
     * test das Ziehen-Prozess vom verdeckten Stapel in den Hand, wenn Spielrunde null ist
     */
    @Test(expected = TechnischeException.class)
    public void testZieheKartenVomStapelSpielrundeNULL() {

        spielsteuerung.zieheKartenVomStapel(spieler1, 2, null);
    }

    /**
     * test ob eine Karte ein Wuenscher ist
     */
    @Test
    public void testPruefeObWuenscherNotWuenscher() {

        Mockito.when(spielregelOhneSonderImpl.pruefeObWuenscher(Mockito.any(Spielkarte.class))).thenReturn(false);

        Spielkarte spielkarte = new Spielkarte(Blattwert.Acht, Blatttyp.Karo);

        boolean isWuenscher = spielsteuerung.pruefeObWuenscher(spielkarte, RegelKompTyp.OHNE_SONDER_REGEL);

        assertFalse(isWuenscher);
    }

    /**
     * test ob eine Karte ein Wuenscher ist
     */
    @Test
    public void testPruefeObWuenscherMIT_BASIC_SONDER_REGEL() {

        Mockito.when(spielregelBasicSonder.pruefeObWuenscher(Mockito.any(Spielkarte.class))).thenReturn(true);

        Spielkarte spielkarte = new Spielkarte(Blattwert.Bube, Blatttyp.Karo);

        boolean isWuenscher = spielsteuerung.pruefeObWuenscher(spielkarte, RegelKompTyp.MIT_BASIC_SONDER_REGEL);

        assertTrue(isWuenscher);
    }

    /**
     * test ob eine Karte ein Wuenscher ist
     */
    @Test
    public void testPruefeObWuenscherALL_SONDER_REGEL() {

        Mockito.when(spielregelAlleSonder.pruefeObWuenscher(Mockito.any(Spielkarte.class))).thenReturn(true);

        Spielkarte spielkarte = new Spielkarte(Blattwert.Bube, Blatttyp.Karo);

        boolean isWuenscher = spielsteuerung.pruefeObWuenscher(spielkarte, RegelKompTyp.ALLE_SONDER_REGEL);

        assertTrue(isWuenscher);
    }


    /**
     * test ob eine Karte ein Wuenscher ist, wenn RegelKompTyp null ist
     */
    @Test(expected = TechnischeException.class)
    public void testPruefeObWuenscherMitRegelKompTypNULL() {

        Spielkarte spielkarte = new Spielkarte(Blattwert.Acht, Blatttyp.Karo);

        spielsteuerung.pruefeObWuenscher(spielkarte, null);
    }

    /**
     * test ob eine Karte ein Wuenscher ist, wenn Spielkarte null ist
     */
    @Test(expected = TechnischeException.class)
    public void testPruefeObWuenscherMitSpielkarteNULL(){

        spielsteuerung.pruefeObWuenscher(null, RegelKompTyp.OHNE_SONDER_REGEL);
    }

    /**
     * test der Fall, in dem der Spieler keine legbare Karten hat und der verdeckte Stapel leer ist.
     */
    @Test
    public void testCheckeObSpielerAusgesetztWird() {

        spielrunde.setVerdeckteStapel(new ArrayList<>(0));

        assertTrue(!spielrunde.getAufgelegtStapel().isEmpty());

        Spielkarte spielkarte = spielrunde.getAufgelegtStapel().get(0);
        ArrayList<Spielkarte> neuAufgelegter = new ArrayList<>(1);
        neuAufgelegter.add(spielkarte);

        spielrunde.setAufgelegtStapel(neuAufgelegter);

        assertEquals(0, spielrunde.getVerdeckteStapel().size());

        assertEquals(1, spielrunde.getAufgelegtStapel().size());

        assertTrue(spielsteuerung.checkeObSpielerAusgesetztWird(spielrunde, spieler1, RegelKompTyp.OHNE_SONDER_REGEL));

    }


}
