package seedu.zettel.exceptions;

/**
 * Exception thrown when an invalid noteId is provided for note operations.
 * This exception is typically thrown when the user specifies an index that is
 * out of bounds or does not correspond to an existing note.
 */
public class InvalidNoteIdException extends ZettelException {
    /**
     * Constructs a new InvalidNoteIdException with the specified detail message.
     * The message is automatically prefixed with "Invalid Note ID! ".
     *
     * @param message The detail message explaining why the note ID is invalid.
     */
    public InvalidNoteIdException(String message) {
        super("Invalid Note ID! " + message);
    }
}
