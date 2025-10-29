package seedu.zettel.exceptions;

/**
 * Exception thrown when there is no tags that are tagged to the note at all.
 * This exception is typically thrown when attempting to perform actions on an empty
 * tag list.
 * This exception is also thrown when trying to list all global tags, and there are no
 * tags at all.
 */
public class NoTagsException extends ZettelException {
    /**
     * Constructs a new NoTagsException with the specified detail message.
     *
     * @param message The detail message explaining which note was not found.
     */
    public NoTagsException(String message) {
        super(message);
    }
}
