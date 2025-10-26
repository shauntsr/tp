package seedu.zettel.exceptions;

/**
 * Exception thrown when attempting to link a note to itself.
 * This prevents circular self-references in the note-linking system.
 */
public class SelfLinkException extends ZettelException {
    /**
     * Constructs a SelfLinkException with the specified error message.
     *
     * @param message The error message describing why the exception was thrown.
     */
    public SelfLinkException(String message) {
        super(message);
    }
}
