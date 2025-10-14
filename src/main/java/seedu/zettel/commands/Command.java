package seedu.zettel.commands;

import seedu.zettel.tasks.Task;
import seedu.zettel.UI;
import seedu.zettel.Storage;
import seedu.zettel.exceptions.CoachException;

import java.util.ArrayList;

public abstract class Command {
    public abstract void execute(ArrayList<Task> tasks, UI ui, Storage storage) throws CoachException;

    public boolean isExit() {
        return false;
    }
}
