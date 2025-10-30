package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.List;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

/**
 * Command to display the currently active repository.
 * Shows which repository the user is currently working in.
 */
public class CurrentRepoCommand extends Command {

    /**
     * Constructs a CurrentRepoCommand.
     */
    public CurrentRepoCommand() {
    }

    /**
     * Executes the command to display the current repository name.
     *
     * @param notes   The list of notes (not used in this command).
     * @param tags    The list of tags (not used in this command).
     * @param ui      The UI instance for user interaction.
     * @param storage The storage instance to get the current repository.
     * @throws ZettelException If an error occurs during command execution.
     */
    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage)
            throws ZettelException {
        String currentRepo = storage.readCurrRepo();
        ui.showCurrentRepo(currentRepo);
    }
}
