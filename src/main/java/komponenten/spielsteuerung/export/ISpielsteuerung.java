package komponenten.spielsteuerung.export;

import komponenten.spielverwaltung.entities.Spieler;
import komponenten.karten.entities.Spielkarte;
import komponenten.spielverwaltung.entities.Spielrunde;
import komponenten.karten.entities.Blatttyp;
import komponenten.spielverwaltung.entities.RegelKompTyp;
import util.exceptions.TechnischeException;

import java.util.List;

public interface ISpielsteuerung {


    /**
     * Gibt den Spieler, der in der Spielrunde dran ist (Spielend true)
     *
     * @param spielerListe - Spielerliste der Spielrunde
     * @return Spieler - der Spieler, der dran ist.
     * @throws TechnischeException - wenn die Spielerliste weniger als 2 Spieler enthält, wenn mehr als
     *                         ein Spieler die Attribute 'Spielend' true hat oder wenn die Spielierliste nicht
     *                         initialisiert ist
     */
    Spieler fragWerDranIst(List<Spieler> spielerListe);

    /**
     * Prüft, ob der Spieler, der dran ist, Karten zu ziehen hat (wenn im Zug davor ein ZWEI_ZIEHEN gespielt wurde)
     *
     * @param spielrunde - jeweilige Spielrunde
     * @return - int - Anzahl der Karten, die zu ziehen sind
     * @throws TechnischeException - wenn Spielrunde nicht gesetzt wird
     */
    int checkZuZiehendenKarten(Spielrunde spielrunde);

    /**
     * Prüft ob Spielkarte auflegbar ist
     * Wenn ja, wird die Karte von der Hand des Spielers auf den aufgelegten Stapel gelegt und die Spielrundefarbe
     * wird neu gesetzt (falls ein Wünscher gespielt wurde)
     * Wenn die Karte eine Sonderfunktion hat, werden die Spielerliste und die Anzahl der zu ziehenden Karten
     * in der Spielrunde aktualisiert.
     *
     * @param spieler             - der Spieler, der den Zug spielt
     * @param spielkarte          - die gespielte Karte
     * @param spielrunde          - die Spielrunde, in der der Zug gespielt wird
     * @param gewaehlteSpielregel - die ausgewählte SpielregelKomponente
     * @return boolean - Wenn die Karte spielbar ist, wird true zurückgegeben, wenn nicht dann false
     * @throws TechnischeException - Wenn Spieler oder Spielerhand oder Spielkarte oder Spielrunde oder die ausgewählte
     *                         Rundenfarbe oder aufgelegter Stapel oder Spielregelkomponente null sind, wird die Exception geworfen.
     */
    boolean spieleKarte(Spieler spieler, Spielkarte spielkarte, Spielrunde spielrunde, RegelKompTyp gewaehlteSpielregel);

    /**
     * Prüft, ob der Spieler MauMau aufrufen sollte
     *
     * @param spielrunde          - die Spielrunde, in der der Zug gespielt wird
     * @param spieler             - Spieler, der dran ist
     * @param gewaehlteSpielregel - der von Benutzer gewünschte Spielregel-Typ
     * @return - boolean - True, wenn der Spieler MauMau aufrufen sollte, sonst false.
     * @throws TechnischeException - Wenn Spieler oder Spielerhand null sind, oder Spielerhand leer ist wird die
     *                         Exception geworfen werden.
     */
    boolean sollMauMauAufrufen(Spielrunde spielrunde, Spieler spieler, RegelKompTyp gewaehlteSpielregel);


    /**
     * Prüft ob die übergebene Spielkarte die Wünscher-Funktion besitzt
     *
     * @param spielkarte          - die gespielte Karte
     * @param gewaehlteSpielregel - die ausgewählte SpielregelKomponente
     * @return boolean - Wenn die Karte Wünscher ist, wird true zurückgegeben, sonst false
     * @throws TechnischeException - Wenn Spielkarte oder Spielregelkomponente null sind, wird die Exception geworfen.
     */
    boolean pruefeObWuenscher(Spielkarte spielkarte, RegelKompTyp gewaehlteSpielregel);

    /**
     * Der Spieler wählt einen Blatttyp und es wird in der Spielrunde festgelegt
     *
     * @param blatttyp   - vom Benutzer ausgewählten Blatttyp
     * @param spielrunde - die gespielte Spielrunde
     * @throws TechnischeException - Wenn Blatttyp oder Spielrunde null sind, wird die Exception geworfen.
     */
    void bestimmeBlatttyp(Blatttyp blatttyp, Spielrunde spielrunde);

    /**
     * Der Spieler zieht Karten vom verdeckten Stapel
     * Falls verdeckter Stapel nicht genug Karten enthält, wird der verdeckte Stapel mit den Karten vom
     * aufgelegten Stapel befüllt.
     *
     * @param spieler      - der Spieler, der Karten zieht
     * @param anzahlKarten - Anzahl der Karten, die gezogen werden sollen.
     * @param spielrunde   - die gespielte Spielrunde
     * @return Spieler - der Spieler mit der aktualisierten Hand.
     * @throws TechnischeException - Wenn Spieler oder Spielerhand oder Spielrunde oder verdeckter und aufgelegter Stapel
     *                         null sind, wird die Exception geworfen.
     */
    Spieler zieheKartenVomStapel(Spieler spieler, int anzahlKarten, Spielrunde spielrunde);

    /**
     * Für den Ausnahmefall, dass der Spieler keine Karte spielen kann und es sind keine Karten mehr zu ziehen übrig
     *
     * @param spielrunde          - die gespielte Spielrunde
     * @param spieler             - der Spieler, der Karten zieht
     * @param gewaehlteSpielregel - die ausgewählte SpielregelKomponente
     * @return true - wenn der Spieler ausgesetzt sein sollte. false wenn nicht.
     * @throws TechnischeException - Wenn der RegelKompTyp nicht erkannt wird oder keine Karten mehr im aufgelegten Stapel gibt.
     */
    boolean checkeObSpielerAusgesetztWird(Spielrunde spielrunde, Spieler spieler, RegelKompTyp gewaehlteSpielregel);
}
