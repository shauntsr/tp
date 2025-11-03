package seedu.zettel.exceptions;

/**
 * Exception thrown when there is no repositories
 * This exception is typically thrown when attempting to perform actions on empty repository.
 * This exception is also thrown when trying to list all repositories when there are none.
 */
public class NoReposException extends ZettelException {
    /**
     * Constructs a new NoReposException with the specified detail message.
     *
     * @param message The detail message explaining which note was not found.
     */
    public NoReposException(String message) {
        super(message);
    }
}
