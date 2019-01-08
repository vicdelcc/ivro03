// --------------------------------------------------------
// Code generated by Papyrus Java
// --------------------------------------------------------

package komponenten.spielverwaltung.export;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import komponenten.karten.export.Spielkarte;
import komponenten.karten.export.Blatttyp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import util.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Spielrunde extends BaseEntity {

    @ManyToOne
    @JsonIgnore
    private Spiel spiel;

    @Column
    private String gewinnerName;

    @Column
    private Date start;

    @Column
    private long dauer;

    // Stapel mit verdeckten karten
    @OneToOne(cascade = CascadeType.ALL,  orphanRemoval = true)

    private Stapel verdeckteStapel;

    // Stapel mit aufgelegten karten
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Stapel aufgelegtStapel;

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "spielrunde", orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Ergebnis> ergebnisListe;

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "spielrunde", orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Spieler> spielerListe;

    @Column
    @Enumerated(EnumType.STRING)
    private Blatttyp rundeFarbe;

    @Column
    private Integer zuZiehnKartenAnzahl;

    public Spielrunde(Spiel spiel, List<Spieler> spielerliste) {
        this.spiel = spiel;
        this.start = new Date();
        this.spielerListe = spielerliste;
        this.ergebnisListe = new ArrayList<>();
    }

}
