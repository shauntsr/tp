package seedu.duke.commands;

import seedu.duke.exceptions.CoachException;
import seedu.duke.Storage;
import seedu.duke.UI;
import seedu.duke.exceptions.InvalidTaskFormatException;
import seedu.duke.tasks.Task;

import java.util.ArrayList;

public class DeleteCommand extends Command {
    private final int index;
    public DeleteCommand(int index) {
        this.index = index;
    }

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
