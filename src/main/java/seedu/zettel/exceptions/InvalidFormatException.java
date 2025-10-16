package seedu.zettel.exceptions;

/**
 * Exception thrown when input or data is not in the expected format.
 * This exception is used to indicate that the structure or format of
 * the provided data does not match the required specifications.
 */
public class InvalidFormatException extends ZettelException {
    /**
     * Constructs a new InvalidFormatException with the specified detail message.
     *
     * @param message The detail message explaining what format was expected
     *                and how the actual format differs.
     */
    public InvalidFormatException(String message) {
        super(message);
    }
}
