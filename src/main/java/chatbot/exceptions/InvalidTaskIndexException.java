package chatbot.exceptions;

public class InvalidTaskIndexException extends CoachException {
    public InvalidTaskIndexException(String message) {
        super("Invalid Index! " + message);
    }
}
