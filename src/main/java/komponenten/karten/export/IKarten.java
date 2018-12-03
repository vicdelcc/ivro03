package komponenten.karten.export;

import model.Spielkarte;
import model.enums.Blatttyp;
import model.enums.Blattwert;
import model.exceptions.MauMauException;

import java.util.List;

public interface IKarten {

    /**
     * Gib zurück einen Kartenstapel zum ausgewählten Spieltyp
     * @param blatttypen - die Liste von Blatttypen, die nicht verwendet werden sollen.
     * @param blattwerten - die Liste von Blattwerten, die nicht verwendet werden sollen.
     * @return List<Spielkarte> - der gebaute Stapel
     * @throws MauMauException
     */
    List<Spielkarte> baueStapel(List<Blatttyp> blatttypen, List<Blattwert> blattwerten) throws MauMauException;
}
