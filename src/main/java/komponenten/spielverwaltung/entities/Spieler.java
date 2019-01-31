package komponenten.spielverwaltung.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import komponenten.karten.entities.Spielkarte;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import util.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Spieler extends BaseEntity {

    @ManyToOne
    @JsonIgnore
    private Spielrunde spielrunde;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Spielkarte> hand;

    @Column
    private String name;

    @Column
    private boolean spielend;

    @Column
    private boolean virtuellerSpieler;

    public Spieler(List<Spielkarte> hand, String name, boolean spielend) {
        this.hand = hand;
        this.name = name;
        this.spielend = spielend;
    }

    public Spieler(String name, boolean virtuellerSpieler) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.virtuellerSpieler = virtuellerSpieler;
    }

}
