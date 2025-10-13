package seedu.duke.commands;

import seedu.duke.tasks.Task;
import seedu.duke.UI;
import seedu.duke.Storage;

import java.util.ArrayList;

public class ListCommand extends Command {
    @Override
    public void execute(ArrayList<Task> tasks, UI ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
