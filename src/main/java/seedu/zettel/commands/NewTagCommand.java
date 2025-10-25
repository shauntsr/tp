package seedu.zettel.commands;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidFormatException;
import seedu.zettel.exceptions.TagExistsException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

import java.util.ArrayList;
import java.util.List;

public class NewTagCommand extends Command {
    private final String tag;

    public NewTagCommand(String tag) {
        this.tag = tag;
    }

    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) throws ZettelException {
        if (tag == null || tag.isBlank()) {
            throw new InvalidFormatException("No tag provided to add.");
        }

        if (tags.contains(tag)) {
            throw new TagExistsException(tag + " already exists.");
        }

        tags.add(tag);
        storage.updateTags(tags);

        ui.showTagAdded(tag);
    }
}
