package model.hilfsklassen;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import model.Spieler;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RegelComponentUtil {

    private List<Spieler> spielerListe;

    private int anzahlKartenZuZiehen;

}
