package seedu.duke.exceptions;

public class InvalidTaskIndexException extends CoachException {
    public InvalidTaskIndexException(String message) {
        super("Invalid Index! " + message);
    }
}
