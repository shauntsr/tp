package seedu.zettel.commands;

import seedu.duke.exceptions.ZettelException;
import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;

import java.util.ArrayList;

public class ExitCommand extends Command {
    @Override
    public CommandResult execute(ArrayList<Note> notes, UI ui, Storage storage) throws ZettelException {
        return null;
    }
}