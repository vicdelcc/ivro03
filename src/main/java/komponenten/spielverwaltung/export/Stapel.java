package komponenten.spielverwaltung.export;

import komponenten.karten.export.Spielkarte;
import lombok.Data;
import lombok.NoArgsConstructor;
import util.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Stapel extends BaseEntity {

    @ManyToMany
    List<Spielkarte> spielkarten;

    public Stapel(List<Spielkarte> spielkarten) {
        this.spielkarten = spielkarten;
    }
}
