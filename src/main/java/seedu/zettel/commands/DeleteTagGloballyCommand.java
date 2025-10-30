package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidFormatException;
import seedu.zettel.exceptions.TagNotFoundException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

/**
 * Command to delete a tag from the global list of tags.
 */
public class DeleteTagGloballyCommand extends Command {
    private final String tag;
    private final boolean isForce;

    /**
     * Constructs a DeleteTagGloballyCommand with the specified tag.
     *
     * @param tag The tag to delete.
     */
    public DeleteTagGloballyCommand(String tag, boolean isForce) {
        this.tag = tag;
        this.isForce = isForce;
    }

    /**
     * Executes the command to remove a tag globally.
     * Validates the tag, deletes it from the global list if present,
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
            throw new InvalidFormatException("No tag provided to delete.");
        }

        if (!tags.contains(tag)) {
            throw new TagNotFoundException("Tag '" + tag + "' does not exist.");
        }

        boolean shouldDelete = isForce;
        if (!isForce) {
            ui.showDeleteTagConfirmation(tag);

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().trim().toLowerCase();
            shouldDelete = input.equals("y") || input.equals("yes");
        }

        if (shouldDelete) {
            // Remove tag from global list
            tags.remove(tag);
            // Remove tag from all notes that have it
            for (Note note : notes) {
                if (note.getTags().contains(tag)) {
                    note.removeTag(tag);
                }
            }
            storage.updateTags(tags);
            ui.showSuccessfullyDeletedTag(tag);
        } else {
            ui.showDeletionCancelled();
        }
    }
}
