
package komponenten.karten.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import util.BaseEntity;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Spielkarte extends BaseEntity {

    @Column
    @Enumerated(EnumType.STRING)
    private Blattwert blattwert;

    @Column
    @Enumerated(EnumType.STRING)
    private Blatttyp blatttyp;

    @Override
    public String toString() {
        return blatttyp + " " + blattwert;
    }
}
