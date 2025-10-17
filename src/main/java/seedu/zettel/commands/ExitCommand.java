package seedu.zettel.commands;

import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;

import java.util.ArrayList;

/**
 * Command to exit the Zettel application.
 * An ExitCommand displays a goodbye message and signals the application to terminate.
 */
public class ExitCommand extends Command {

    /**
     * Executes the exit command by displaying a goodbye message to the user.
     *
     * @param ui The UI object for displaying the goodbye message.
     */
    @Override
    public void execute(ArrayList<Note> notes, UI ui, Storage storage) throws ZettelException {
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
