package chatbot.commands;

import chatbot.exceptions.CoachException;
import chatbot.Storage;
import chatbot.UI;
import chatbot.exceptions.InvalidTaskFormatException;
import chatbot.tasks.Task;

import java.util.ArrayList;

public class DeleteCommand extends Command {
    private final int index;

    public DeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(ArrayList<Task> tasks, UI ui, Storage storage) throws CoachException {
        if (index < 0 || index >= tasks.size()) {
            throw new InvalidTaskFormatException("Task number out of range!");
        }
        Task removedTask = tasks.remove(index);
        ui.showTaskDeleted(removedTask, tasks.size());
        storage.save(tasks);
    }
}