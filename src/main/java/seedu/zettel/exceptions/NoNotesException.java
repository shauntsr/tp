package seedu.zettel.exceptions;

/**
 * Exception thrown when a requested note cannot be found or does not exist.
 * This exception is typically thrown when attempting to access, modify, or delete
 * a note that is not present in the note list.
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
