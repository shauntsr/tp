package seedu.zettel.exceptions;

public class TagExistsException extends ZettelException {
    /**
     * Constructs a new TagExistsException with the specified detail message.
     * This exception is usually thrown when the user adds a tag that already exists,
     * or tries to tag a note with a duplicate tag.
     *
     * @param message The detail message explaining the cause of the exception.
     */
    public TagExistsException(String message) {
        super(message);
    }
}
