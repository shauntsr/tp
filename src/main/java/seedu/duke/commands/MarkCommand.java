package seedu.duke.commands;

import seedu.duke.exceptions.CoachException;
import seedu.duke.exceptions.InvalidTaskFormatException;
import seedu.duke.Storage;
import seedu.duke.UI;
import seedu.duke.tasks.Task;

import java.util.ArrayList;

public class MarkCommand extends Command {
    private final int index;
    private final boolean isMark;

    public MarkCommand(int index, boolean isMark) {
        this.index = index;
        this.isMark = isMark;
    }

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
