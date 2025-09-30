package chatbot.exceptions;

public class InvalidInputException extends CoachException {
    public InvalidInputException(String message) {
        super("Invalid Input: " + message);
    }
}
