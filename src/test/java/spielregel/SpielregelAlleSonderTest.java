package spielregel;

import komponenten.spielregel.export.ISpielregel;
import komponenten.spielregel.impl.SpielregelAlleSonderImpl;
import model.Spieler;
import model.Spielkarte;
import model.enums.Blatttyp;
import model.enums.Blattwert;
import model.exceptions.TechnischeException;
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
public class SpielregelAlleSonderTest extends SpielregelTestBase {

    private static ISpielregel spielRegelService;

    @BeforeClass
    public static void initialize() {
        spielRegelService = new SpielregelAlleSonderImpl();
    }

    /**
     * Parametrisierter Teil der Test-Klasse (für die Methode istKarteLegbar)
     */
    @RunWith(Parameterized.class)
    public static class ParmeterizedPart {
        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return getDataAlleSonder();
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
        public void testIstKarteAuflegbarSuccess()  {
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
            istKarteLegbar(spielRegelService, null, new Spielkarte(Blattwert.Bube, Blatttyp.Herz), null, false);
        }

        @Test(expected = TechnischeException.class)
        public void testIstKarteLegbarFailedDanachNull() {
            istKarteLegbar(spielRegelService, new Spielkarte(Blattwert.Bube, Blatttyp.Herz), null, null, false);
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
         * Test für den Fall von ein 9 bzw. Richtungswechsel-Regel, wenn der 1. Spieler auf spielend gesetzt ist
         *
         * @throws TechnischeException - Falls einer von den übergebenen Parameter null war
         */
        @Test
        public void testHoleAuswirkungenRichtungswechselErsterSpielerTrue() {
            List<Spieler> spielerListe = getDefaultSpielerListe();
            spielerListe.get(0).setSpielend(true);
            Spielkarte spielkarte = new Spielkarte(Blattwert.Neun, Blatttyp.Herz);
            RegelComponentUtil util = spielRegelService.holeAuswirkungVonKarte(spielkarte, spielerListe, 0);
            assertNotNull(util);
            assertEquals(0, util.getAnzahlKartenZuZiehen());
            assertTrue(util.getSpielerListe().get(spielerListe.size() - 1).isSpielend());
        }

        /**
         * Test für den Fall von ein 9 bzw. Richtungswechsel-Regel, wenn nicht der 1. Spieler auf spielend gesetzt ist
         *
         * @throws TechnischeException - Falls einer von den übergebenen Parameter null war
         */
        @Test
        public void testHoleAuswirkungenRichtungswechselNichtErsterSpielerTrue() {
            List<Spieler> spielerListe = getDefaultSpielerListe();
            spielerListe.get(1).setSpielend(true);
            Spielkarte spielkarte = new Spielkarte(Blattwert.Neun, Blatttyp.Herz);
            RegelComponentUtil util = spielRegelService.holeAuswirkungVonKarte(spielkarte, spielerListe, 0);
            assertNotNull(util);
            assertEquals(0, util.getAnzahlKartenZuZiehen());
            assertTrue(util.getSpielerListe().get(0).isSpielend());
        }

        /**
         * Test für den Fall von ein 9 bzw. Richtungswechsel-Regel, wenn es nur 2 Spieler gibt
         *
         * @throws TechnischeException - Falls einer von den übergebenen Parameter null war
         */
        @Test
        public void testHoleAuswirkungenRichtungswechselNurZweiSpielerTrue() {
            List<Spieler> spielerListe = getDefaultSpielerListe();
            spielerListe.remove(spielerListe.size() - 1);
            spielerListe.get(0).setSpielend(true);
            Spielkarte spielkarte = new Spielkarte(Blattwert.Neun, Blatttyp.Herz);
            RegelComponentUtil util = spielRegelService.holeAuswirkungVonKarte(spielkarte, spielerListe, 0);
            assertNotNull(util);
            assertEquals(0, util.getAnzahlKartenZuZiehen());
            assertTrue(util.getSpielerListe().get(0).isSpielend());
        }

        /**
         * Test für den Fall von ein 8 bzw. Stopper-Regel
         *
         * @throws TechnischeException - Falls einer von den übergebenen Parameter null war
         */
        @Test
        public void testHoleAuswirkungenStopper() {
            List<Spieler> spielerListe = getDefaultSpielerListe();
            spielerListe.get(0).setSpielend(true);
            Spielkarte spielkarte = new Spielkarte(Blattwert.Acht, Blatttyp.Herz);
            RegelComponentUtil util = spielRegelService.holeAuswirkungVonKarte(spielkarte, spielerListe, 2);
            assertNotNull(util);
            assertEquals(0, util.getAnzahlKartenZuZiehen());
            assertTrue(util.getSpielerListe().get(1).isSpielend());
        }

        /**
         * Test für den Fall von ein 10 bzw. Allesleger-Regel
         */
        @Test
        public void testHoleAuswirkungenAllesleger() {
            List<Spieler> spielerListe = getDefaultSpielerListe();
            spielerListe.get(0).setSpielend(true);
            Spielkarte spielkarte = new Spielkarte(Blattwert.Zehn, Blatttyp.Herz);
            RegelComponentUtil util = spielRegelService.holeAuswirkungVonKarte(spielkarte, spielerListe, 0);
            assertNotNull(util);
            assertEquals(0, util.getAnzahlKartenZuZiehen());
            assertTrue(util.getSpielerListe().get(1).isSpielend());
        }
    }
}
