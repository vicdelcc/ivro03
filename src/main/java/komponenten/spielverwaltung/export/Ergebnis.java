
package komponenten.spielverwaltung.export;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import util.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Ergebnis extends BaseEntity {

    @Column
    private int Punkte;

    @ManyToOne
    @JsonIgnore
    private Spielrunde spielrunde;

    @OneToOne
    private Spieler spieler;


}
