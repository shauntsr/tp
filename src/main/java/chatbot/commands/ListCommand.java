package chatbot.commands;

import chatbot.tasks.Task;
import chatbot.UI;
import chatbot.Storage;

import java.util.ArrayList;

public class ListCommand extends Command {
    @Override
    public void execute(ArrayList<Task> tasks, UI ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}