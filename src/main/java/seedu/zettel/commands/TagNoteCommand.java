package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidFormatException;
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.TagAlreadyExistsException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

/**
 * Command to tag a specific note with a new tag.
 */
public class TagNoteCommand extends Command {
    private static final String LIST_DELIM = ";;";

    private final String noteID;
    private final String tag;

    /**
     * Constructs a TagNoteCommand for a given note ID and tag.
     *
     * @param noteID The ID of the note to tag.
     * @param tag The tag to add to the note.
     */
    public TagNoteCommand(String noteID, String tag) {
        this.noteID = noteID;
        this.tag = tag;
    }

    /**
     * Executes the tagging command.
     * Adds the tag to the note if it does not already exist,
     * updates global tags if necessary, and shows UI feedback.
     *
     * @param notes List of notes in the repository.
     * @param tags Global list of tags.
     * @param ui UI object to display messages.
     * @param storage Storage object to persist changes.
     * @throws ZettelException If note not found, tag exists, or validation fails.
     */
    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) throws ZettelException {
        // Validate Inputs
        validateTag(tag);

        // Try to find the note
        Optional<Note> noteOpt = notes.stream()
                .filter(n -> n.getId().equals(noteID))
                .findFirst();
        if (noteOpt.isEmpty()) {
            throw new InvalidNoteIdException("Note with ID '"+ noteID + "' does not exist.");
        }

        Note note = noteOpt.get();

        // Ensure the note doesn't have this tag currently
        List<String> tagList = note.getTags();
        boolean tagExists = tagList.stream()
                .anyMatch(n -> n.equals(tag));
        if (tagExists) {
            throw new TagAlreadyExistsException("This note is already tagged with '" + tag + "'");
        }

        if (!tags.contains(tag)) {
            tags.add(tag);
            storage.updateTags(tags);
            ui.showSuccessfullyAddedTag(tag);
        }

        note.addTag(tag);
        ui.showSuccessfullyTaggedNote(noteID, tag);
    }

    private void validateTag(String tag) throws InvalidFormatException {
        if (tag.contains(LIST_DELIM)) {
            throw new InvalidFormatException("Tag should not contain " + LIST_DELIM);
        }
    }
}
