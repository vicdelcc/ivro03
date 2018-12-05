package komponenten.karten.impl;

import komponenten.karten.export.IKarten;
import model.Spielkarte;
import model.enums.Blatttyp;
import model.enums.Blattwert;
import model.exceptions.MauMauException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Komponent, der je nach Spieltyp, den Kartenstapel baut
 */
@Service
public class KartenImpl implements IKarten {

    @Override
    public List<Spielkarte> baueStapel(List<Blatttyp> blatttypen, List<Blattwert> blattwerten) throws MauMauException {

        if(blatttypen==null || blattwerten == null) {
            throw new MauMauException("Fehler");
        }

        List<Spielkarte> stapel = new ArrayList<>();
        for(Blatttyp blatttyp : Blatttyp.values()) {
            if(!blatttypen.contains(blatttyp)) {
                for(Blattwert blattwert : Blattwert.values()) {
                    if(!blattwerten.contains(blattwert)) {
                        stapel.add(new Spielkarte(blattwert, blatttyp));
                    }
                }
            }
        }

        //TODO vic ich habe es hinzugefügt
        Collections.shuffle(stapel, new Random(System.nanoTime()));
        return stapel;
    }

}
