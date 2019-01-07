package spielregel;

import komponenten.spielregel.export.ISpielregel;
import komponenten.spielregel.impl.SpielregelBasicSonderImpl;
import komponenten.spielverwaltung.export.Spieler;
import komponenten.karten.export.Spielkarte;
import komponenten.karten.export.Blatttyp;
import komponenten.karten.export.Blattwert;
import util.exceptions.TechnischeException;
import komponenten.spielregel.export.RegelComponentUtil;
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
        public void testIstKarteAuflegbarSuccess() {
            boolean legbar = istKarteLegbar(spielRegelService, beforeSpielkarte, afterSpielkarte, blatttyp, sindKartenZuZiehen);
            assertEquals(istAuflegbar, legbar);
        }
    }

    /**
     * Nicht parametrisierter Teil der Test-Klasse (für den rest der Methoden)
     */
    public static class NotParameteriedPart {

        @Test(expected = TechnischeException.class)
        public void testIstKarteLegbarFailedVorherNull() {
            istKarteLegbar(spielRegelService, null, new Spielkarte(Blattwert.Bube, Blatttyp.Herz), null,false );
        }

        @Test(expected = TechnischeException.class)
        public void testIstKarteLegbarFailedDanachNull() {
            istKarteLegbar(spielRegelService, new Spielkarte(Blattwert.Bube, Blatttyp.Herz), null, null,false );
        }

        @Test
        public void testPruefeObWuenscherTrue() {
            boolean istWuenscher = istKarteWuenscher(spielRegelService, new Spielkarte(Blattwert.Bube, Blatttyp.Herz));
            assertTrue(istWuenscher);
        }

        @Test
        public void testPruefeObWuenscherFalse() {
            boolean istWuenscher = istKarteWuenscher(spielRegelService, new Spielkarte(Blattwert.Sechs, Blatttyp.Herz));
            assertFalse(istWuenscher);
        }

        @Test(expected = TechnischeException.class)
        public void testPruefeObWuenscherFailed() {
            istKarteWuenscher(spielRegelService, null);
        }

        @Test(expected = TechnischeException.class)
        public void testHoleAuswirkungFailedKarteNull() {
            holeAuswirkungenFailedKarteNull(spielRegelService);
        }

        @Test(expected = TechnischeException.class)
        public void testHoleAuswirkungFailedSpielerNull() {
            holeAuswirkungenFailedSpielerNull(spielRegelService);
        }

        /**
         * Test für den Fall von ein 7 bzw. zwei-Ziehen-Regel
         */
        @Test
        public void testHoleAuswirkungenZweiZiehen() {
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
         */
        @Test
        public void testHoleAuswirkungenAussetzenErsterSpielerTrue() {
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
         */
        @Test
        public void testHoleAuswirkungenAussetzenVorletzterSpielerTrue() {
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
         */
        @Test
        public void testHoleAuswirkungenAussetzenLetzterSpielerTrue() {
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
         */
        @Test
        public void testHoleAuswirkungenAussetzenNurZweiSpieler() {
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
         */
        @Test
        public void testHoleAuswirkungenWuenscher() {
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
