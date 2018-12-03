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

        public ParmeterizedPart(Spielkarte beforeSpielkarte, Spielkarte afterSpielkarte, boolean istAuflegbar, Blatttyp blatttyp) {
            super();
            this.beforeSpielkarte = beforeSpielkarte;
            this.afterSpielkarte = afterSpielkarte;
            this.istAuflegbar = istAuflegbar;
            this.blatttyp = blatttyp;
        }

        /**
         * Testet ob die obigen Kombinationen das erwartete Ergebnis liefern
         *
         * @throws MauMauException
         */
        @Test
        public void testIstKarteAuflegbarSuccess() throws MauMauException {
            boolean legbar = istKarteLegbar(spielRegelService, beforeSpielkarte, afterSpielkarte, blatttyp);
            assertEquals(istAuflegbar, legbar);
        }
    }

    public static class NotParameteriedPart {

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
        public void testIstKarteAuflegbarFailed() throws MauMauException {
            istKarteLegbar(spielRegelService, null, null, null);
        }

        @Test(expected = MauMauException.class)
        public void testHoleAuswirkungFailed() throws MauMauException {
            holeAuswirkungenFailed(spielRegelService);
        }

        /**
         * Test für den Fall von ein 7 bzw. zwei-Ziehen-Regel
         *
         * @throws MauMauException
         */
        @Test
        public void testHoleAuswirkungenZweiZiehen() throws MauMauException {
            List<Spieler> spielerListe = getDefaultSpielerListe();
            Spielkarte spielkarte = new Spielkarte(Blattwert.Sieben, Blatttyp.Herz);
            RegelComponentUtil util = spielRegelService.holeAuswirkungVonKarte(spielkarte, spielerListe);
            assertNotNull(util);
            assertEquals(2, util.getAnzahlKartenZuZiehen());
            assertTrue(util.getSpielerListe().get(1).isSpielend());
        }

        /**
         * Test für den Fall von ein Ass bzw. Aussetzen-Regel
         *
         * @throws MauMauException
         */
        @Test
        public void testHoleAuswirkungenAussetzen() throws MauMauException {
            List<Spieler> spielerListe = getDefaultSpielerListe();
            Spielkarte spielkarte = new Spielkarte(Blattwert.Ass, Blatttyp.Herz);
            RegelComponentUtil util = spielRegelService.holeAuswirkungVonKarte(spielkarte, spielerListe);
            assertNotNull(util);
            assertEquals(0, util.getAnzahlKartenZuZiehen());
            assertTrue(util.getSpielerListe().get(2).isSpielend());
        }

        /**
         * Test für den Fall von eine Bube bzw. Wünscher-Regel
         *
         * @throws MauMauException
         */
        @Test
        public void testHoleAuswirkungenWuenscher() throws MauMauException {
            List<Spieler> spielerListe = getDefaultSpielerListe();
            Spielkarte spielkarte = new Spielkarte(Blattwert.Bube, Blatttyp.Herz);
            RegelComponentUtil util = spielRegelService.holeAuswirkungVonKarte(spielkarte, spielerListe);
            assertNotNull(util);
            assertEquals(0, util.getAnzahlKartenZuZiehen());
            assertTrue(util.getSpielerListe().get(1).isSpielend());
        }
    }
}
