package seedu.duke.exceptions;

/**
 * Represents a general exception specific to the Coach chatbot application.
 * This is the base exception class for all custom exceptions in the chatbot system.
 */
public class CoachException extends Exception {
    /**
     * Constructs a CoachException with the specified error message.
     *
     * @param message The detailed error message explaining the exception.
     */
    public CoachException(String message) {
        super(message);
    }
}

