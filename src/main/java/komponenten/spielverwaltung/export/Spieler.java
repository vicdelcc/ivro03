package komponenten.spielverwaltung.export;

import lombok.Data;
import lombok.NoArgsConstructor;
import util.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Spieler extends BaseEntity {

    @ManyToOne
    private Spielrunde spielrunde;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hand> hands;

    @Column
    private String name;

    @Column
    private boolean spielend;

    public Spieler(Hand hand, String name, boolean spielend) {
        hands = new ArrayList<>();
        hands.add(hand);
        this.name = name;
        this.spielend = spielend;
    }

    public Spieler(String name) {
        this.name = name;
        hands = new ArrayList<>();
    }

}
