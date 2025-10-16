package seedu.zettel.exceptions;

/**
 * Represents the base exception class for all Zettel-related exceptions.
 * This is the parent class for all custom exceptions in the Zettel application.
 */
public class ZettelException extends Exception {
    /**
     * Constructs a new ZettelException with the specified detail message.
     *
     * @param message The detail message explaining the cause of the exception.
     */
    public ZettelException(String message) {
        super(message);
    }
}
