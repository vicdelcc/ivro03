package komponenten.karten.export;

import komponenten.karten.entities.Blatttyp;
import komponenten.karten.entities.Blattwert;
import komponenten.karten.entities.Spielkarte;
import util.exceptions.TechnischeException;

import java.util.List;

public interface IKarten {

    /**
     * Gib zurück einen Kartenstapel, der nicht die übergebenen Blatttypen bzw. -werten enthält
     *
     * @param blatttypenNicht  - die Liste von Blatttypen, die nicht verwendet werden sollen.
     * @param blattwertenNicht - die Liste von Blattwerten, die nicht verwendet werden sollen.
     * @return List<Spielkarte> - der gebaute Stapel
     * @throws TechnischeException - Falls einer von den übergebenen Parameter null ist
     */
    List<Spielkarte> baueStapel(List<Blatttyp> blatttypenNicht, List<Blattwert> blattwertenNicht);
}
