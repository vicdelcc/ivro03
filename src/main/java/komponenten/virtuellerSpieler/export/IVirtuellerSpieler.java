package komponenten.virtuellerSpieler.export;

import komponenten.spielverwaltung.export.Hand;
import komponenten.spielverwaltung.export.RegelKompTyp;
import komponenten.spielverwaltung.export.Spielrunde;

public interface IVirtuellerSpieler {

    /**
     * Virtuelle Spieler spielt eine Karte
     *
     * @param spielrunde          - die Spielrunde, in der der Zug gespielt wird
     * @param hand                - die Hand des virtuellen Spielers, der daran ist
     * @param gewaehlteSpielregel - die ausgewählte SpielregelKomponente
     * @return String - die Antwort vom virtuellen Spieler (Spielkarte, maumauAufrufen oder karteZiehen)
     */
    String spieleKarte(Spielrunde spielrunde, Hand hand, RegelKompTyp gewaehlteSpielregel);
}


