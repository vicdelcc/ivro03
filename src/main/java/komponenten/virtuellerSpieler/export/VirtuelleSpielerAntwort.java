package komponenten.virtuellerSpieler.export;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VirtuelleSpielerAntwort {

    private int spielkarteIndex;

    private boolean maumauAufgerufen;

    private boolean kartenZiehen;

}
