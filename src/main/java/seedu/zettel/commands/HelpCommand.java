package seedu.zettel.commands;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

import java.util.ArrayList;
import java.util.List;

/**
 * Command to display the list of available commands.
 * A HelpCommand displays the help message showing all commands and their usage.
 */
public class HelpCommand extends Command {

    /**
     * Executes the help command by displaying the available commands to the user.
     *
     * @param notes   The list of current notes (not used by this command).
     * @param tags    The list of current tags (not used by this command).
     * @param ui      The UI object for displaying the help message.
     * @param storage The storage object (not used by this command).
     * @throws ZettelException If an error occurs during command execution.
     */
    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) throws ZettelException {
        ui.showHelp();
    }

    /**
     * Indicates that this command should not terminate the application.
     *
     * @return False, as this command does not exit the application.
     */
    @Override
    public boolean isExit() {
        return false;
    }
}
