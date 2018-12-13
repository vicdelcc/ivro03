package spielregel;

import komponenten.spielregel.export.ISpielregel;
import model.Spieler;
import model.Spielkarte;
import model.enums.Blatttyp;
import model.enums.Blattwert;
import model.exceptions.MauMauException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Testbase für die Spielkarten-Kombinationen
 */
public abstract class SpielregelTestBase {

    /**
     * Spielkarte-Kombinationen für die Spielregel-Impl ohne Sonderregel
     *
     * @return Collection<Object                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               [                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               ]> - die Kombinationen in einem Collection-Object
     */
    public static Collection<Object[]> getDataOhneSonder() {
        Object[][] data = {{new Spielkarte(Blattwert.Acht, Blatttyp.Herz), new Spielkarte(Blattwert.Bube, Blatttyp.Herz), true, null, false},
                {new Spielkarte(Blattwert.Acht, Blatttyp.Herz), new Spielkarte(Blattwert.Acht, Blatttyp.Pik), true, null, false},
                {new Spielkarte(Blattwert.Acht, Blatttyp.Herz), new Spielkarte(Blattwert.Sieben, Blatttyp.Karo), false, null, false},
                {new Spielkarte(Blattwert.Zehn, Blatttyp.Kreuz), new Spielkarte(Blattwert.Dame, Blatttyp.Herz), false, null, false}};
        return Arrays.asList(data);
    }

    /**
     * Spielkarte-Kombinationen für die Spielregel-Impl mit Basic-Sonderregel (Zwei_ziehen(7), Aussetzen(Ass) und Wünscher(Bube))
     *
     * @return Collection<Object                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               [                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               ]> - die Kombinationen in einem Collection-Object
     */
    public static Collection<Object[]> getDataBasicSonder() {
        Collection<Object[]> dataList = getDataOhneSonder();
        Object[][] data = {
                // Kombination ALLES_ZIEHEN --> true
                {new Spielkarte(Blattwert.Sieben, Blatttyp.Herz), new Spielkarte(Blattwert.Sieben, Blatttyp.Herz), true, null, true},
                // Kombination ZWEI_ZIEHEN --> false
                {new Spielkarte(Blattwert.Sieben, Blatttyp.Herz), new Spielkarte(Blattwert.Sechs, Blatttyp.Herz), false, null, true},
                // Kombination ZWEI_ZIEHEN --> true
                {new Spielkarte(Blattwert.Sieben, Blatttyp.Herz), new Spielkarte(Blattwert.Sieben, Blatttyp.Herz), true, null, true},
                // Kombination WUENSCHER --> true
                {new Spielkarte(Blattwert.Bube, Blatttyp.Kreuz), new Spielkarte(Blattwert.Dame, Blatttyp.Herz), true, Blatttyp.Herz, false},
                // Kombination WUENSCHER --> false
                {new Spielkarte(Blattwert.Bube, Blatttyp.Kreuz), new Spielkarte(Blattwert.Dame, Blatttyp.Herz), false, Blatttyp.Karo, false}};
        Collection<Object[]> newList = Arrays.asList(data);
        Stream<Object[]> combinedStream = Stream.of(dataList, newList).flatMap(Collection::stream);
        return combinedStream.collect(Collectors.toList());
    }

    /**
     * Spielkarte-Kombinationen für die Spielregel-Impl mit Basic-Sonderregel (Zwei_ziehen(7), Aussetzen(Ass), Wünscher(Bube),
     * Richtungswechsel(9), Stopper(8) und Allesleger(10))
     *
     * @return Collection<Object                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               [                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               ]> - die Kombinationen in einem Collection-Object
     */
    public static Collection<Object[]> getDataAlleSonder() {
        Collection<Object[]> dataList = getDataBasicSonder();
        Object[][] data = {
                // Kombination RICHTUNGSWECHSEL (geht immer nach den normalen Regel)
                {new Spielkarte(Blattwert.Dame, Blatttyp.Kreuz), new Spielkarte(Blattwert.Neun, Blatttyp.Kreuz), true, null, false},
                // Kombination STOPPER --> true (auf ALLES_ZIEHEN)
                {new Spielkarte(Blattwert.Sieben, Blatttyp.Kreuz), new Spielkarte(Blattwert.Acht, Blatttyp.Kreuz), true, null, true},
                // Kombination STOPPER --> true (auf ALLES_ZIEHEN)
                {new Spielkarte(Blattwert.Sieben, Blatttyp.Kreuz), new Spielkarte(Blattwert.Acht, Blatttyp.Herz), true, null, true},
                // Kombination ALLESLEGER --> true
                {new Spielkarte(Blattwert.Sechs, Blatttyp.Kreuz), new Spielkarte(Blattwert.Sechs, Blatttyp.Herz), true, null, false},
                // Kombination ALLESLEGER --> false
                {new Spielkarte(Blattwert.Sieben, Blatttyp.Kreuz), new Spielkarte(Blattwert.Zehn, Blatttyp.Kreuz), false, null, true}};
        Collection<Object[]> newList = Arrays.asList(data);
        Stream<Object[]> combinedStream = Stream.of(dataList, newList).flatMap(Collection::stream);
        return combinedStream.collect(Collectors.toList());
    }


    /**
     * Super-Methode für den erfolgreichen und gescheiterten "prüfeObWünscher" für alle verschiedenen SpielRegel-Impls
     *
     * @param service    - die entsprechende SpielRegel-Impl
     * @param spielkarte - die zu spielende Spielkarte
     * @return boolean - ob die Spielkarte ein Wünscher ist oder nicht
     * @throws MauMauException
     */
    public static boolean istKarteWuenscher(ISpielregel service, Spielkarte spielkarte) throws MauMauException {
        return service.pruefeObWuenscher(spielkarte);
    }

    /**
     * Super-Methode für den erfolgreichen und gescheiterten "istKarteLegbar" für alle verschiedene Spielregel-Impls
     *
     * @param service              - die entsprechende SpielRegel-Impl
     * @param davor                - die Spielkarte, die im Zug davor gespielt wurde
     * @param danach               - die Spielkarte, die im aktuellen Zug gespielt werden soll
     * @param gewuenschterBlatttyp - falls vorhanden, der gewünschte Blatttyp
     * @return boolean - ob die Spielkarte legbar ist oder nicht
     * @throws MauMauException
     */
    public static boolean istKarteLegbar(ISpielregel service, Spielkarte davor, Spielkarte danach, Blatttyp gewuenschterBlatttyp, boolean sindKartenZuZiehen) throws MauMauException {
        return service.istKarteLegbar(davor, danach, gewuenschterBlatttyp, sindKartenZuZiehen);
    }


    /**
     * Super-Methode für den gescheiterten "holeAuswirkungen" für alle verschiedenen Spielregel-Impls wegen Spielkarte-Null
     *
     * @throws MauMauException
     */
    public static void holeAuswirkungenFailedKarteNull(ISpielregel service) throws MauMauException {
        service.holeAuswirkungVonKarte(null, new ArrayList<>(), 0);
    }

    /**
     * Super-Methode für den gescheiterten "holeAuswirkungen" für alle verschiedenen Spielregel-Impls wegen Spielerliste-Null
     *
     * @throws MauMauException
     */
    public static void holeAuswirkungenFailedSpielerNull(ISpielregel service) throws MauMauException {
        service.holeAuswirkungVonKarte(new Spielkarte(Blattwert.Bube, Blatttyp.Herz), null, 0);
    }

    /**
     * Generiert eine default-Spielerliste für alle Spielregel-Impls-Tests
     *
     * @return List<Spieler> - die generierte Lsite
     */
    public static List<Spieler> getDefaultSpielerListe() {
        List<Spieler> spielerListe = new ArrayList<>();
        spielerListe.add(new Spieler("Ido"));
        spielerListe.add(new Spieler("Victor"));
        spielerListe.add(new Spieler("Lucas"));
        return spielerListe;
    }

}
