package seedu.zettel.exceptions;

/**
 * Exception thrown when a required description field is empty or missing.
 * This exception is used to enforce that certain operations require
 * non-empty description text.
 */
public class EmptyDescriptionException extends ZettelException {
    /**
     * Constructs a new EmptyDescriptionException with the specified detail message.
     *
     * @param message The detail message explaining which description field is empty
     *                and why it is required.
     */
    public EmptyDescriptionException(String message) {
        super(message);
    }
}
