package komponenten.spielverwaltung.export;

import com.fasterxml.jackson.annotation.JsonIgnore;
import komponenten.karten.export.Spielkarte;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
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
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Spielkarte> spielkarten;

    @OneToOne
    @JsonIgnore
    private Spielrunde spielrunde;

    public Hand(List<Spielkarte> spielkarten, Spielrunde spielrunde) {
        this.spielkarten = spielkarten;
        this.spielrunde = spielrunde;
    }

}
