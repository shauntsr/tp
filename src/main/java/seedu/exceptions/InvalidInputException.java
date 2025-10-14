package seedu.exceptions;

public class InvalidInputException extends ZettelException {
    public InvalidInputException(String message) {
        super("Invalid Input: " + message);
    }
}
