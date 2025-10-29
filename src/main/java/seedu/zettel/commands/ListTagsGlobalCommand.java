package seedu.zettel.commands;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

import java.util.ArrayList;
import java.util.List;

public class ListTagsGlobalCommand extends Command{
    public ListTagsGlobalCommand() {
    }

    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) throws ZettelException {
        if (tags.isEmpty()) {
            throw new ZettelException("There are no tags to list");
        }
        ui.showTagsListGlobal(tags);
    }
}
