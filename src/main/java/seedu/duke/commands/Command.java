package seedu.duke.commands;

import seedu.duke.tasks.Task;
import seedu.duke.UI;
import seedu.duke.Storage;
import seedu.duke.exceptions.CoachException;

import java.util.ArrayList;

/**
 * Represents an abstract command that can be executed in the Coach chatbot.
 * A Command object defines execution of user input.
 * Subclasses implement specific command behaviors such as adding, deleting, or listing tasks.
 */
public abstract class Command {
    /**
     * Executes the command with the given task list, UI, and storage.
     * This method must be implemented by subclasses to define specific command behavior.
     *
     * @param tasks   The list of tasks to operate on.
     * @param ui      The UI object for displaying output to the user.
     * @param storage The storage object for saving changes to file.
     * @throws CoachException If an error occurs during command execution.
     */
    public abstract void execute(ArrayList<Task> tasks, UI ui, Storage storage) throws CoachException;

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
