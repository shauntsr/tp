package chatbot.commands;

import chatbot.tasks.Task;
import chatbot.UI;
import chatbot.Storage;
import chatbot.exceptions.CoachException;

import java.util.ArrayList;


public abstract class Command {
    public abstract void execute(ArrayList<Task> tasks, UI ui, Storage storage) throws CoachException;

    public boolean isExit() {
        return false;
    }
}
