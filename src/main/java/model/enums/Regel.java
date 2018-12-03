package model.enums;

public enum Regel {


    // BASIC
    // 7
    ZWEI_ZIEHEN,

    // Ass
    AUSSETZEN,

    // BUBE
    WUENSCHER,


    // FULL
    // 9
    RICHTUNGSWECHSEL,

    // Hebt die Funktion von ZWEI_ZIEHEN auf,  Farbe muss mit der zuletzt gelegten übereinstimmen
    // 8
    STOPPER,

    // Kann auf jede Nicht-Funktionskarte gelegt werden
    // 10
    ALLESLEGER,

    KEIN;

}
