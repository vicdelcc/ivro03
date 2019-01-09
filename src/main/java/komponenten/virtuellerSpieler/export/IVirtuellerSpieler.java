package komponenten.virtuellerSpieler.export;

import komponenten.karten.export.Blatttyp;
import komponenten.spielverwaltung.export.RegelKompTyp;
import komponenten.spielverwaltung.export.Spieler;
import komponenten.spielverwaltung.export.Spielrunde;

public interface IVirtuellerSpieler {

    /**
     * Virtueller Spieler spielt eine Karte
     *
     * @param spielrunde          - die Spielrunde, in der der Zug gespielt wird
     * @param spieler             - der virtuelle Spieler, der daran ist
     * @param gewaehlteSpielregel - die ausgewählte SpielregelKomponente
     * @return String - die Antwort vom virtuellen Spieler (Spielkarte, maumauAufrufen oder karteZiehen)
     */
    String spieleKarte(Spielrunde spielrunde, Spieler spieler, RegelKompTyp gewaehlteSpielregel);

    /**
     * Virtueller Spieler sucht einen Blatttyp aus
     *
     * @param spieler    - der virtuelle Spieler, der daran ist
     * @param spielrunde - die aktuelle Spielrunde
     * @return Blatttyp - der vom virtuellen Spieler ausgesuchten Blatttyp
     */
    Blatttyp sucheBlatttypAus(Spieler spieler, Spielrunde spielrunde);
}


