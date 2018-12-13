package spielregel;

import komponenten.spielregel.export.ISpielregel;
import komponenten.spielregel.impl.SpielregelOhneSonderImpl;
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
public class SpielregelOhneSonderTest extends SpielregelTestBase {

    private static ISpielregel spielRegelService;

    @BeforeClass
    public static void initialize() {

        spielRegelService = new SpielregelOhneSonderImpl();
    }

    @RunWith(Parameterized.class)
    public static class ParmeterizedPart {
        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return getDataOhneSonder();
        }

        private Spielkarte beforeSpielkarte;
        private Spielkarte afterSpielkarte;
        private boolean istAuflegbar;
        private Blatttyp blatttyp;
        private boolean sindKarteZuZiehen;

        public ParmeterizedPart(Spielkarte beforeSpielkarte, Spielkarte afterSpielkarte, boolean istAuflegbar, Blatttyp blatttyp, boolean sindKarteZuZiehen) {
            super();
            this.beforeSpielkarte = beforeSpielkarte;
            this.afterSpielkarte = afterSpielkarte;
            this.istAuflegbar = istAuflegbar;
            this.blatttyp = blatttyp;
            this.sindKarteZuZiehen = sindKarteZuZiehen;
        }

        @Test
        public void testIstKarteLegbarSuccess() throws MauMauException {
            boolean legbar = istKarteLegbar(spielRegelService, beforeSpielkarte, afterSpielkarte, blatttyp, sindKarteZuZiehen);
            assertEquals(istAuflegbar,legbar);
        }
    }

    public static class NotParameteriedPart {

        /**
         * Test für den gescheiterten istKarteLegbar-Aufruf wegen vorherigeKarte-Null
         * @throws MauMauException
         */
        @Test(expected = MauMauException.class)
        public void testIstKarteLegbarFailedVorherNull() throws MauMauException {
            istKarteLegbar(spielRegelService, null, new Spielkarte(Blattwert.Bube, Blatttyp.Herz), null,false );
        }

        /**
         * Test für den gescheiterten istKarteLegbar-Aufruf wegen aktuelleKarte-Null
         * @throws MauMauException
         */
        @Test(expected = MauMauException.class)
        public void testIstKarteLegbarFailedDanachNull() throws MauMauException {
            istKarteLegbar(spielRegelService, new Spielkarte(Blattwert.Bube, Blatttyp.Herz), null, null,false );
        }

        /**
         * Test für den positiven PruefeWuenscher
         * @throws MauMauException
         */
        @Test
        public void testPruefeObWuenscherTrue() throws MauMauException {
            boolean istWuenscher = istKarteWuenscher(spielRegelService, new Spielkarte(Blattwert.Bube, Blatttyp.Herz));
            assertTrue(istWuenscher);
        }

        /**
         * Test für den negativen PruefeWuenscher
         * @throws MauMauException
         */
        @Test
        public void testPruefeObWuenscherFalse() throws MauMauException {
            boolean istWuenscher = istKarteWuenscher(spielRegelService, new Spielkarte(Blattwert.Sechs, Blatttyp.Herz));
            assertFalse(istWuenscher);
        }

        /**
         * Test für den gescheiterten pruefeObWuenscher-Aufruf wegen Karte-Null
         * @throws MauMauException
         */
        @Test(expected = MauMauException.class)
        public void testPruefeObWuenscherFailed() throws MauMauException {
            istKarteWuenscher(spielRegelService, null);
        }

        /**
         * Test für den gescheiterten holeAuswirkungen-Aufruf wegen Karte-Null
         * @throws MauMauException
         */
        @Test(expected = MauMauException.class)
        public void testHoleAuswirkungFailedKarteNull() throws MauMauException {
            holeAuswirkungenFailedKarteNull(spielRegelService);
        }

        /**
         * Test für den gescheiterten holeAuswirkungen-Aufruf wegen Spieler-Null
         * @throws MauMauException
         */
        @Test(expected = MauMauException.class)
        public void testHoleAuswirkungFailedSpielerNull() throws MauMauException {
            holeAuswirkungenFailedSpielerNull(spielRegelService);
        }

        /**
         * Test für die normale HoleAuswirkungen, der nächste Spieler sollte daran sein. Erster Spieler war auf true gesetzt
         *
         * @throws MauMauException
         */
        @Test
        public void testHoleAuswirkungenErsterSpielerTrue() throws MauMauException {
            List<Spieler> spielerListe = getDefaultSpielerListe();
            spielerListe.get(0).setSpielend(true);
            Spielkarte spielkarte = new Spielkarte(Blattwert.Sechs, Blatttyp.Herz);
            RegelComponentUtil util = spielRegelService.holeAuswirkungVonKarte(spielkarte, spielerListe, 0);
            assertNotNull(util);
            assertEquals(0, util.getAnzahlKartenZuZiehen());
            assertTrue(util.getSpielerListe().get(1).isSpielend());
        }

        /**
         * Test für die normale HoleAuswirkungen, der nächste Spieler sollte daran sein. Letzter Spieler war auf true gesetzt
         *
         * @throws MauMauException
         */
        @Test
        public void testHoleAuswirkungenLetzterSpielerTrue() throws MauMauException {
            List<Spieler> spielerListe = getDefaultSpielerListe();
            spielerListe.get(spielerListe.size()-1).setSpielend(true);
            Spielkarte spielkarte = new Spielkarte(Blattwert.Sechs, Blatttyp.Herz);
            RegelComponentUtil util = spielRegelService.holeAuswirkungVonKarte(spielkarte, spielerListe, 0);
            assertNotNull(util);
            assertEquals(0, util.getAnzahlKartenZuZiehen());
            assertTrue(util.getSpielerListe().get(0).isSpielend());
        }
    }
}
