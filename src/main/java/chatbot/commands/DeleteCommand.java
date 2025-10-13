package chatbot.commands;

import chatbot.exceptions.CoachException;
import chatbot.Storage;
import chatbot.UI;
import chatbot.exceptions.InvalidTaskFormatException;
import chatbot.tasks.Task;

import java.util.ArrayList;

/**
 * Command to delete a task from the task list.
 * A DeleteCommand removes a task at the specified index,
 * displays a confirmation message, and saves the updated list to storage.
 */
public class DeleteCommand extends Command {
    private final int index;

    /**
     * Constructs a DeleteCommand with the specified task index.
     *
     * @param index The index of the task to delete.
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    /**
     * Executes the delete command by removing the task at the index in list,
     * displaying a confirmation message, and saving the updated list to storage.
     *
     * @param tasks   The list of tasks to delete from.
     * @param ui      The UI object for displaying the confirmation message.
     * @param storage The storage object for saving the updated task list.
     * @throws CoachException If the task index is out of range.
     */
    @Override
    public void execute(ArrayList<Task> tasks, UI ui, Storage storage) throws CoachException {
        if (index < 0 || index >= tasks.size()) {
            throw new InvalidTaskFormatException("Task number out of range!");
        }
        Task removedTask = tasks.remove(index);
        ui.showTaskDeleted(removedTask, tasks.size());
        storage.save(tasks);
    }
}