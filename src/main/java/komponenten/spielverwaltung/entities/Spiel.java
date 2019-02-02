package komponenten.spielverwaltung.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import util.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
public class Spiel extends BaseEntity {


    @Column
    @Enumerated(EnumType.STRING)
    private SpielTyp spielTyp;

    @Column
    @Enumerated(EnumType.STRING)
    private RegelKompTyp regelKompTyp;

    @Column
    private Date beginn;

    @Column
    private long dauer;

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "spiel", orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Spielrunde> spielrunden;

    public Spiel(SpielTyp spielTyp, RegelKompTyp regelKompTyp) {
        this.spielTyp = spielTyp;
        this.regelKompTyp = regelKompTyp;
        this.beginn = new Date();
        this.spielrunden = new ArrayList<>();
    }

}
