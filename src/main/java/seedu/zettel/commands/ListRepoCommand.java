package seedu.zettel.commands;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.NoReposException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

import java.util.ArrayList;
import java.util.List;

/**
 * Command to list all available repositories.
 */
public class ListRepoCommand extends Command {

    /**
     * Executes the list repositories command by displaying the available repositories. to the user.
     *
     * @param notes   The list of current notes (not used by this command).
     * @param tags    The list of current tags (not used by this command).
     * @param ui      The UI object for displaying the repository list.
     * @param storage The storage object storing repository list.
     * @throws ZettelException If an error occurs during command execution.
     */

    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) throws ZettelException {
        ArrayList<String> repos = storage.getRepoList();
        if (repos.isEmpty()) {
            throw new NoReposException("There are no respositories to list");
        }
        ui.showRepoList(repos);
    }
}
