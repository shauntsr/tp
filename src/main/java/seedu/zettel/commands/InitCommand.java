package seedu.zettel.commands;

import java.util.ArrayList;

import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;

/**
 * Command to initialize a new Zettel repository with the specified name.
 * Creates a folder structure containing notes, archive, and index files.
 */
public class InitCommand extends Command {

    /** The name of the repository to initialize. */
    private final String repoName;

    /**
     * Constructs an InitCommand with the specified repository name.
     *
     * @param repoName The name of the repository to create
     */
    public InitCommand(String repoName) {
        this.repoName = repoName;
    }

    /**
     * Executes the repository initialization command.
     * Creates a new repository directory structure and
     * notifies the user of successful initialization.
     *
     * @param notes   The list of notes (not used)
     * @param ui      The UI instance for user interaction
     * @param storage The storage instance for file and directory management
     * @throws ZettelException If an error occurs during repository creation
     */
    @Override
    public void execute(ArrayList<Note> notes, UI ui, Storage storage) throws ZettelException {
        storage.createRepo(repoName);
        ui.showRepoInit(repoName);
    }
}
