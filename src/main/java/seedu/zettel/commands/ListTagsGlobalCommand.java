package seedu.zettel.commands;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.NoTagsException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

import java.util.ArrayList;
import java.util.List;

/**
 * Command to list all tags available globally across all notes.
 * <p>
 * Displays every unique tag present in the repository, regardless of which note it belongs to.
 * Throws an exception if there are no tags to display.
 * </p>
 */
public class ListTagsGlobalCommand extends Command {

    /**
     * Constructs a {@code ListTagsGlobalCommand}.
     */
    public ListTagsGlobalCommand() {
    }

    /**
     * Executes the command to display all global tags.
     *
     * @param notes   The list of all notes in the repository (unused in this command).
     * @param tags    The list of all unique tags available globally.
     * @param ui      The UI instance used to display the global tag list.
     * @param storage The storage instance (unused in this command).
     * @throws ZettelException If there are no tags to list.
     */
    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) throws ZettelException {
        if (tags.isEmpty()) {
            throw new NoTagsException("There are no tags to list");
        }
        ui.showTagsListGlobal(tags);
    }
}
