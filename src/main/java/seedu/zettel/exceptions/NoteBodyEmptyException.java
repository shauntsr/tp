package seedu.zettel.exceptions;

/**
 * Exception thrown when there is no body content in a note.
 * 
 */
public class NoteBodyEmptyException extends ZettelException {
    /**
     * Constructs a new NoteBodyEmptyException with the specified detail message.
     *
     * @param message The detail message explaining which note was not found.
     */
    public NoteBodyEmptyException(String message) {
        super(message);
    }
}
