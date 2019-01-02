package model.exceptions;

public class TechnischeException extends RuntimeException {

    public TechnischeException(final String message) {
        super("Technischer Fehler: " + message);
    }

    public TechnischeException(final Throwable cause) {
        super(cause);
    }
}
