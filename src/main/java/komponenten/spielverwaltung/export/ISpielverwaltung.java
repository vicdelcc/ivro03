package komponenten.spielverwaltung.export;

import komponenten.spielverwaltung.entities.*;
import util.exceptions.TechnischeException;

import java.util.List;

public interface ISpielverwaltung {

    /**
     * Erstellt ein neues Spiel mit dem übergebenen Spieltyp und Regelkomponententyp
     *
     * @param spielTyp     - der gewählte Spieltyp
     * @param regelKompTyp - der gewählte Regelkomponententyp
     * @return Spiel - das erstellte Spiel
     * @throws TechnischeException - Falls einer von den übergebenen Parameter null ist
     */
    Spiel starteNeuesSpiel(SpielTyp spielTyp, RegelKompTyp regelKompTyp);

    /**
     * Erstellt eine Spielrunde, registriert die Spieler in der Spielrunde, wählt einen Spieler, der als Erstes daran
     * kommt, verdeckter und aufgelegter Stapel werden generiert und die Initialkarten werden an alle Spieler verteilt.
     *
     * @param spielerListe - die Liste mit den Spieler, die in der Spielrunde registriert werden sollen
     * @param spiel        - das Spiel zu dem die Spielrunde gehört
     * @return Spielrunde - die erstellte Spielrunde
     * @throws TechnischeException - Falls einer von den übergebenen Parameter null ist
     */
    Spielrunde starteSpielrunde(List<Spieler> spielerListe, Spiel spiel);


    /**
     * Beendet eine Spielrunde bzw. berechnet die Ergebnisse aller Spieler je nach Kartenhand, berechnet die Dauer der
     * Spielrunde und ermittelt den Gewinnernamen
     *
     * @param spielrunde - die Spielrunde, die beendet werden soll
     * @return Spielrunde - die beendete Spielrunde
     * @throws TechnischeException - Falls die übergebene Spielrunde null ist
     */
    Spielrunde beendeSpielrunde(Spielrunde spielrunde);

    /**
     * Beendet ein Spiel, berechnet die Dauer des Spiels und persistert es in der Datenbank (mit allen abhängenden
     * Unterentitäten)
     *
     * @param spiel - das Spiel, das beendet werden soll
     * @return Spiel - das gespeicherte Spiel
     * @throws TechnischeException - Falls das übergebene Spiel null ist
     */
    Spiel beendeSpiel(Spiel spiel);
}
