package komponenten.spielverwaltung.export;

import model.Ergebnis;
import model.Spiel;
import model.Spieler;
import model.Spielrunde;
import model.enums.RegelKompTyp;
import model.enums.SpielTyp;
import model.exceptions.MauMauException;

import java.util.List;

public interface ISpielverwaltung {

    /**
     * Erstellt ein neues Spiel mit dem übergebenen Spieltyp und Regelkomponententyp
     * @param spielTyp
     * @param regelKompTyp
     * @return
     * @throws MauMauException
     */
    Spiel starteNeuesSpiel(SpielTyp spielTyp, RegelKompTyp regelKompTyp) throws MauMauException;

    /**
     * Erstellt eine Spielrunde und registriert die Spieler in der Spielrunde
     * @param spielerListe
     * @param spiel
     * @return
     * @throws MauMauException
     */
    Spielrunde starteSpielrunde(List<Spieler> spielerListe, Spiel spiel) throws MauMauException;


    /**
     * Beendet eine Spielrunde bzw. berechnet die Ergebnisse aller Spieler je nach Kartenhand
     * @param spielrunde
     * @return
     * @throws MauMauException
     */
    List<Ergebnis> beendeSpielrunde(Spielrunde spielrunde) throws MauMauException;

    /**
     * Beendet ein Spiel und berechnet die Dauer des Spiels
     * @param spiel
     * @return
     * @throws MauMauException
     */
    Spiel beendeSpiel(Spiel spiel) throws MauMauException;
}
