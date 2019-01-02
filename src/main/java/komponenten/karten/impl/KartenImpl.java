package komponenten.karten.impl;

import komponenten.karten.export.IKarten;
import model.Spielkarte;
import model.enums.Blatttyp;
import model.enums.Blattwert;
import model.exceptions.TechnischeException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Komponent, der je nach übergebenen Parameter , den Kartenstapel baut
 */
@Service
public class KartenImpl implements IKarten {

    @Override
    public List<Spielkarte> baueStapel(List<Blatttyp> blatttypenNicht, List<Blattwert> blattwertenNicht) {

        if (blatttypenNicht == null) {
            throw new TechnischeException("Die Liste von übergebenen Blatttypen, die nicht verwendet werden soll, ist leer");
        } else if (blattwertenNicht == null) {
            throw new TechnischeException("Die Liste von übergebenen Blattwerten, die nicht verwendet werden soll, ist leer");
        }

        List<Spielkarte> stapel = new ArrayList<>();
        for (Blatttyp blatttyp : Blatttyp.values()) {
            if (!blatttypenNicht.contains(blatttyp)) {
                for (Blattwert blattwert : Blattwert.values()) {
                    if (!blattwertenNicht.contains(blattwert)) {
                        stapel.add(new Spielkarte(blattwert, blatttyp));
                    }
                }
            }
        }
        Collections.shuffle(stapel, new Random(System.nanoTime()));
        return stapel;
    }

}
