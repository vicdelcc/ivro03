package komponenten.spielsteuerung.impl;

import komponenten.spielregel.export.ISpielregel;
import komponenten.spielsteuerung.export.ISpielsteuerung;
import lombok.NoArgsConstructor;
import model.Spieler;
import model.Spielkarte;
import model.Spielrunde;
import model.enums.Blatttyp;
import model.enums.Blattwert;
import model.enums.RegelKompTyp;
import model.exceptions.MauMauException;
import model.exceptions.TechnischeException;
import model.hilfsklassen.RegelComponentUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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

    public Spieler fragWerDranIst(List<Spieler> spielerListe) throws MauMauException {
        if(spielerListe == null) {
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

    public int checkZuZiehendenKarten(Spielrunde spielrunde) throws MauMauException {
        if (spielrunde == null) {
            throw new MauMauException("Spielrunde ist null");
        }
        if (spielrunde.getZuZiehnKartenAnzahl() == null) {
            spielrunde.setZuZiehnKartenAnzahl(0);
        }
        return spielrunde.getZuZiehnKartenAnzahl();
    }

    public boolean spieleKarte(Spieler spieler, Spielkarte spielkarte, Spielrunde spielrunde, RegelKompTyp gewaehlteSpielregel) throws MauMauException {
        if (spieler == null) {
            throw new MauMauException("Spieler ist null");
        }
        if (spieler.getHand() == null) {
            throw new MauMauException("Spielershand ist null");
        }
        if (spielkarte == null) {
            throw new MauMauException("Spielkarte ist null");
        }
        if (spielrunde == null) {
            throw new MauMauException("Spielrunde ist null");
        }
        if (spielrunde.getAufgelegtStapel() == null || spielrunde.getAufgelegtStapel().isEmpty()) {
            throw new MauMauException("Aufgelegter Stapel ist null oder leer");
        }
        if (spielrunde.getZuZiehnKartenAnzahl() == null) {
            spielrunde.setZuZiehnKartenAnzahl(0);
        }

        boolean istKarteLegbar;
        RegelComponentUtil regelComponentUtil = null;

        ISpielregel gewaehlteSpielRegelK = getGewaehlteSpielRegelKomponente(gewaehlteSpielregel);
        if(gewaehlteSpielRegelK != null){
            istKarteLegbar = gewaehlteSpielRegelK.istKarteLegbar(getLetzteAufgelegteKarte(spielrunde.getAufgelegtStapel()), spielkarte, spielrunde.getRundeFarbe(), spielrunde.getZuZiehnKartenAnzahl() != 0);
            if (istKarteLegbar) {
                regelComponentUtil = gewaehlteSpielRegelK.holeAuswirkungVonKarte(spielkarte, spielrunde.getSpielerListe(), spielrunde.getZuZiehnKartenAnzahl());
            }
        } else {
            throw new MauMauException("unbekannte SpielRegelKomponente wurde übergeben");
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


    public boolean sollMauMauAufrufen(Spielrunde spielrunde,Spieler spieler, RegelKompTyp regelKompTyp) throws MauMauException {
        if (spieler == null) {
            throw new MauMauException("Spieler ist null");
        }
        if (spieler.getHand() == null) {
            throw new MauMauException("Spielershand ist null");
        }
        if (spieler.getHand().isEmpty()) {
            throw new MauMauException("Spielershand ist leer, Spiel sollte schon beendet werden");
        }
        boolean sollMauMau = false;
        if(spieler.getHand().size() == 1) {

            sollMauMau = getGewaehlteSpielRegelKomponente(regelKompTyp).istKarteLegbar(getLetzteAufgelegteKarte(spielrunde.getAufgelegtStapel()), spieler.getHand().get(0), spielrunde.getRundeFarbe(), spielrunde.getZuZiehnKartenAnzahl() != 0);
        }
        return sollMauMau;
    }

    public boolean pruefeObWuenscher(Spielkarte spielkarte, RegelKompTyp gewaehlteSpielregel) throws MauMauException {
        if (spielkarte == null) {
            throw new MauMauException("Spielkarte ist null");
        }
        if (gewaehlteSpielregel == null) {
            throw new MauMauException("gewaehlte Spielegel ist null");
        }

        return getGewaehlteSpielRegelKomponente(gewaehlteSpielregel).pruefeObWuenscher(spielkarte);

    }

    public void bestimmeBlatttyp(Blatttyp blatttyp, Spielrunde spielrunde) throws MauMauException {
        if (blatttyp == null) {
            throw new MauMauException("Blatttyp ist null");
        }
        if (spielrunde == null) {
            throw new MauMauException("Spielrunde ist null");
        }
        spielrunde.setRundeFarbe(blatttyp);
    }

    public Spieler zieheKartenVomStapel(Spieler spieler, int anzahlKarten, Spielrunde spielrunde) throws MauMauException {
        if (spieler == null) {
            throw new MauMauException("Spieler ist null");
        }
        if (spieler.getHand() == null) {
            throw new MauMauException("Spielershand ist null");
        }
        if (spielrunde == null) {
            throw new MauMauException("Spielrunde ist null");
        }
        if (spielrunde.getVerdeckteStapel() == null) {
            throw new MauMauException("Verdeckter Stapel ist null");
        }
        if (spielrunde.getAufgelegtStapel() == null || spielrunde.getAufgelegtStapel().isEmpty()) {
            throw new MauMauException("Aufgelegter Stapel ist null oder leer");
        }

        List<Spielkarte> neueKarten = getNeueKartenVomVerdecktenStapelUndRemove(anzahlKarten, spielrunde);
        spieler.getHand().addAll(neueKarten);

        setSpielendToNextPlayer(spielrunde.getSpielerListe());

        spielrunde.setZuZiehnKartenAnzahl(0);

        return spieler;
    }

    @Override
    public boolean checkeObSpielerAusgesetztWird(Spielrunde spielrunde, Spieler spieler, RegelKompTyp gewaehlteSpielregel) throws MauMauException {
        boolean kartenSpielbar = false;
        for (Spielkarte spielkarte : spieler.getHand()) {
            kartenSpielbar = getGewaehlteSpielRegelKomponente(gewaehlteSpielregel).istKarteLegbar(getLetzteAufgelegteKarte(spielrunde.getAufgelegtStapel()), spielkarte, spielrunde.getRundeFarbe(), spielrunde.getZuZiehnKartenAnzahl() != 0);

            if (kartenSpielbar) {
                break;
            }
        }
        if(spielrunde.getVerdeckteStapel().size() == 0 && spielrunde.getAufgelegtStapel().size() <2 && !kartenSpielbar) {
            this.setSpielendToNextPlayer(spielrunde.getSpielerListe());
            return true;
        } else {
            return false;
        }
    }

    private void setSpielendToNextPlayer(List<Spieler> spielerListe) throws MauMauException {
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
        int i = spielrunde.getSpielerListe().indexOf(spieler);
        spieler.getHand().remove(spielkarte);
        spielrunde.getSpielerListe().get(i).setHand(spieler.getHand());
        spielrunde.getAufgelegtStapel().add(spielkarte);
    }

    // Für aufgedeckten Stapel
    private Spielkarte getLetzteAufgelegteKarte(List<Spielkarte> aufgelegterStapel) throws MauMauException {
        if (CollectionUtils.isEmpty(aufgelegterStapel)) {
            throw new MauMauException("AufgelegtStapel ist leer");
        }
        return aufgelegterStapel.get(aufgelegterStapel.size() - 1);
    }

    // Für verdeckten Stapel
    private List<Spielkarte> getNeueKartenVomVerdecktenStapelUndRemove(int anzahl, Spielrunde spielrunde) throws MauMauException {
        if (spielrunde.getVerdeckteStapel().size() < anzahl) {
            reloadVerdecktenStapel(spielrunde);
        }
        List<Spielkarte> returnedKarte = new ArrayList<>(anzahl);
        for (int i = 0; i < anzahl; i++) {
            int letzteIndex = spielrunde.getVerdeckteStapel().size() - 1;
            returnedKarte.add(spielrunde.getVerdeckteStapel().get(letzteIndex));
            spielrunde.getVerdeckteStapel().remove(letzteIndex);
        }
        return returnedKarte;
    }

    private void reloadVerdecktenStapel(Spielrunde spielrunde) {
        List<Spielkarte> originalAufgeleteStapel = spielrunde.getAufgelegtStapel();
        // nimmt die letzte aufgelegte Karte, erzeug davon ein neuer Stapel und setzt er als der neue aufgelegte Stapel in der Spielrunde
        Spielkarte letzteAufgelegteSpielKarte = originalAufgeleteStapel.get(originalAufgeleteStapel.size() - 1);
        List<Spielkarte> neuAufgelegteStapel = new ArrayList<>();
        neuAufgelegteStapel.addAll(Arrays.asList(letzteAufgelegteSpielKarte));
        spielrunde.setAufgelegtStapel(neuAufgelegteStapel);

        //Entfernung von der letzte aufgelegte Karte und durchmischen
        originalAufgeleteStapel.remove(originalAufgeleteStapel.size() - 1);
        List<Spielkarte> gemischteNeuerAufgedeckterStapel = mischeKarten(originalAufgeleteStapel);

        //hinzufügen von den vorherigen karten vom verdeckten Stapel
        List<Spielkarte> originalVerdeckteStapel = spielrunde.getVerdeckteStapel();
        gemischteNeuerAufgedeckterStapel.addAll(originalVerdeckteStapel);

        //Aktualisierung vom verdeckten Stapel in der Spielrunde
        List<Spielkarte> neuVerdeckterStapel = new ArrayList<>();
        neuVerdeckterStapel.addAll(gemischteNeuerAufgedeckterStapel);
        spielrunde.setVerdeckteStapel(neuVerdeckterStapel);
    }


    private List<Spielkarte> mischeKarten(List<Spielkarte> listSpielkarten) {
        Collections.shuffle(listSpielkarten);
        return listSpielkarten;
    }

    private ISpielregel getGewaehlteSpielRegelKomponente(RegelKompTyp gewaehlteSpielregel) throws MauMauException {
        switch (gewaehlteSpielregel) {
            case OHNE_SONDER_REGEL:
                return spielregelohneSonder;
            case MIT_BASIC_SONDER_REGEL:
                return spielregelBasicSonder;
            case ALLE_SONDER_REGEL:
                return spielregelAlleSonder;
            default:
                throw new MauMauException("unbekannte SpielRegelKomponente wurde übergeben");
        }
    }
}
