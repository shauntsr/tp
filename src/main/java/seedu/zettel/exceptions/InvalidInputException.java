package seedu.zettel.exceptions;

/**
 * Exception thrown when user input is invalid or cannot be processed.
 * This exception is used to indicate that the user has provided input
 * that does not conform to expected formats or constraints.
 */
public class InvalidInputException extends ZettelException {
    /**
     * Constructs a new InvalidInputException with the specified detail message.
     * The message is automatically prefixed with "Invalid Input: ".
     *
     * @param message The detail message explaining what aspect of the input is invalid.
     */
    public InvalidInputException(String message) {
        super("Invalid Input: " + message);
    }
}
