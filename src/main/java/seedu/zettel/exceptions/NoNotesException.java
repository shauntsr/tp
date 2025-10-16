package seedu.zettel.exceptions;

/**
 * Exception thrown when there is no notes in the list at all.
 * This exception is typically thrown when attempting to perform actions on an empty
 * note list.
 */
public class NoNotesException extends ZettelException {
    /**
     * Constructs a new NoNoteException with the specified detail message.
     *
     * @param message The detail message explaining which note was not found.
     */
    public NoNotesException(String message) {
        super(message);
    }
}
