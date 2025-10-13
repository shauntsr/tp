package seedu.duke.exceptions;

/**
 * Exception thrown when user input is invalid or cannot be parsed correctly.
 * This exception is used to indicate that the user's command or input does not conform to the expected format or contains invalid data.
 */
public class InvalidInputException extends CoachException {
    /**
     * Constructs an InvalidInputException with the specified error message.
     * The message is automatically prefixed with "Invalid Input: " for clarity.
     *
     * @param message The detailed error message explaining what made the input invalid.
     */
    public InvalidInputException(String message) {
        super("Invalid Input: " + message);
    }
}
