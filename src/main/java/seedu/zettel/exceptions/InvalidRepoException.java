package seedu.zettel.exceptions;

/**
 * Exception thrown when a repository name or repository state is invalid.
 * This exception is used to indicate issues with repository validation,
 * such as invalid repository names, non-existent repositories, or
 * repository structure problems.
 */
public class InvalidRepoException extends ZettelException {
    /**
     * Constructs a new InvalidRepoException with the specified detail message.
     *
     * @param message The detail message explaining what aspect of the repository is invalid,
     *                such as invalid naming conventions or missing repository structure.
     */
    public InvalidRepoException(String message) {
        super(message);
    }
}
