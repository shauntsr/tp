public class CoachException extends Exception {
    public CoachException(String message) {
        super(message);
    }
}

class InvalidTaskFormatException extends CoachException {
    public InvalidTaskFormatException(String message) {
        super(message);
    }
}

class EmptyDescriptionException extends CoachException {
    public EmptyDescriptionException(String message) {
        super(message);
    }
}

class InvalidTaskIndexException extends CoachException {
    public InvalidTaskIndexException(String message) {
        super("Invalid Index! " + message);
    }
}

class InvalidInputException extends CoachException {
    public InvalidInputException(String message) {
        super("Invalid Input: " + message);
    }
}