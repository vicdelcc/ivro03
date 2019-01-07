package komponenten.spielsteuerung.impl;

import komponenten.karten.export.Blatttyp;
import komponenten.karten.export.Blattwert;
import komponenten.karten.export.Spielkarte;
import komponenten.spielregel.export.ISpielregel;
import komponenten.spielregel.export.RegelComponentUtil;
import komponenten.spielsteuerung.export.ISpielsteuerung;
import komponenten.spielverwaltung.export.*;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import util.exceptions.TechnischeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Komponent, der eine Spielrunde steuert
 */
@Service
@NoArgsConstructor
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
        for (Hand hand : spieler.getHands()) {
            if (hand.getSpielrunde().getIdentity() == spielrunde.getIdentity()) {
                if (hand.getSpielkarten() == null) {
                    throw new TechnischeException("Spielershand ist null");
                }
            }
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
                regelComponentUtil = gewaehlteSpielRegelK.holeAuswirkungVonKarte(spielkarte, spielrunde.getSpielerListe(), spielrunde.getZuZiehnKartenAnzahl());
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
        for (Hand hand : spieler.getHands()) {
            if (hand.getSpielrunde().getIdentity() == spielrunde.getIdentity()) {
                if (hand.getSpielkarten() == null) {
                    throw new TechnischeException("Spielershand ist null");
                }
                if (hand.getSpielkarten().isEmpty()) {
                    throw new TechnischeException("Spielershand ist leer, Spiel sollte schon beendet werden");
                }
                if (hand.getSpielkarten().size() == 1) {
                    sollMauMau = getGewaehlteSpielRegelKomponente(regelKompTyp).istKarteLegbar(getLetzteAufgelegteKarte(spielrunde.getAufgelegtStapel().getSpielkarten()), hand.getSpielkarten().get(0), spielrunde.getRundeFarbe(), spielrunde.getZuZiehnKartenAnzahl() != 0);
                }
            }
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
        for (Hand hand : spieler.getHands()) {
            if (hand.getSpielrunde().getIdentity() == spielrunde.getIdentity()) {
                if (hand.getSpielkarten() == null) {
                    throw new TechnischeException("Spielershand ist null");
                }
            }
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
        for (Hand hand : spieler.getHands()) {
            if (hand.getSpielrunde().getIdentity() == spielrunde.getIdentity()) {
                hand.getSpielkarten().addAll(neueKarten);
            }
        }

        setSpielendToNextPlayer(spielrunde.getSpielerListe());

        spielrunde.setZuZiehnKartenAnzahl(0);

        return spieler;
    }

    @Override
    public boolean checkeObSpielerAusgesetztWird(Spielrunde spielrunde, Spieler spieler, RegelKompTyp gewaehlteSpielregel) {
        boolean kartenSpielbar = false;

        for (Hand hand : spieler.getHands()) {
            if (hand.getSpielrunde().getIdentity() == spielrunde.getIdentity()) {
                for (Spielkarte spielkarte : hand.getSpielkarten()) {
                    kartenSpielbar = getGewaehlteSpielRegelKomponente(gewaehlteSpielregel).istKarteLegbar(getLetzteAufgelegteKarte(spielrunde.getAufgelegtStapel().getSpielkarten()), spielkarte, spielrunde.getRundeFarbe(), spielrunde.getZuZiehnKartenAnzahl() != 0);

                    if (kartenSpielbar) {
                        break;
                    }
                }
            }
        }
        if (spielrunde.getVerdeckteStapel().getSpielkarten().size() == 0 && spielrunde.getAufgelegtStapel().getSpielkarten().size() < 2 && !kartenSpielbar) {
            this.setSpielendToNextPlayer(spielrunde.getSpielerListe());
            return true;
        } else {
            return false;
        }
    }

    private void setSpielendToNextPlayer(List<Spieler> spielerListe) {
        Spieler spieler = fragWerDranIst(spielerListe);
        int indexSpielend = spielerListe.indexOf(spieler);

        if (indexSpielend == spielerListe.size() - 1) {
            spielerListe.get(0).setSpielend(true);
        } else {
            spielerListe.get(indexSpielend + 1).setSpielend(true);
        }
        spielerListe.get(indexSpielend).setSpielend(false);
    }

    private void setztKarteVomHandAufDemAufgelegteStapel(Spieler spieler, Spielkarte spielkarte, Spielrunde spielrunde) {
       // int i = spielrunde.getSpielerListe().indexOf(spieler);

        for (Hand hand : spieler.getHands()) {
            if (hand.getSpielrunde().getIdentity() == spielrunde.getIdentity()) {
                hand.getSpielkarten().remove(spielkarte);
           //     spielrunde.getSpielerListe().get(i).setHand(spieler.getHand());
            }
        }
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
        List<Spielkarte> originalAufgeleteStapel = spielrunde.getAufgelegtStapel().getSpielkarten();
        // nimmt die letzte aufgelegte Karte, erzeug davon ein neuer Stapel und setzt er als der neue aufgelegte Stapel in der Spielrunde
        Spielkarte letzteAufgelegteSpielKarte = originalAufgeleteStapel.get(originalAufgeleteStapel.size() - 1);
        List<Spielkarte> neuAufgelegteStapel = new ArrayList<>();
        neuAufgelegteStapel.addAll(Arrays.asList(letzteAufgelegteSpielKarte));
        spielrunde.setAufgelegtStapel(new Stapel(neuAufgelegteStapel));

        //Entfernung von der letzte aufgelegte Karte und durchmischen
        originalAufgeleteStapel.remove(originalAufgeleteStapel.size() - 1);
        List<Spielkarte> gemischteNeuerAufgedeckterStapel = mischeKarten(originalAufgeleteStapel);

        //hinzufügen von den vorherigen karten vom verdeckten Stapel
        List<Spielkarte> originalVerdeckteStapel = spielrunde.getVerdeckteStapel().getSpielkarten();
        gemischteNeuerAufgedeckterStapel.addAll(originalVerdeckteStapel);

        //Aktualisierung vom verdeckten Stapel in der Spielrunde
        List<Spielkarte> neuVerdeckterStapel = new ArrayList<>();
        neuVerdeckterStapel.addAll(gemischteNeuerAufgedeckterStapel);
        spielrunde.setVerdeckteStapel(new Stapel(neuVerdeckterStapel));
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
