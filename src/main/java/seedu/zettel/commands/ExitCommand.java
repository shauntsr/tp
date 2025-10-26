package seedu.zettel.commands;

import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.Note;
import seedu.zettel.storage.Storage;
import seedu.zettel.UI;

import java.util.ArrayList;
import java.util.List;

/**
 * Command to exit the Zettel application.
 * An ExitCommand displays a goodbye message and signals the application to terminate.
 */
public class ExitCommand extends Command {

    /**
     * Executes the exit command by displaying a goodbye message to the user.
     *
     * @param tags The list of current tags.
     * @param ui   The UI object for displaying the goodbye message.
     */
    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) throws ZettelException {
        ui.showBye();
    }

    /**
     * Indicates that this command should terminate the application.
     *
     * @return True, as this command exits the application.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
