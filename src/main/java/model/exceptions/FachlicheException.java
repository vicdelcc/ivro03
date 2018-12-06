package model.exceptions;

public class FachlicheException extends MauMauException {

    public FachlicheException(final String message) {

        super("Fachlicher Fehler: " + message);
    }

    public FachlicheException(final Throwable cause) {
        super(cause);
    }
}
