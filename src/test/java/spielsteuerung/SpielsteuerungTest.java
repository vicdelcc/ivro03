package spielsteuerung;

import komponenten.spielregel.export.ISpielregel;
import komponenten.spielsteuerung.export.ISpielsteuerung;
import komponenten.spielsteuerung.impl.SpielsteuerungImpl;
import model.Spieler;
import model.Spielkarte;
import model.Spielrunde;
import model.enums.Blatttyp;
import model.enums.Blattwert;
import model.exceptions.MauMauException;
import model.hilfsklassen.RegelComponentUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class SpielsteuerungTest {
    private static ISpielsteuerung spielSteuerung;
    private static Spieler spieler1;
    private static Spielrunde spielrunde;
    private static Spielkarte vorherigeKarte;

//    @Mock
//    private ISpielregel spielregel;

    private ISpielregel spielregel = Mockito.mock(ISpielregel.class);

    private static RegelComponentUtil regelComponentUtil;


    @BeforeClass
    public static void initialize() {
        spielSteuerung = new SpielsteuerungImpl();

        List<Spielkarte> hand = new ArrayList<>(1);
        hand.add(new Spielkarte(Blattwert.Drei, Blatttyp.Herz));
        spieler1 = new Spieler(hand, "spieler1", true);

        spielrunde = new Spielrunde();
        ArrayList<Spieler> spielerliste = new ArrayList<>(0);
        spielerliste.add(spieler1);
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

        regelComponentUtil= new RegelComponentUtil(spielrunde.getSpielerListe(), 0);
    }

    /**
     * Wenn ein Spieler eine Karte im Hand hat soll MauMau aufgerufen werden
     * @throws MauMauException
     */
    @Test
    public void testSollMauMauAufrufenLetzteKarte() throws MauMauException {

        //Spieler hat nur noch eine Karte im Hand
        assertEquals(1, spieler1.getHand().size());

        assertTrue(spielSteuerung.sollMauMauAufrufen(spieler1));
    }

    /**
     * Wenn ein Spieler mehr als eine Karte im Hand hat soll MauMau nicht aufgerufen werden
     * @throws MauMauException
     */
    @Test
    public void testSollMauMauAufrufenMehrAlsEineKarteImHand() throws MauMauException {

        //Fügt eine neue Karte zum Hand
        spieler1.getHand().add(new Spielkarte(Blattwert.Fuenf, Blatttyp.Herz));

        //Checkt ob der Hand mehr als eine Karte hat
        assertTrue(spieler1.getHand().size() > 1);

        assertFalse(spielSteuerung.sollMauMauAufrufen(spieler1));
    }

    /**
     * Check wie viele Karten soll vom verdeckten Stapel gezogen werden
     * @throws MauMauException
     */
    @Test
    public void testCheckZuziehendenKarten() throws MauMauException {
        int anzahlZuZiehendeKarten = 0;

        spielrunde.setZuZiehnKartenAnzahl(anzahlZuZiehendeKarten);

        assertEquals(anzahlZuZiehendeKarten, spielSteuerung.checkZuZiehendenKarten(spielrunde));
    }

    /**
     * Wenn keiner oder nur ein Spieler in einer Spielrunde angemeldet ist, soll die Methode Exception werfen
     * @throws MauMauException
     */
    @Test(expected = MauMauException.class)
    public void testfragWerDaranIstSpielendIstNichtGesetzt() throws MauMauException {
        List<Spieler> spielerListe = new ArrayList<Spieler>();
        spielerListe.add(new Spieler("Ido"));
        spielerListe.add(new Spieler("Victor"));
        spielerListe.add(new Spieler("Lucas"));
        spielSteuerung.fragWerDranIst(spielerListe);
    }

    /**
     * Wenn keiner oder nur ein Spieler in einer Spielrunde angemeldet ist, soll die Methode Exception werfen
     * @throws MauMauException
     */
    @Test(expected = MauMauException.class)
    public void testfragWerDaranIstNurEinSpieler() throws MauMauException {
        List<Spieler> spielerListe = new ArrayList<Spieler>();
        spielerListe.add(new Spieler(new ArrayList<>(0),"Ido", true));
        spielSteuerung.fragWerDranIst(spielerListe);
    }

    /**
     * Wenn zwei Spieler oder mehr sind in einer Spielrunde angemeldet, soll den Spieler zurück gegeben werden,
     * der gerade daran ist.
     * @throws MauMauException
     */
    @Test
    public void testfragWerDaranIstSpieler1Daran() throws MauMauException {

        Spieler spieler2 = new Spieler(new ArrayList<>(0), "spieler2", false);

        spielrunde.getSpielerListe().add(spieler2);

        assertEquals(spieler1, spielSteuerung.fragWerDranIst(spielrunde.getSpielerListe()));
    }

    /**
     * test ob die gespielte Karte anlegbar ist.
     * @throws MauMauException
     */
    @Test
    public void testSpieleKarteErlaubteKarte() throws MauMauException {

        Spielkarte aktuelleKarte = new Spielkarte(Blattwert.Fuenf, Blatttyp.Karo);

        Spielkarte letzteAufgelegteKarte = spielrunde.getAufgelegtStapel().get(spielrunde.getAufgelegtStapel().size() - 1);

        spielrunde.setRundeFarbe(letzteAufgelegteKarte.getBlatttyp());

        Mockito.when(spielregel.istKarteLegbar(letzteAufgelegteKarte,aktuelleKarte,letzteAufgelegteKarte.getBlatttyp())).thenReturn(true);

        Mockito.when(spielregel.holeAuswirkungVonKarte(aktuelleKarte, spielrunde.getSpielerListe())).thenReturn(regelComponentUtil);

        assertTrue(spielSteuerung.spieleKarte(spieler1, aktuelleKarte, spielrunde, spielregel));
    }

    /**
     * test ob die gespielte Karte anlegbar ist.
     * @throws MauMauException
     */
    @Test
    public void testSpieleKarteUnerlaubteKarte() throws MauMauException {

        Spielkarte aktuelleKarte = new Spielkarte(Blattwert.Sieben, Blatttyp.Karo);

        Mockito.when(spielregel.istKarteLegbar(vorherigeKarte,aktuelleKarte, vorherigeKarte.getBlatttyp())).thenReturn(false);

        assertFalse(spielSteuerung.spieleKarte(spieler1, aktuelleKarte, spielrunde,spielregel));
    }

    /**
     * test ob eine gewuenschte Blatttyp in der Spielrunde aktualisiert ist
     * @throws MauMauException
     */
    @Test
    public void testBestimmeBlatttyp() throws MauMauException {

        Blatttyp gewuenschteBlatttyp = Blatttyp.Herz;

        assertNotEquals(gewuenschteBlatttyp, spielrunde.getRundeFarbe());

        spielSteuerung.bestimmeBlatttyp(gewuenschteBlatttyp, spielrunde);

        assertEquals(gewuenschteBlatttyp, spielrunde.getRundeFarbe());
    }

    /**
     * test das Ziehen-Prozess vom verdeckten Stapel in den Hand
     * @throws MauMauException
     */
    @Test
    public void testZieheKartenVomStapel() throws MauMauException {

        int anzahlZuZiehendeKarten = 2;

        int anzahlKartenImHand = spieler1.getHand().size();

        int anzahlKartenImVerdeckteStapel = spielrunde.getVerdeckteStapel().size();

        assertEquals(anzahlKartenImHand + anzahlZuZiehendeKarten,
                spielSteuerung.zieheKartenVomStapel(spieler1, anzahlZuZiehendeKarten, spielrunde).getHand().size());

        assertEquals(anzahlKartenImVerdeckteStapel-anzahlZuZiehendeKarten,
                spielrunde.getVerdeckteStapel().size());
    }

    /**
     * test ob eine Karte ein Wuenscher ist
     * @throws MauMauException
     */
    @Test
    public void testPruefeObWuenscher() throws MauMauException {

        Mockito.when(spielregel.pruefeObWuenscher(Mockito.any(Spielkarte.class))).thenReturn(true);

        Spielkarte spielkarte = new Spielkarte(Blattwert.Bube, Blatttyp.Karo);

        boolean isWuenscher = spielSteuerung.pruefeObWuenscher(spielkarte, spielregel);

        assertTrue(isWuenscher);
    }

    /**
     * test ob eine Karte ein Wuenscher ist
     * @throws MauMauException
     */
    @Test
    public void testPruefeObWuenscherNotWuenscher() throws MauMauException {

        Mockito.when(spielregel.pruefeObWuenscher(Mockito.any(Spielkarte.class))).thenReturn(false);

        Spielkarte spielkarte = new Spielkarte(Blattwert.Acht, Blatttyp.Karo);

        boolean isWuenscher = spielSteuerung.pruefeObWuenscher(spielkarte, spielregel);

        assertFalse(isWuenscher);
    }
}
