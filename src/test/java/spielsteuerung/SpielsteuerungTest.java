package spielsteuerung;

import config.AppConfig;
import komponenten.spielregel.export.ISpielregel;
import komponenten.spielregel.impl.SpielregelOhneSonderImpl;
import komponenten.spielsteuerung.export.ISpielsteuerung;
import model.Spieler;
import model.Spielkarte;
import model.Spielrunde;
import model.enums.Blatttyp;
import model.enums.Blattwert;
import model.enums.RegelKompTyp;
import model.exceptions.MauMauException;
import model.hilfsklassen.RegelComponentUtil;
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
     *
     * @throws MauMauException
     */
    @Test
    public void testSollMauMauAufrufenLetzteKarte() throws MauMauException {

        //Spieler hat nur noch eine Karte im Hand
        assertEquals(1, spieler1.getHand().size());

        assertTrue(!spielrunde.getAufgelegtStapel().isEmpty());

        Spielkarte letzteAufgelegteKarte = spielrunde.getAufgelegtStapel().get(spielrunde.getAufgelegtStapel().size() - 1);

        Mockito.when(spielregelOhneSonderImpl.istKarteLegbar(letzteAufgelegteKarte, spieler1.getHand().get(0), letzteAufgelegteKarte.getBlatttyp(), false)).thenReturn(true);

        assertTrue(spielsteuerung.sollMauMauAufrufen(spielrunde, spieler1, RegelKompTyp.OHNE_SONDER_REGEL));
    }

    /**
     * Wenn ein Spieler mehr als eine Karte im Hand hat soll MauMau nicht aufgerufen werden
     *
     * @throws MauMauException
     */
    @Test
    public void testSollMauMauAufrufenMehrAlsEineKarteImHand() throws MauMauException {

        //Fügt eine neue Karte zum Hand
        spieler1.getHand().add(new Spielkarte(Blattwert.Fuenf, Blatttyp.Herz));

        //Checkt ob der Hand mehr als eine Karte hat
        assertTrue(spieler1.getHand().size() > 1);

        assertFalse(spielsteuerung.sollMauMauAufrufen(spielrunde, spieler1, RegelKompTyp.OHNE_SONDER_REGEL));
    }

    /**
     * Wenn ein Spieler mehr als eine Karte im Hand hat soll MauMau nicht aufgerufen werden
     *
     * @throws MauMauException
     */
    @Test (expected = MauMauException.class)
    public void testSollMauMauAufrufenSpielerNull() throws MauMauException {

        assertFalse(spielsteuerung.sollMauMauAufrufen(spielrunde, null, RegelKompTyp.OHNE_SONDER_REGEL));
    }

    /**
     * Wenn ein Spieler mehr als eine Karte im Hand hat soll MauMau nicht aufgerufen werden
     *
     * @throws MauMauException
     */
    @Test (expected = MauMauException.class)
    public void testSollMauMauAufrufenSpielershandNull() throws MauMauException {

        spieler1.setHand(null);

        assertFalse(spielsteuerung.sollMauMauAufrufen(spielrunde, spieler1, RegelKompTyp.OHNE_SONDER_REGEL));
    }

    /**
     * Wenn ein Spieler mehr als eine Karte im Hand hat soll MauMau nicht aufgerufen werden
     *
     * @throws MauMauException
     */
    @Test (expected = MauMauException.class)
    public void testSollMauMauAufrufenSpielershandLeer() throws MauMauException {

        spieler1.setHand(new ArrayList<>(0));

        assertFalse(spielsteuerung.sollMauMauAufrufen(spielrunde, spieler1, RegelKompTyp.OHNE_SONDER_REGEL));
    }

    /**
     * Check wie viele Karten soll vom verdeckten Stapel gezogen werden
     *
     * @throws MauMauException
     */
    @Test
    public void testCheckZuziehendenKarten() throws MauMauException {
        int anzahlZuZiehendeKarten = 0;

        spielrunde.setZuZiehnKartenAnzahl(anzahlZuZiehendeKarten);

        assertEquals(anzahlZuZiehendeKarten, spielsteuerung.checkZuZiehendenKarten(spielrunde));
    }

    /**
     * Wenn ein Spielrunde == null übergeben wird, soll MauMauException zurückgegeben
     *
     * @throws MauMauException
     */
    @Test(expected = MauMauException.class)
    public void testCheckZuziehendenKartenSpielRundeNull() throws MauMauException {
        spielsteuerung.checkZuZiehendenKarten(null);
    }

    /**
     * Wenn keiner oder nur ein Spieler in einer Spielrunde angemeldet ist, soll die Methode Exception werfen
     *
     * @throws MauMauException
     */
    @Test(expected = MauMauException.class)
    public void testfragWerDaranIstSpielendIstNichtGesetzt() throws MauMauException {
        List<Spieler> spielerListe = new ArrayList<Spieler>();
        spielerListe.add(new Spieler("Ido"));
        spielerListe.add(new Spieler("Victor"));
        spielerListe.add(new Spieler("Lucas"));
        spielsteuerung.fragWerDranIst(spielerListe);
    }

    /**
     * Wenn keiner oder nur ein Spieler in einer Spielrunde angemeldet ist, soll die Methode Exception werfen
     *
     * @throws MauMauException
     */
    @Test(expected = MauMauException.class)
    public void testfragWerDaranIstNurEinSpieler() throws MauMauException {
        List<Spieler> spielerListe = new ArrayList<Spieler>();
        spielerListe.add(new Spieler(new ArrayList<>(0), "Ido", true));
        spielsteuerung.fragWerDranIst(spielerListe);
    }

    /**
     * Wenn zwei Spieler oder mehr sind in einer Spielrunde angemeldet, soll den Spieler zurück gegeben werden,
     * der gerade daran ist.
     *
     * @throws MauMauException
     */
    @Test
    public void testfragWerDaranIstSpieler1Daran() throws MauMauException {

        assertEquals(spieler1, spielsteuerung.fragWerDranIst(spielrunde.getSpielerListe()));
    }

    /**
     * test ob die gespielte Karte anlegbar ist.
     *
     * @throws MauMauException
     */
    @Test
    public void testSpieleKarteErlaubteKarte() throws MauMauException {

        Spielkarte aktuelleKarte = prepareSpieleKarte();

        assertTrue(spielsteuerung.spieleKarte(spieler1, aktuelleKarte, spielrunde, RegelKompTyp.OHNE_SONDER_REGEL));

    }

    /**
     * test ob die gespielte Karte anlegbar ist, wenn spieler Null ist
     *
     * @throws MauMauException
     */
    @Test(expected = MauMauException.class)
    public void testSpieleKarteSpielerNULL() throws MauMauException {

        Spielkarte aktuelleKarte = prepareSpieleKarte();

        assertTrue(spielsteuerung.spieleKarte(null, aktuelleKarte, spielrunde, RegelKompTyp.OHNE_SONDER_REGEL));

    }

    /**
     * test ob die gespielte Karte anlegbar ist, wenn Spielershand Null ist
     *
     * @throws MauMauException
     */
    @Test(expected = MauMauException.class)
    public void testSpieleKarteSpielershandNULL() throws MauMauException {

        Spielkarte aktuelleKarte = prepareSpieleKarte();

        spieler1.setHand(null);

        spielsteuerung.spieleKarte(spieler1, aktuelleKarte, spielrunde, RegelKompTyp.OHNE_SONDER_REGEL);

    }

    /**
     * test ob die gespielte Karte anlegbar ist, wenn aktuelle Spielkarte Null ist
     *
     * @throws MauMauException
     */
    @Test(expected = MauMauException.class)
    public void testSpieleKarteAktuelleKarteNULL() throws MauMauException {

        spielsteuerung.spieleKarte(spieler1, null, spielrunde, RegelKompTyp.OHNE_SONDER_REGEL);

    }

    /**
     * test ob die gespielte Karte anlegbar ist, wenn Spielrunde Null ist
     *
     * @throws MauMauException
     */
    @Test(expected = MauMauException.class)
    public void testSpieleKarteSpielrundeNULL() throws MauMauException {

        Spielkarte aktuelleKarte = prepareSpieleKarte();


        spielsteuerung.spieleKarte(spieler1, aktuelleKarte, null, RegelKompTyp.OHNE_SONDER_REGEL);

    }

    /**
     * test ob die gespielte Karte anlegbar ist, wenn RegelKompTyp ist null
     *
     * @throws MauMauException
     */
    @Test(expected = Exception.class)
    public void testSpieleKarteRegelKompTypNULL() throws MauMauException {

        Spielkarte aktuelleKarte = prepareSpieleKarte();


        spielsteuerung.spieleKarte(spieler1, aktuelleKarte, spielrunde, null);

    }

    private Spielkarte prepareSpieleKarte() throws MauMauException {
        Spielkarte aktuelleKarte = new Spielkarte(Blattwert.Fuenf, Blatttyp.Karo);

        Spielkarte letzteAufgelegteKarte = spielrunde.getAufgelegtStapel().get(spielrunde.getAufgelegtStapel().size() - 1);

        spielrunde.setRundeFarbe(letzteAufgelegteKarte.getBlatttyp());

        Mockito.when(spielregelOhneSonderImpl.istKarteLegbar(letzteAufgelegteKarte, aktuelleKarte, letzteAufgelegteKarte.getBlatttyp(), false)).thenReturn(true);

        Mockito.when(spielregelOhneSonderImpl.holeAuswirkungVonKarte(aktuelleKarte, spielrunde.getSpielerListe(), 0)).thenReturn(regelComponentUtil);
        return aktuelleKarte;
    }

    /**
     * test ob die gespielte Karte anlegbar ist.
     *
     * @throws MauMauException
     */
    @Test
    public void testSpieleKarteUnerlaubteKarte() throws MauMauException {

        Spielkarte aktuelleKarte = new Spielkarte(Blattwert.Sieben, Blatttyp.Karo);

        Mockito.when(spielregelOhneSonderImpl.istKarteLegbar(vorherigeKarte, aktuelleKarte, vorherigeKarte.getBlatttyp(), false)).thenReturn(false);

        assertFalse(spielsteuerung.spieleKarte(spieler1, aktuelleKarte, spielrunde, RegelKompTyp.OHNE_SONDER_REGEL));
    }

    /**
     * test ob eine gewuenschte Blatttyp in der Spielrunde aktualisiert ist
     *
     * @throws MauMauException
     */
    @Test
    public void testBestimmeBlatttyp() throws MauMauException {

        Blatttyp gewuenschteBlatttyp = Blatttyp.Herz;

        assertNotEquals(gewuenschteBlatttyp, spielrunde.getRundeFarbe());

        spielsteuerung.bestimmeBlatttyp(gewuenschteBlatttyp, spielrunde);

        assertEquals(gewuenschteBlatttyp, spielrunde.getRundeFarbe());
    }

    /**
     * test die Methode BestimmeBlatttyp - wenn der übergebene Blatttyp null ist
     *
     * @throws MauMauException
     */
    @Test(expected = MauMauException.class)
    public void testBestimmeBlatttypBlatttypNull() throws MauMauException {

        Blatttyp gewuenschteBlatttyp = null;

        spielsteuerung.bestimmeBlatttyp(gewuenschteBlatttyp, spielrunde);
    }

    /**
     * test die Methode BestimmeBlatttyp - wenn die Spielrunde null ist
     *
     * @throws MauMauException
     */
    @Test(expected = MauMauException.class)
    public void testBestimmeBlatttypSpielrundeNull() throws MauMauException {

        Blatttyp gewuenschteBlatttyp = Blatttyp.Herz;

        spielsteuerung.bestimmeBlatttyp(gewuenschteBlatttyp, null);
    }

    /**
     * test das Ziehen-Prozess vom verdeckten Stapel in den Hand
     *
     * @throws MauMauException
     */
    @Test
    public void testZieheKartenVomStapel() throws MauMauException {

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
     *
     * @throws MauMauException
     */
    @Test
    public void testZieheKartenVomStapelReloadVerdecktenStapel() throws MauMauException {

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
     *
     * @throws MauMauException
     */
    @Test(expected = MauMauException.class)
    public void testZieheKartenVomStapelSpielerNULL() throws MauMauException {

        spielsteuerung.zieheKartenVomStapel(null, 2, spielrunde);
    }

    /**
     * test das Ziehen-Prozess vom verdeckten Stapel in den Hand, wenn Spieler null ist
     *
     * @throws MauMauException
     */
    @Test(expected = MauMauException.class)
    public void testZieheKartenVomStapelSpielershandNULL() throws MauMauException {

        spieler1.setHand(null);

        spielsteuerung.zieheKartenVomStapel(spieler1, 2, spielrunde);
    }

    /**
     * test das Ziehen-Prozess vom verdeckten Stapel in den Hand, wenn Spielrunde null ist
     *
     * @throws MauMauException
     */
    @Test(expected = MauMauException.class)
    public void testZieheKartenVomStapelSpielrundeNULL() throws MauMauException {

        spielsteuerung.zieheKartenVomStapel(spieler1, 2, null);
    }

    /**
     * test ob eine Karte ein Wuenscher ist
     *
     * @throws MauMauException
     */
    @Test
    public void testPruefeObWuenscherMIT_BASIC_SONDER_REGEL() throws MauMauException {

        Mockito.when(spielregelBasicSonder.pruefeObWuenscher(Mockito.any(Spielkarte.class))).thenReturn(true);

        Spielkarte spielkarte = new Spielkarte(Blattwert.Bube, Blatttyp.Karo);

        boolean isWuenscher = spielsteuerung.pruefeObWuenscher(spielkarte, RegelKompTyp.MIT_BASIC_SONDER_REGEL);

        assertTrue(isWuenscher);
    }

    /**
     * test ob eine Karte ein Wuenscher ist
     *
     * @throws MauMauException
     */
    @Test
    public void testPruefeObWuenscherALL_SONDER_REGEL() throws MauMauException {

        Mockito.when(spielregelAlleSonder.pruefeObWuenscher(Mockito.any(Spielkarte.class))).thenReturn(true);

        Spielkarte spielkarte = new Spielkarte(Blattwert.Bube, Blatttyp.Karo);

        boolean isWuenscher = spielsteuerung.pruefeObWuenscher(spielkarte, RegelKompTyp.ALLE_SONDER_REGEL);

        assertTrue(isWuenscher);
    }

    /**
     * test ob eine Karte ein Wuenscher ist
     *
     * @throws MauMauException
     */
    @Test
    public void testPruefeObWuenscherNotWuenscher() throws MauMauException {

        Mockito.when(spielregelOhneSonderImpl.pruefeObWuenscher(Mockito.any(Spielkarte.class))).thenReturn(false);

        Spielkarte spielkarte = new Spielkarte(Blattwert.Acht, Blatttyp.Karo);

        boolean isWuenscher = spielsteuerung.pruefeObWuenscher(spielkarte, RegelKompTyp.OHNE_SONDER_REGEL);

        assertFalse(isWuenscher);
    }

    /**
     * test ob eine Karte ein Wuenscher ist, wenn RegelKompTyp null ist
     *
     * @throws MauMauException
     */
    @Test(expected = MauMauException.class)
    public void testPruefeObWuenscherMitRegelKompTypNULL() throws MauMauException {

        Spielkarte spielkarte = new Spielkarte(Blattwert.Acht, Blatttyp.Karo);

        spielsteuerung.pruefeObWuenscher(spielkarte, null);
    }

    /**
     * test ob eine Karte ein Wuenscher ist
     *
     * @throws MauMauException
     */
    @Test(expected = MauMauException.class)
    public void testPruefeObWuenscherMitSpielkarteNULL() throws MauMauException {

        spielsteuerung.pruefeObWuenscher(null, RegelKompTyp.OHNE_SONDER_REGEL);
    }

    /**
     * test der Fall, in dem der Spieler keine legbare Karten hat und der verdeckte Stapel leer ist.
     *
     * @throws MauMauException
     */
    @Test
    public void testCheckeObSpielerAusgesetztWird() throws MauMauException {

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
