package komponenten.spielregel.export;

import komponenten.karten.export.Blatttyp;
import komponenten.karten.export.Spielkarte;
import komponenten.spielverwaltung.export.Spieler;
import komponenten.spielverwaltung.export.Spielrunde;
import util.exceptions.TechnischeException;

import java.util.List;

public interface ISpielregel {

    /**
     * Prüft ob eine Spielkarte auf eine andere Spielkarte legbar ist
     *
     * @param vorherigeSpielkarte - die Spielkarte, die im Zug davor gespielt wurde
     * @param aktuelleSpielkarte  - die Spielkarte, die im aktuellen Zug gespielt werden soll
     * @param blatttyp            - der Blatttyp, der bei einer Bube gewünscht wird
     * @param sindKartenZuZiehen  - ob Karten zu ziehen sind
     * @return boolean - Ob die Spielkarte legbar ist oder nicht
     * @throws TechnischeException - Falls einer von den übergebenen Parameter leer ist
     */
    boolean istKarteLegbar(Spielkarte vorherigeSpielkarte, Spielkarte aktuelleSpielkarte, Blatttyp blatttyp, boolean sindKartenZuZiehen);


    /**
     * Bestimmt die Auswirkungen von der gespielten Karte wie z.B. ob Karten im nächsten Zug zu ziehen sind sowie den
     * Spieler, der im nächsten Zug daran ist
     *
     * @param aktuelleSpielkarte - die Spielkarte, die im aktuellen Zug gespielt wird
     * @param spielerListe       - die Spielerliste
     * @param spielrunde         - die aktuelle Spielrunde
     * @return RegelComponentUtil - Util-Object, um die Spielerliste und die Anzahl von zu ziehenden karten zurückzugeben
     * @throws TechnischeException - Falls einer von den übergebenen Parameter null ist
     */
    RegelComponentUtil holeAuswirkungVonKarte(Spielkarte aktuelleSpielkarte, List<Spieler> spielerListe, Spielrunde spielrunde);

    /**
     * Prüft, ob die übergebene Spielkarte den Blattwert Bube hat
     *
     * @param spielkarte - die übergebene Spielkarte
     * @return boolean - Ob die Karte eine Bube ist bzw. die Funktion von Wünscher hat
     * @throws TechnischeException - Falls einer von den übergebenen Parameter null ist
     */
    boolean pruefeObWuenscher(Spielkarte spielkarte);
}


