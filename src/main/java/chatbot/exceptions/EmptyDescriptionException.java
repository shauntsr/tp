package chatbot.exceptions;

/**
 * Exception thrown when a task description is empty or missing.
 * This exception is used to indicate that a required task description was not provided by the user.
 */
public class EmptyDescriptionException extends CoachException {
    /**
     * Constructs an EmptyDescriptionException with the specified error message.
     *
     * @param message The detailed error message explaining why the description is invalid.
     */
    public EmptyDescriptionException(String message) {
        super(message);
    }
}