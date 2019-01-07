
package komponenten.karten.export;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Spielkarte {

    private Blattwert blattwert;

    private Blatttyp blatttyp;

    @Override
    public String toString() {
        return blatttyp + " " + blattwert;
    }
}
