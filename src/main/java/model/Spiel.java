package model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.enums.RegelKompTyp;
import model.enums.SpielTyp;
import model.hilfsklassen.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private List<Spielrunde> spielrunden;

    public Spiel(SpielTyp spielTyp, RegelKompTyp regelKompTyp) {
        this.spielTyp = spielTyp;
        this.regelKompTyp = regelKompTyp;
        this.beginn = new Date();
        this.spielrunden = new ArrayList<>();
    }

}
