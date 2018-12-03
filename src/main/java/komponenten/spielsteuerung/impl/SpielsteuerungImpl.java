package komponenten.spielsteuerung.impl;

import komponenten.spielregel.export.ISpielregel;
import komponenten.spielsteuerung.export.ISpielsteuerung;
import lombok.NoArgsConstructor;
import model.Spieler;
import model.Spielkarte;
import model.Spielrunde;
import model.enums.Blatttyp;
import model.exceptions.MauMauException;
import model.hilfsklassen.RegelComponentUtil;
import org.apache.commons.collections4.CollectionUtils;
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
// NoArgsConstructor needed for spring container initiation
@NoArgsConstructor
public class SpielsteuerungImpl implements ISpielsteuerung {

    public Spieler fragWerDranIst(List<Spieler> spielerListe) throws MauMauException {
        if(spielerListe.size() < 2){
            throw new MauMauException("Spielerliste muss mehr als einen Spieler enthalten");
        }
        List<Spieler> spielerMitSpielend =spielerListe.stream()
                .filter(Spieler::isSpielend).collect(Collectors.toList());

        if(spielerMitSpielend.size() != 1){
            throw new MauMauException("Keine oder mehrere Spieler mit spielend true gesetzt");
        }
        return spielerMitSpielend.get(0);
    }

    public int checkZuZiehendenKarten(Spielrunde spielrunde) throws MauMauException {
        if(spielrunde == null){
            throw new MauMauException("Spielrunde ist null");
        }
        if( spielrunde.getZuZiehnKartenAnzahl() == null){
            spielrunde.setZuZiehnKartenAnzahl(0);
        }
        return spielrunde.getZuZiehnKartenAnzahl();
    }

    public boolean spieleKarte(Spieler spieler, Spielkarte spielkarte, Spielrunde spielrunde, ISpielregel selectedSpielRegel) throws MauMauException {
        if(spieler == null){
            throw new MauMauException("Spieler ist null");
        }
        if(spieler.getHand() == null){
            throw new MauMauException("Spielershand ist null");
        }
        if(spielkarte == null){
            throw new MauMauException("Spielkarte ist null");
        }
        if(spielrunde == null){
            throw new MauMauException("Spielrunde ist null");
        }
        if(spielrunde.getRundeFarbe() == null){
            throw new MauMauException("Spielrundesfarbe ist null");
        }
        if(spielrunde.getAufgelegtStapel() == null || spielrunde.getAufgelegtStapel().isEmpty()){
            throw new MauMauException("Aufgelegter Stapel ist null oder leer");
        }
        if(selectedSpielRegel == null){
            throw new MauMauException("spielregel Komponente ist nicht gesetzt");
        }
        if(spielrunde.getZuZiehnKartenAnzahl() == null){
            spielrunde.setZuZiehnKartenAnzahl(0);
        }
        if(selectedSpielRegel.istKarteLegbar(getLetzteAufgelegteKarte(spielrunde.getAufgelegtStapel()), spielkarte, spielrunde.getRundeFarbe())){
            setztKarteVomHandAufDemAufgelegteStapel(spieler, spielkarte, spielrunde);
            RegelComponentUtil regelComponentUtil = selectedSpielRegel.holeAuswirkungVonKarte(spielkarte, spielrunde.getSpielerListe());
            spielrunde.setSpielerListe(regelComponentUtil.getSpielerListe());
            spielrunde.setZuZiehnKartenAnzahl(regelComponentUtil.getAnzahlKartenZuZiehen() + spielrunde.getZuZiehnKartenAnzahl());
            spielrunde.setRundeFarbe(spielkarte.getBlatttyp());
            return true;
        } else {
            return false;
        }
    }

    public boolean sollMauMauAufrufen(Spieler spieler) throws MauMauException {
        if(spieler == null){
            throw new MauMauException("Spieler ist null");
        }
        if(spieler.getHand() == null){
            throw new MauMauException("Spielershand ist null");
        }
        if(spieler.getHand().isEmpty()){
            throw new MauMauException("Spielershand ist leer, Spiel sollte schon beendet werden");
        }
        return spieler.getHand().size() == 1;
    }

    public boolean pruefeObWuenscher(Spielkarte spielkarte, ISpielregel selectedSpielRegel) throws MauMauException {
        if(spielkarte == null){
            throw new MauMauException("Spielkarte ist null");
        }
        if(selectedSpielRegel == null){
            throw new MauMauException("spielregel Komponente ist nicht gesetzt");
        }
        return selectedSpielRegel.pruefeObWuenscher(spielkarte);
    }

    public void bestimmeBlatttyp(Blatttyp blatttyp, Spielrunde spielrunde) throws MauMauException {
        if (blatttyp == null){
            throw new MauMauException("Blatttyp ist null");
        }
        if (spielrunde == null){
            throw new MauMauException("Spielrunde ist null");
        }
        spielrunde.setRundeFarbe(blatttyp);
    }

    public Spieler zieheKartenVomStapel(Spieler spieler, int anzahlKarten, Spielrunde spielrunde) throws MauMauException {
        if(spieler == null){
            throw new MauMauException("Spieler ist null");
        }
        if(spieler.getHand() == null){
            throw new MauMauException("Spielershand ist null");
        }
        if(spielrunde == null){
            throw new MauMauException("Spielrunde ist null");
        }
        if(spielrunde.getVerdeckteStapel() == null){
            throw new MauMauException("Verdeckter Stapel ist null");
        }
        if(spielrunde.getAufgelegtStapel() == null || spielrunde.getAufgelegtStapel().isEmpty() ){
            throw new MauMauException("Aufgelegter Stapel ist null oder leer");
        }

        List<Spielkarte> neueKarten = getNeueKartenVomVerdecktenStapelUndRemove(anzahlKarten, spielrunde);
        spieler.getHand().addAll(neueKarten);

        return spieler;
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
        if(spielrunde.getVerdeckteStapel().size() < anzahl){
            reloadVerdecktenStapel(spielrunde);
        }
        List<Spielkarte> returnedKarte = new ArrayList<>(anzahl);
        for (int i=0; i < anzahl; i++){
            int letzteIndex = spielrunde.getVerdeckteStapel().size()-1;
            returnedKarte.add(spielrunde.getVerdeckteStapel().get(letzteIndex));
            spielrunde.getVerdeckteStapel().remove(letzteIndex);
        }
        return returnedKarte;
    }

    //TODO add logger comment
    private void reloadVerdecktenStapel(Spielrunde spielrunde){
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
}
