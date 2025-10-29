package seedu.zettel.exceptions;

/**
 * Exception thrown when attempting to unlink two notes that are already unlinked.
 * This exception is typically thrown when trying to unlink between two notes that were not 
 * linked in the first place, be is unidirectionally or bidirectionally.
 */
public class NotesAlreadyUnlinkedException extends ZettelException {
    /**
     * Constructs a new NotesAlreadyUnlinkedException with the specified detail message.
     *
     * @param message The detail message explaining which notes are already linked.
     */
    public NotesAlreadyUnlinkedException (String message) {
        super(message);
    }
}
