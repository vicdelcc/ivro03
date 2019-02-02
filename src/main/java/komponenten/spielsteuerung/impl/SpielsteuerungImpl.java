package komponenten.spielsteuerung.impl;

import komponenten.karten.entities.Blatttyp;
import komponenten.karten.entities.Blattwert;
import komponenten.karten.entities.Spielkarte;
import komponenten.spielregel.export.ISpielregel;
import komponenten.spielregel.entities.RegelComponentUtil;
import komponenten.spielsteuerung.export.ISpielsteuerung;
import komponenten.spielverwaltung.entities.RegelKompTyp;
import komponenten.spielverwaltung.entities.Spieler;
import komponenten.spielverwaltung.entities.Spielrunde;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import util.exceptions.TechnischeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Komponent, der eine Spielrunde steuert
 */
@Service
public class SpielsteuerungImpl implements ISpielsteuerung {

    @Autowired
    @Qualifier("ohneSonder")
    private ISpielregel spielregelohneSonder;

    @Autowired
    @Qualifier("basicSonder")
    private ISpielregel spielregelBasicSonder;

    @Autowired
    @Qualifier("alleSonder")
    private ISpielregel spielregelAlleSonder;


    public Spieler fragWerDranIst(List<Spieler> spielerListe) {
        if (spielerListe == null) {
            throw new TechnischeException("Spielerliste ist nicht initialisiert");
        }
        if (spielerListe.size() < 2) {
            throw new TechnischeException("Spielerliste muss mehr als einen Spieler enthalten");
        }
        List<Spieler> spielerMitSpielend = spielerListe.stream()
                .filter(Spieler::isSpielend).collect(Collectors.toList());

        if (spielerMitSpielend.size() != 1) {
            throw new TechnischeException("Keine oder mehrere Spieler mit spielend true gesetzt");
        }
        return spielerMitSpielend.get(0);
    }

    public int checkZuZiehendenKarten(Spielrunde spielrunde) {
        if (spielrunde == null) {
            throw new TechnischeException("Spielrunde ist null");
        }
        if (spielrunde.getZuZiehnKartenAnzahl() == null) {
            spielrunde.setZuZiehnKartenAnzahl(0);
        }
        return spielrunde.getZuZiehnKartenAnzahl();
    }

    public boolean spieleKarte(Spieler spieler, Spielkarte spielkarte, Spielrunde spielrunde, RegelKompTyp gewaehlteSpielregel) {
        if (spieler == null) {
            throw new TechnischeException("Spieler ist null");
        }
        if (spieler.getHand() == null) {
            throw new TechnischeException("Spielershand ist null");
        }

        if (spielkarte == null) {
            throw new TechnischeException("Spielkarte ist null");
        }
        if (spielrunde == null) {
            throw new TechnischeException("Spielrunde ist null");
        }
        if (spielrunde.getAufgelegtStapel() == null || spielrunde.getAufgelegtStapel().getSpielkarten().isEmpty()) {
            throw new TechnischeException("Aufgelegter Stapel ist null oder leer");
        }
        if (spielrunde.getZuZiehnKartenAnzahl() == null) {
            spielrunde.setZuZiehnKartenAnzahl(0);
        }

        boolean istKarteLegbar;
        RegelComponentUtil regelComponentUtil = null;
        ISpielregel gewaehlteSpielRegelK = getGewaehlteSpielRegelKomponente(gewaehlteSpielregel);
        if (gewaehlteSpielRegelK != null) {
            istKarteLegbar = gewaehlteSpielRegelK.istKarteLegbar(getLetzteAufgelegteKarte(spielrunde.getAufgelegtStapel().getSpielkarten()), spielkarte, spielrunde.getRundeFarbe(), spielrunde.getZuZiehnKartenAnzahl() != 0);
            if (istKarteLegbar) {
                regelComponentUtil = gewaehlteSpielRegelK.holeAuswirkungVonKarte(spielkarte, spielrunde.getSpielerListe(), spielrunde);
                if(spielkarte.getBlattwert() == Blattwert.Neun) {
                    boolean uhrzeigerVorher = spielrunde.isUhrzeiger();
                    spielrunde.setUhrzeiger(!uhrzeigerVorher);
                }
            }
        } else {
            throw new TechnischeException("unbekannte SpielRegelKomponente wurde übergeben");
        }

        if (istKarteLegbar) {
            if (spielkarte.getBlattwert() == Blattwert.Acht) {
                spielrunde.setZuZiehnKartenAnzahl(0);
            }
            setztKarteVomHandAufDemAufgelegteStapel(spieler, spielkarte, spielrunde);
            spielrunde.setSpielerListe(regelComponentUtil.getSpielerListe());
            spielrunde.setZuZiehnKartenAnzahl(regelComponentUtil.getAnzahlKartenZuZiehen());
            if (spielrunde.getRundeFarbe() != null) {
                spielrunde.setRundeFarbe(null);
            }
            return true;
        } else {
            return false;
        }
    }


    public boolean sollMauMauAufrufen(Spielrunde spielrunde, Spieler spieler, RegelKompTyp regelKompTyp) {
        if (spieler == null) {
            throw new TechnischeException("Spieler ist null");
        }
        boolean sollMauMau = false;

        if (spieler.getHand() == null) {
            throw new TechnischeException("Spielershand ist null");
        }
        if (spieler.getHand().isEmpty()) {
            throw new TechnischeException("Spielershand ist leer, Spiel sollte schon beendet werden");
        }
        if (spieler.getHand().size() == 1) {
            sollMauMau = getGewaehlteSpielRegelKomponente(regelKompTyp).istKarteLegbar(getLetzteAufgelegteKarte(spielrunde.getAufgelegtStapel().getSpielkarten()), spieler.getHand().get(0), spielrunde.getRundeFarbe(), spielrunde.getZuZiehnKartenAnzahl() != 0);
        }


        return sollMauMau;
    }

    public boolean pruefeObWuenscher(Spielkarte spielkarte, RegelKompTyp gewaehlteSpielregel) {
        if (spielkarte == null) {
            throw new TechnischeException("Spielkarte ist null");
        }
        if (gewaehlteSpielregel == null) {
            throw new TechnischeException("gewaehlte Spielegel ist null");
        }

        return getGewaehlteSpielRegelKomponente(gewaehlteSpielregel).pruefeObWuenscher(spielkarte);

    }

    public void bestimmeBlatttyp(Blatttyp blatttyp, Spielrunde spielrunde) {
        if (blatttyp == null) {
            throw new TechnischeException("Blatttyp ist null");
        }
        if (spielrunde == null) {
            throw new TechnischeException("Spielrunde ist null");
        }
        spielrunde.setRundeFarbe(blatttyp);
    }

    public Spieler zieheKartenVomStapel(Spieler spieler, int anzahlKarten, Spielrunde spielrunde) {
        if (spieler == null) {
            throw new TechnischeException("Spieler ist null");
        }

        if (spieler.getHand() == null) {
            throw new TechnischeException("Spielershand ist null");
        }

        if (spielrunde == null) {
            throw new TechnischeException("Spielrunde ist null");
        }
        if (spielrunde.getVerdeckteStapel() == null) {
            throw new TechnischeException("Verdeckter Stapel ist null");
        }
        if (spielrunde.getAufgelegtStapel() == null || spielrunde.getAufgelegtStapel().getSpielkarten().isEmpty()) {
            throw new TechnischeException("Aufgelegter Stapel ist null oder leer");
        }

        List<Spielkarte> neueKarten = getNeueKartenVomVerdecktenStapelUndRemove(anzahlKarten, spielrunde);

        spieler.getHand().addAll(neueKarten);

        setSpielendToNextPlayer(spielrunde.getSpielerListe(), spielrunde);

        spielrunde.setZuZiehnKartenAnzahl(0);

        return spieler;
    }

    @Override
    public boolean checkeObSpielerAusgesetztWird(Spielrunde spielrunde, Spieler spieler, RegelKompTyp gewaehlteSpielregel) {
        boolean kartenSpielbar = false;

        for (Spielkarte spielkarte : spieler.getHand()) {
            kartenSpielbar = getGewaehlteSpielRegelKomponente(gewaehlteSpielregel).istKarteLegbar(getLetzteAufgelegteKarte(spielrunde.getAufgelegtStapel().getSpielkarten()), spielkarte, spielrunde.getRundeFarbe(), spielrunde.getZuZiehnKartenAnzahl() != 0);

            if (kartenSpielbar) {
                break;
            }
        }
        if (spielrunde.getVerdeckteStapel().getSpielkarten().size() == 0 && spielrunde.getAufgelegtStapel().getSpielkarten().size() < 2 && !kartenSpielbar) {
            this.setSpielendToNextPlayer(spielrunde.getSpielerListe(), spielrunde);
            return true;
        } else {
            return false;
        }
    }

    private void setSpielendToNextPlayer(List<Spieler> spielerListe, Spielrunde spielrunde) {
        Spieler spieler = fragWerDranIst(spielerListe);
        int indexSpielend = spielerListe.indexOf(spieler);
        spielerListe.get(indexSpielend).setSpielend(false);

        if(spielrunde.isUhrzeiger()) {
            if (indexSpielend == spielerListe.size() - 1) {
                spielerListe.get(0).setSpielend(true);
            } else {
                spielerListe.get(indexSpielend + 1).setSpielend(true);
            }
        } else {
            if (indexSpielend == 0) {
                spielerListe.get(spielerListe.size()-1).setSpielend(true);
            } else {
                spielerListe.get(indexSpielend - 1).setSpielend(true);
            }
        }
    }

    private void setztKarteVomHandAufDemAufgelegteStapel(Spieler spieler, Spielkarte spielkarte, Spielrunde spielrunde) {
        // int i = spielrunde.getSpielerListe().indexOf(spieler);

        spieler.getHand().remove(spielkarte);
        //     spielrunde.getSpielerListe().get(i).setHand(spieler.getHand());

        spielrunde.getAufgelegtStapel().getSpielkarten().add(spielkarte);
    }

    // Für aufgedeckten Stapel
    private Spielkarte getLetzteAufgelegteKarte(List<Spielkarte> aufgelegterStapel) {
        if (CollectionUtils.isEmpty(aufgelegterStapel)) {
            throw new TechnischeException("AufgelegtStapel ist leer");
        }
        return aufgelegterStapel.get(aufgelegterStapel.size() - 1);
    }

    // Für verdeckten Stapel
    private List<Spielkarte> getNeueKartenVomVerdecktenStapelUndRemove(int anzahl, Spielrunde spielrunde) {
        if (spielrunde.getVerdeckteStapel().getSpielkarten().size() < anzahl) {
            reloadVerdecktenStapel(spielrunde);
        }
        List<Spielkarte> returnedKarte = new ArrayList<>(anzahl);
        for (int i = 0; i < anzahl; i++) {
            int letzteIndex = spielrunde.getVerdeckteStapel().getSpielkarten().size() - 1;
            returnedKarte.add(spielrunde.getVerdeckteStapel().getSpielkarten().get(letzteIndex));
            spielrunde.getVerdeckteStapel().getSpielkarten().remove(letzteIndex);
        }
        return returnedKarte;
    }

    private void reloadVerdecktenStapel(Spielrunde spielrunde) {
        // Original soll weitergenutzt werden
        List<Spielkarte> originalAufgelegterStapel = spielrunde.getAufgelegtStapel().getSpielkarten();
        // Clone vom Original um alle außer die letzte zu mischen
        List<Spielkarte> originalAufgelegterStapelClone = originalAufgelegterStapel.stream().collect(toList());

        // nimmt die letzte aufgelegte Karte, erzeug davon ein neuer Stapel und setzt er als der neue aufgelegte Stapel in der Spielrunde
        Spielkarte letzteAufgelegteSpielKarte = originalAufgelegterStapel.get(originalAufgelegterStapel.size() - 1);

        //Entfernung von der letzte aufgelegte Karte und durchmischen der vorher aufgelegten Karten
        originalAufgelegterStapel.clear();
        originalAufgelegterStapel.add(letzteAufgelegteSpielKarte);
        originalAufgelegterStapelClone.remove(letzteAufgelegteSpielKarte);
        originalAufgelegterStapelClone = mischeKarten(originalAufgelegterStapelClone);

        //hinzufügen von den vorherigen karten vom verdeckten Stapel
        List<Spielkarte> originalVerdeckteStapel = spielrunde.getVerdeckteStapel().getSpielkarten();
        originalVerdeckteStapel.addAll(originalAufgelegterStapelClone);
    }


    private List<Spielkarte> mischeKarten(List<Spielkarte> listSpielkarten) {
        Collections.shuffle(listSpielkarten);
        return listSpielkarten;
    }

    private ISpielregel getGewaehlteSpielRegelKomponente(RegelKompTyp gewaehlteSpielregel) {
        switch (gewaehlteSpielregel) {
            case OHNE_SONDER_REGEL:
                return spielregelohneSonder;
            case MIT_BASIC_SONDER_REGEL:
                return spielregelBasicSonder;
            case ALLE_SONDER_REGEL:
                return spielregelAlleSonder;
            default:
                throw new TechnischeException("unbekannte SpielRegelKomponente wurde übergeben");
        }
    }
}
