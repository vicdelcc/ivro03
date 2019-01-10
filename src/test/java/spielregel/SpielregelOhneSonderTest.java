package spielregel;

import komponenten.spielregel.export.ISpielregel;
import komponenten.spielregel.impl.SpielregelOhneSonderImpl;
import komponenten.spielverwaltung.export.Spieler;
import komponenten.karten.export.Spielkarte;
import komponenten.karten.export.Blatttyp;
import komponenten.karten.export.Blattwert;
import komponenten.spielverwaltung.export.Spielrunde;
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
public class SpielregelOhneSonderTest extends SpielregelTestBase {

    private static ISpielregel spielRegelService;

    @BeforeClass
    public static void initialize() {

        spielRegelService = new SpielregelOhneSonderImpl();
    }

    /**
     * Parametrisierter Teil der Test-Klasse (für die Methode istKarteLegbar)
     */
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
        public void testIstKarteLegbarSuccess() {
            boolean legbar = istKarteLegbar(spielRegelService, beforeSpielkarte, afterSpielkarte, blatttyp, sindKarteZuZiehen);
            assertEquals(istAuflegbar,legbar);
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
         * Test für die normale holeAuswirkungen, Uhrzeiger-Richtung. Der nächste Spieler sollte daran sein. Erster Spieler war auf true gesetzt
         *
         * @- Falls einer von den übergebenen Parameter null war
         */
        @Test
        public void testHoleAuswirkungenErsterSpielerTrueUhrzeiger() {
            List<Spieler> spielerListe = getDefaultSpielerListe();
            spielerListe.get(0).setSpielend(true);
            Spielkarte spielkarte = new Spielkarte(Blattwert.Sechs, Blatttyp.Herz);
            Spielrunde spielrunde = new Spielrunde(null, spielerListe);
            spielrunde.setUhrzeiger(true);
            RegelComponentUtil util = spielRegelService.holeAuswirkungVonKarte(spielkarte, spielerListe, spielrunde);
            assertNotNull(util);
            assertEquals(0, util.getAnzahlKartenZuZiehen());
            assertTrue(util.getSpielerListe().get(1).isSpielend());
        }

        /**
         * Test für die normale holeAuswirkungen, Uhrzeiger-Richtung. Der nächste Spieler sollte daran sein. Letzter Spieler war auf true gesetzt
         *
         * @- Falls einer von den übergebenen Parameter null war
         */
        @Test
        public void testHoleAuswirkungenLetzterSpielerTrueUhrzeiger() {
            List<Spieler> spielerListe = getDefaultSpielerListe();
            spielerListe.get(spielerListe.size()-1).setSpielend(true);
            Spielkarte spielkarte = new Spielkarte(Blattwert.Sechs, Blatttyp.Herz);
            Spielrunde spielrunde = new Spielrunde(null, spielerListe);
            spielrunde.setUhrzeiger(true);
            RegelComponentUtil util = spielRegelService.holeAuswirkungVonKarte(spielkarte, spielerListe, spielrunde);
            assertNotNull(util);
            assertEquals(0, util.getAnzahlKartenZuZiehen());
            assertTrue(util.getSpielerListe().get(0).isSpielend());
        }


        /**
         * Test für die normale holeAuswirkungen, keine Uhrzeiger-Richtung. Der letzter Spieler sollte daran sein. Erster Spieler war auf true gesetzt
         *
         * @- Falls einer von den übergebenen Parameter null war
         */
        @Test
        public void testHoleAuswirkungenErsterSpielerTrueNichtUhrzeiger() {
            List<Spieler> spielerListe = getDefaultSpielerListe();
            spielerListe.get(0).setSpielend(true);
            Spielkarte spielkarte = new Spielkarte(Blattwert.Sechs, Blatttyp.Herz);
            Spielrunde spielrunde = new Spielrunde(null, spielerListe);
            spielrunde.setUhrzeiger(false);
            RegelComponentUtil util = spielRegelService.holeAuswirkungVonKarte(spielkarte, spielerListe, spielrunde);
            assertNotNull(util);
            assertEquals(0, util.getAnzahlKartenZuZiehen());
            assertTrue(util.getSpielerListe().get(util.getSpielerListe().size()-1).isSpielend());
        }

        /**
         * Test für die normale holeAuswirkungen, keine Uhrzeiger-Richtung. Der Spieler davor (index) sollte daran sein. Letzter Spieler war auf true gesetzt
         *
         * @- Falls einer von den übergebenen Parameter null war
         */
        @Test
        public void testHoleAuswirkungenLetzterSpielerTrueNichtUhrzeiger() {
            List<Spieler> spielerListe = getDefaultSpielerListe();
            spielerListe.get(spielerListe.size()-1).setSpielend(true);
            Spielkarte spielkarte = new Spielkarte(Blattwert.Sechs, Blatttyp.Herz);
            Spielrunde spielrunde = new Spielrunde(null, spielerListe);
            spielrunde.setUhrzeiger(false);
            RegelComponentUtil util = spielRegelService.holeAuswirkungVonKarte(spielkarte, spielerListe, spielrunde);
            assertNotNull(util);
            assertEquals(0, util.getAnzahlKartenZuZiehen());
            assertTrue(util.getSpielerListe().get(1).isSpielend());
        }
    }
}
