package chatbot.commands;

import chatbot.tasks.Task;
import chatbot.UI;
import chatbot.Storage;

import java.util.ArrayList;

/**
 * Command to exit the Coach chatbot application.
 * An ExitCommand displays a goodbye message and signals the application to terminate.
 */
public class ExitCommand extends Command {
    /**
     * Executes the exit command by displaying a goodbye message to the user.
     *
     * @param ui The UI object for displaying the goodbye message.
     */
    @Override
    public void execute(ArrayList<Task> tasks, UI ui, Storage storage) {
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
