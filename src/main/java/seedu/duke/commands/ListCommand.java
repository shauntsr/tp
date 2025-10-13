package seedu.duke.commands;

import seedu.duke.tasks.Task;
import seedu.duke.UI;
import seedu.duke.Storage;

import java.util.ArrayList;

/**
 * Command to display all tasks in the task list.
 * A ListCommand shows the complete list of tasks with their current status.
 */
public class ListCommand extends Command {
    /**
     * Executes the list command by displaying all tasks in the task list.
     *
     * @param tasks The list of tasks to display.
     * @param ui    The UI object for displaying the task list.
     */
    @Override
    public void execute(ArrayList<Task> tasks, UI ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}