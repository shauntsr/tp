package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.List;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidFormatException;
import seedu.zettel.exceptions.TagAlreadyExistsException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

/**
 * Command to add a new tag to the global list of tags.
 */
public class NewTagCommand extends Command {
    private final String tag;

    /**
     * Constructs a NewTagCommand with the specified tag.
     *
     * @param tag The tag to add.
     */
    public NewTagCommand(String tag) {
        this.tag = tag;
    }

    /**
     * Executes the command to add the tag.
     * Validates the tag, adds it to the global list if not present,
     * persists the updated tags in storage, and shows UI feedback.
     *
     * @param notes List of notes in the repository (not used in this command).
     * @param tags Global list of tags.
     * @param ui UI object to display messages.
     * @param storage Storage object to persist changes.
     * @throws ZettelException If the tag is invalid or already exists.
     */
    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) throws ZettelException {
        if (tag == null || tag.isBlank()) {
            throw new InvalidFormatException("No tag provided to add.");
        }

        if (tags.contains(tag)) {
            throw new TagAlreadyExistsException(tag + " already exists.");
        }

        tags.add(tag);
        storage.updateTags(tags);
        ui.showSuccessfullyAddedTag(tag);
    }
}
