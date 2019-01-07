package komponenten.spielregel.export;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import komponenten.spielverwaltung.export.Spieler;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RegelComponentUtil {

    private List<Spieler> spielerListe;

    private int anzahlKartenZuZiehen;

}
