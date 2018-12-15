package komponenten.console.export;

import model.exceptions.MauMauException;

public interface IConsole {

    /**
     * Methode, die Zug-Spiel-Schleife ermöglicht
     *
     * @throws MauMauException - die von den darunter liegenden Komponenten weitergereichte MauMauException
     */
    void run() throws MauMauException;
}
