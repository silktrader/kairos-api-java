package eu.silktrader.kairos.exception;

public class KairosException extends RuntimeException {

    public KairosException(String message, Exception exception) {
        super(message, exception);
    }

    public KairosException(String message) {
        super(message);
    }
}
