
package komponenten.karten.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import util.BaseEntity;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
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
