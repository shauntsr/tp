package seedu.zettel.exceptions;

/**
 * Exception thrown when there are problems reading or processing the index file.
 */
public class IndexFileException extends ZettelException {
    public IndexFileException(String message) {
        super(message);
    }
}
