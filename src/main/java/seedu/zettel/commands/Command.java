package seedu.zettel.commands;

import java.util.ArrayList;

import seedu.duke.exceptions.ZettelException;
import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;

public abstract class Command {

    public abstract void execute(ArrayList<Note> notes, UI ui, Storage storage) throws ZettelException;

    public boolean isExit() {
        return false;
    }
}
