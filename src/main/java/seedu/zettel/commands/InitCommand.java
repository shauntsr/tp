package seedu.zettel.commands;

import java.util.ArrayList;

import seedu.exceptions.ZettelException;
import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;

public class InitCommand extends Command {

    public InitCommand(String content) {
        super();
    }

    @Override
    public void execute(ArrayList<Note> notes, UI ui, Storage storage) throws ZettelException {

    }
}
