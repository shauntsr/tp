package chatbot.commands;

import chatbot.exceptions.CoachException;
import chatbot.exceptions.InvalidTaskFormatException;
import chatbot.Storage;
import chatbot.UI;
import chatbot.tasks.Task;

import java.util.ArrayList;

public class MarkCommand extends Command {
    private final int index;
    private final boolean isMark;

    public MarkCommand(int index, boolean isMark) {
        this.index = index;
        this.isMark = isMark;
    }

    @Override
    public void execute(ArrayList<Task> tasks, UI ui, Storage storage) throws CoachException {
        if (index < 0 || index >= tasks.size()) {
            throw new InvalidTaskFormatException("Task number out of range!");
        }
        Task task = tasks.get(index);
        task.setDone(isMark);
        ui.showTaskMarked(task, isMark);
        storage.save(tasks);
    }
}