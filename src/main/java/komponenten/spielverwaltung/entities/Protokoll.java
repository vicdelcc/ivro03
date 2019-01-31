package komponenten.spielverwaltung.entities;

import komponenten.karten.entities.Blatttyp;
import lombok.Data;
import lombok.NoArgsConstructor;
import util.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@Data
@NoArgsConstructor
public class Protokoll extends BaseEntity {

    @Column
    private Long spielID;

    @Column
    private Long spielrundeID;

    @Column
    private String spielerName;

    @Column
    private String spielkarteDavor;

    @Column
    private String auswahlSpieler;

    @Column
    @Enumerated(EnumType.STRING)
    private Blatttyp rundefarbeDavor;

    @Column
    @Enumerated(EnumType.STRING)
    private Blatttyp rundefarbeDanach;

    @Column
    private int zieheKarteDavor;

    @Column
    private int zieheKarteDanach;

    @Column
    private boolean uhrzeigerDavor;

    @Column
    private boolean uhrzeigerDanach;

    @Column
    private int anzahlKartenInHandDavor;

    @Column
    private int anzahlKartenInHandDanach;


}
