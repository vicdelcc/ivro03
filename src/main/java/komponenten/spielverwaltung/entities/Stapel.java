package komponenten.spielverwaltung.entities;

import komponenten.karten.entities.Spielkarte;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import util.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Stapel extends BaseEntity {

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    List<Spielkarte> spielkarten;

    public Stapel(List<Spielkarte> spielkarten) {
        this.spielkarten = spielkarten;
    }
}
