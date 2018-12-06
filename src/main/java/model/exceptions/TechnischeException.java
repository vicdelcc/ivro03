package model.exceptions;

public class TechnischeException extends MauMauException {

    public TechnischeException(final String message) {
        super("Technischer Fehler: " + message);
    }

    public TechnischeException(final Throwable cause) {
        super(cause);
    }
}
