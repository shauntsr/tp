package seedu.zettel.exceptions;

public class TagAlreadyExistsException extends ZettelException {
    /**
     * Constructs a new TagAlreadyExistsException with the specified detail message.
     * This exception is usually thrown when the user adds a tag that already exists,
     * or tries to tag a note with a duplicate tag.
     *
     * @param message The detail message explaining the cause of the exception.
     */
    public TagAlreadyExistsException(String message) {
        super(message);
    }
}
