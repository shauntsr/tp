package seedu.zettel.exceptions;

/**
 * Exception thrown when attempting to link two notes that are already linked in that particular direction.
 * This exception is typically thrown when trying to create a unidirectional link between two notes,
 * but an exception will also be thrown when trying to create a bidirection link if the notes are already
 * linked in both directions.
 */
public class NotesAlreadyLinkedException extends ZettelException {
    /**
     * Constructs a new NotesAlreadyLinkedException with the specified detail message.
     *
     * @param message The detail message explaining which notes are already linked.
     */
    public NotesAlreadyLinkedException (String message) {
        super(message);
    }
}
