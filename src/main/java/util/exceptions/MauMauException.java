package util.exceptions;

public class MauMauException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public MauMauException(final String message) {
        super(message);
    }

    public MauMauException(final Throwable cause) {
        super(cause);
    }

}
