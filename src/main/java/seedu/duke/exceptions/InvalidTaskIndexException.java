package seedu.duke.exceptions;

/**
 * Exception thrown when a task index is invalid or out of bounds.
 * This exception is used to indicate that the specified task number does not exist in the task list or out of range.
 */
public class InvalidTaskIndexException extends CoachException {
    /**
     * Constructs an InvalidTaskIndexException with the specified error message.
     *
     * @param message The detailed error message explaining the index error.
     */
    public InvalidTaskIndexException(String message) {
        super("Invalid Index! " + message);
    }
}
