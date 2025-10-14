package seedu.zettel.commands;

import seedu.exceptions.ZettelException;
import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;

import java.util.ArrayList;

public class ListNoteCommand extends Command{
    public ListNoteCommand(boolean b) {
    }

    @Override
    public void execute(ArrayList<Note> notes, UI ui, Storage storage) throws ZettelException {
        return;
    }
}
