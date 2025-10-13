package seedu.duke.commands;

import seedu.duke.tasks.Task;
import seedu.duke.UI;
import seedu.duke.Storage;
import seedu.duke.exceptions.CoachException;

import java.util.ArrayList;

public abstract class Command {
    public abstract void execute(ArrayList<Task> tasks, UI ui, Storage storage) throws CoachException;

    public boolean isExit() {
        return false;
    }
}
