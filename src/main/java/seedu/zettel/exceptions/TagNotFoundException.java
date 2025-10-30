package seedu.zettel.exceptions;

public class TagNotFoundException extends ZettelException {
    /**
     * Constructs a new TagNotFoundException with the specified detail message.
     * This exception is usually thrown when the user tries to remove a tag that does not exist.
     *
     * @param message The detail message explaining the cause of the exception.
     */
    public TagNotFoundException(String message) {
        super(message);
    }
}

