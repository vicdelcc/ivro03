
package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.enums.Blatttyp;
import model.enums.Blattwert;

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
        return blattwert + " " + blatttyp;
    }
}
