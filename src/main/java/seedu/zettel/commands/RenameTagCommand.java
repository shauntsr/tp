package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.List;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidFormatException;
import seedu.zettel.exceptions.TagAlreadyExistsException;
import seedu.zettel.exceptions.TagNotFoundException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

/**
 * Command to delete a tag from the global list of tags.
 */
public class RenameTagCommand extends Command {
    private final String oldTag;
    private final String newTag;

    /**
     * Constructs a RenameTagCommand with the specified old and new tags.
     *
     * @param oldTag The tag to rename.
     * @param newTag The new tag name.
     */
    public RenameTagCommand(String oldTag, String newTag) {
        this.oldTag = oldTag;
        this.newTag = newTag;
    }

    /**
     * Executes the command to rename a tag globally.
     * Validates the tag, updates the global list if present,
     * persists the updated tags in storage, and shows UI feedback.
     * All notes containing the old tag will have it replaced with the new tag.
     *
     * @param notes List of notes in the repository (not used in this command).
     * @param tags Global list of tags.
     * @param ui UI object to display messages.
     * @param storage Storage object to persist changes.
     * @throws ZettelException If the tag is invalid or already exists.
     */
    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) throws ZettelException {
        // Validate Inputs
        if (oldTag == null || oldTag.isBlank()) {
            throw new InvalidFormatException("No tag provided to rename.");
        }

        if (!tags.contains(oldTag)) {
            throw new TagNotFoundException("Tag '" + oldTag + "' does not exist.");
        }
        if (newTag == null || newTag.isBlank()) {
            throw new InvalidFormatException("No new tag name provided.");
        }
        if (tags.contains(newTag)) {
            throw new TagAlreadyExistsException("Tag '" + newTag + "' already exists.");
        }
        // Rename tag in global list
        int index = tags.indexOf(oldTag);
        tags.set(index, newTag);
        // Rename tag in all notes that have it
        for (Note note : notes) {
            if (note.getTags().contains(oldTag)) {
                note.removeTag(oldTag);
                note.addTag(newTag);
            }
        }
        storage.updateTags(tags);
        ui.showSuccessfullyRenamedTag(oldTag, newTag);
    }
}
