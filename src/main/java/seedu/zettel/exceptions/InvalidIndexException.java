package seedu.zettel.exceptions;

public class InvalidIndexException extends ZettelException {
    public InvalidIndexException(String message) {
        super("Invalid Index! " + message);
    }
}
