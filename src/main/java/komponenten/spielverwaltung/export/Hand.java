package komponenten.spielverwaltung.export;

import komponenten.karten.export.Spielkarte;
import lombok.Data;
import lombok.NoArgsConstructor;
import util.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Hand extends BaseEntity {

    @ManyToMany
    private List<Spielkarte> spielkarten;

    @OneToOne
    private Spielrunde spielrunde;

    public Hand(List<Spielkarte> spielkarten, Spielrunde spielrunde) {
        this.spielkarten = spielkarten;
        this.spielrunde = spielrunde;
    }

}
