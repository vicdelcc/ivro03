package spielregel;

import komponenten.spielregel.export.ISpielregel;
import komponenten.spielregel.impl.SpielregelBasicSonderImpl;
import model.Spieler;
import model.Spielkarte;
import model.enums.Blatttyp;
import model.enums.Blattwert;
import model.exceptions.MauMauException;
import model.hilfsklassen.RegelComponentUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Enclosed.class)
public class SpielregelBasicSonderTest extends SpielregelTestBase{

    private static ISpielregel spielRegelService;

    @BeforeClass
    public static void initialize() {
        spielRegelService = new SpielregelBasicSonderImpl();
    }

    /**
     * Parametrisierter Teil der Test-Klasse (für die Methode istKarteLegbar)
     */
    @RunWith(Parameterized.class)
    public static class ParmeterizedPart {

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return getDataBasicSonder();
        }

        private Spielkarte beforeSpielkarte;
        private Spielkarte afterSpielkarte;
        private boolean istAuflegbar;
        private Blatttyp blatttyp;
        private boolean sindKartenZuZiehen;

        public ParmeterizedPart(Spielkarte beforeSpielkarte, Spielkarte afterSpielkarte, boolean istAuflegbar, Blatttyp blatttyp, boolean sindKartenZuZiehen) {
            super();
            this.beforeSpielkarte = beforeSpielkarte;
            this.afterSpielkarte = afterSpielkarte;
            this.istAuflegbar = istAuflegbar;
            this.blatttyp = blatttyp;
            this.sindKartenZuZiehen = sindKartenZuZiehen;
        }

        @Test
        public void testIstKarteAuflegbarSuccess() throws MauMauException {
            boolean legbar = istKarteLegbar(spielRegelService, beforeSpielkarte, afterSpielkarte, blatttyp, sindKartenZuZiehen);
            assertEquals(istAuflegbar, legbar);
        }
    }

    /**
     * Nicht parametrisierter Teil der Test-Klasse (für den rest der Methoden)
     */
    public static class NotParameteriedPart {

        @Test(expected = MauMauException.class)
        public void testIstKarteLegbarFailedVorherNull() throws MauMauException {
            istKarteLegbar(spielRegelService, null, new Spielkarte(Blattwert.Bube, Blatttyp.Herz), null,false );
        }

        @Test(expected = MauMauException.class)
        public void testIstKarteLegbarFailedDanachNull() throws MauMauException {
            istKarteLegbar(spielRegelService, new Spielkarte(Blattwert.Bube, Blatttyp.Herz), null, null,false );
        }

        @Test
        public void testPruefeObWuenscherTrue() throws MauMauException {
            boolean istWuenscher = istKarteWuenscher(spielRegelService, new Spielkarte(Blattwert.Bube, Blatttyp.Herz));
            assertTrue(istWuenscher);
        }

        @Test
        public void testPruefeObWuenscherFalse() throws MauMauException {
            boolean istWuenscher = istKarteWuenscher(spielRegelService, new Spielkarte(Blattwert.Sechs, Blatttyp.Herz));
            assertFalse(istWuenscher);
        }

        @Test(expected = MauMauException.class)
        public void testPruefeObWuenscherFailed() throws MauMauException {
            istKarteWuenscher(spielRegelService, null);
        }

        @Test(expected = MauMauException.class)
        public void testHoleAuswirkungFailedKarteNull() throws MauMauException {
            holeAuswirkungenFailedKarteNull(spielRegelService);
        }

        @Test(expected = MauMauException.class)
        public void testHoleAuswirkungFailedSpielerNull() throws MauMauException {
            holeAuswirkungenFailedSpielerNull(spielRegelService);
        }

        /**
         * Test für den Fall von ein 7 bzw. zwei-Ziehen-Regel
         *
         * @throws MauMauException - Falls einer von den übergebenen Parameter null war
         */
        @Test
        public void testHoleAuswirkungenZweiZiehen() throws MauMauException {
            List<Spieler> spielerListe = getDefaultSpielerListe();
            spielerListe.get(0).setSpielend(true);
            Spielkarte spielkarte = new Spielkarte(Blattwert.Sieben, Blatttyp.Herz);
            RegelComponentUtil util = spielRegelService.holeAuswirkungVonKarte(spielkarte, spielerListe, 0);
            assertNotNull(util);
            assertEquals(2, util.getAnzahlKartenZuZiehen());
            assertTrue(util.getSpielerListe().get(1).isSpielend());
        }

        /**
         * Test für den Fall von ein Ass bzw. Aussetzen-Regel, wenn der 1. Spieler auf spielend gesetzt ist
         *
         * @throws MauMauException - Falls einer von den übergebenen Parameter null war
         */
        @Test
        public void testHoleAuswirkungenAussetzenErsterSpielerTrue() throws MauMauException {
            List<Spieler> spielerListe = getDefaultSpielerListe();
            spielerListe.get(0).setSpielend(true);
            Spielkarte spielkarte = new Spielkarte(Blattwert.Ass, Blatttyp.Herz);
            RegelComponentUtil util = spielRegelService.holeAuswirkungVonKarte(spielkarte, spielerListe, 0);
            assertNotNull(util);
            assertEquals(0, util.getAnzahlKartenZuZiehen());
            assertTrue(util.getSpielerListe().get(2).isSpielend());
        }

        /**
         * Test für den Fall von ein Ass bzw. Aussetzen-Regel, wenn der vorletze Spieler auf spielend gesetzt ist
         *
         * @throws MauMauException - Falls einer von den übergebenen Parameter null war
         */
        @Test
        public void testHoleAuswirkungenAussetzenVorletzterSpielerTrue() throws MauMauException {
            List<Spieler> spielerListe = getDefaultSpielerListe();
            spielerListe.get(spielerListe.size()-2).setSpielend(true);
            Spielkarte spielkarte = new Spielkarte(Blattwert.Ass, Blatttyp.Herz);
            RegelComponentUtil util = spielRegelService.holeAuswirkungVonKarte(spielkarte, spielerListe, 0);
            assertNotNull(util);
            assertEquals(0, util.getAnzahlKartenZuZiehen());
            assertTrue(util.getSpielerListe().get(0).isSpielend());
        }

        /**
         * Test für den Fall von ein Ass bzw. Aussetzen-Regel, wenn der letzte Spieler auf spielend gesetzt ist
         *
         * @throws MauMauException - Falls einer von den übergebenen Parameter null war
         */
        @Test
        public void testHoleAuswirkungenAussetzenLetzterSpielerTrue() throws MauMauException {
            List<Spieler> spielerListe = getDefaultSpielerListe();
            spielerListe.get(spielerListe.size()-1).setSpielend(true);
            Spielkarte spielkarte = new Spielkarte(Blattwert.Ass, Blatttyp.Herz);
            RegelComponentUtil util = spielRegelService.holeAuswirkungVonKarte(spielkarte, spielerListe, 0);
            assertNotNull(util);
            assertEquals(0, util.getAnzahlKartenZuZiehen());
            assertTrue(util.getSpielerListe().get(1).isSpielend());
        }


        /**
         * Test für einen normalen holeAuswirkungen, wenn es nur 2 Spieler gibt
         *
         * @throws MauMauException - Falls einer von den übergebenen Parameter null war
         */
        @Test
        public void testHoleAuswirkungenAussetzenNurZweiSpieler() throws MauMauException {
            List<Spieler> spielerListe = getDefaultSpielerListe();
            spielerListe.remove(spielerListe.size()-1);
            spielerListe.get(0).setSpielend(true);
            Spielkarte spielkarte = new Spielkarte(Blattwert.Ass, Blatttyp.Herz);
            RegelComponentUtil util = spielRegelService.holeAuswirkungVonKarte(spielkarte, spielerListe, 0);
            assertNotNull(util);
            assertEquals(0, util.getAnzahlKartenZuZiehen());
            assertTrue(util.getSpielerListe().get(0).isSpielend());
        }

        /**
         * Test für den Fall von eine Bube bzw. Wünscher-Regel
         *
         * @throws MauMauException - Falls einer von den übergebenen Parameter null war
         */
        @Test
        public void testHoleAuswirkungenWuenscher() throws MauMauException {
            List<Spieler> spielerListe = getDefaultSpielerListe();
            spielerListe.get(0).setSpielend(true);
            Spielkarte spielkarte = new Spielkarte(Blattwert.Bube, Blatttyp.Herz);
            RegelComponentUtil util = spielRegelService.holeAuswirkungVonKarte(spielkarte, spielerListe, 0);
            assertNotNull(util);
            assertEquals(0, util.getAnzahlKartenZuZiehen());
            assertTrue(util.getSpielerListe().get(1).isSpielend());
        }
    }
}
