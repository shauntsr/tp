package seedu.zettel.exceptions;

/**
 * Exception thrown when a user tries to pin a note that is already pinned, opr
 * unpin a note that is already unpinned.
 */

public class AlreadyPinnedException extends ZettelException {
    /**
     * Constructs a new AlreadyPinnedException with the specified detail message.
     *
     * @param message The detail message explaining that the note is already pinned.
     */
    public AlreadyPinnedException(String message) {
        super(message);
    }
    
}
