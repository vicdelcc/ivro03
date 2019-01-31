package komponenten.virtuellerSpieler.export;

import komponenten.karten.entities.Blatttyp;
import komponenten.spielverwaltung.entities.RegelKompTyp;
import komponenten.spielverwaltung.entities.Spieler;
import komponenten.spielverwaltung.entities.Spielrunde;

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
     * @return Blatttyp - der vom virtuellen Spieler ausgesuchten Blatttyp
     */
    Blatttyp sucheBlatttypAus(Spieler spieler);
}


