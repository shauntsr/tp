package chatbot.commands;

import chatbot.tasks.Task;
import chatbot.UI;
import chatbot.Storage;

import java.util.ArrayList;

public class ExitCommand extends Command {
    @Override
    public void execute(ArrayList<Task> tasks, UI ui, Storage storage) {
        ui.showBye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
