package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.List;

import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.Note;
import seedu.zettel.storage.Storage;
import seedu.zettel.UI;

/**
 * Represents an abstract command that can be executed in Zettel.
 * A Command object defines execution of user input.
 * Subclasses implement specific command behaviors such as adding, deleting, or listing notes.
 */
public abstract class Command {

    /**
     * Executes the command with the given task list, UI, and storage.
     * This method must be implemented by subclasses to define specific command behavior.
     *
     * @param notes   The list of tasks to operate on.
     * @param tags    The list of current tags.
     * @param ui      The UI object for displaying output to the user.
     * @param storage The storage object for reading or writing changes to file.
     * @throws ZettelException If an error occurs during command execution.
     */
    public abstract void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage)
            throws ZettelException;

    /**
     * Checks if this command should terminate the application.
     * Returns false by default. Subclasses can override to return true for exit commands.
     *
     * @return True if this command exits the application, false otherwise.
     */
    public boolean isExit() {
        return false;
    }
}
