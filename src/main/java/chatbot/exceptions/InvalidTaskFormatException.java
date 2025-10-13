package chatbot.exceptions;

/**
 * Exception thrown when a task format is invalid or improperly structured.
 * This exception is used to indicate issues such as invalid task-related format errors.
 */
public class InvalidTaskFormatException extends CoachException {
    /**
     * Constructs an InvalidTaskFormatException with the specified error message.
     *
     * @param message The detailed error message explaining the task format error.
     */
    public InvalidTaskFormatException(String message) {
        super(message);
    }
}
