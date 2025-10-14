package seedu.zettel.commands;

public class CommandResult {
    public final boolean isSuccessful;
    public final String message;
    public final Object payload;

    public CommandResult(boolean isSuccessful, String message, Object payload) {
        this.isSuccessful = isSuccessful;
        this.message = message;
        this.payload = payload;
    }
}
