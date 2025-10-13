package seedu.duke.commands;

import seedu.duke.tasks.Task;
import seedu.duke.UI;
import seedu.duke.Storage;

import java.util.ArrayList;

public class ExitCommand extends Command {
    @Override
    public void execute(ArrayList<Task> tasks, UI ui, Storage storage) {
        ui.showBye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
