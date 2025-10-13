package seedu.duke.commands;

import seedu.duke.exceptions.CoachException;
import seedu.duke.exceptions.InvalidTaskFormatException;
import seedu.duke.Storage;
import seedu.duke.UI;
import seedu.duke.tasks.Task;

import java.util.ArrayList;

/**
 * Command to mark or unmark a task as done.
 * This command modifies the mark status of a task at a specified index in the task list.
 */
public class MarkCommand extends Command {
    private final int index;
    private final boolean isMark;

    /**
     * Constructs a MarkCommand with the specified task index and mark status.
     *
     * @param index  The index of the task to be marked or unmarked.
     * @param isMark true to mark the task as done, false to unmark it.
     */
    public MarkCommand(int index, boolean isMark) {
        this.index = index;
        this.isMark = isMark;
    }

    /**
     * Executes the mark command by updating the completion status of the task at the specified index.
     * The updated task list is then saved to storage and a confirmation message is displayed to the user.
     *
     * @param tasks   The list of tasks to operate on.
     * @param ui      The user interface to display messages.
     * @param storage The storage system to persist changes.
     * @throws CoachException             If the task index is out of range.
     * @throws InvalidTaskFormatException If the specified task number is invalid.
     */
    @Override
    public void execute(ArrayList<Task> tasks, UI ui, Storage storage) throws CoachException {
        if (index < 0 || index >= tasks.size()) {
            throw new InvalidTaskFormatException("Task number out of range!");
        }
        Task task = tasks.get(index);
        task.setDone(isMark);
        ui.showTaskMarked(task, isMark);
        storage.save(tasks);
    }
}