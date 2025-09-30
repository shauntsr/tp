package chatbot.commands;

import chatbot.exceptions.CoachException;
import chatbot.Storage;
import chatbot.UI;
import chatbot.tasks.Deadline;
import chatbot.tasks.Event;
import chatbot.tasks.Task;
import chatbot.tasks.ToDo;

import java.util.ArrayList;

public class AddCommand extends Command {
    public enum TaskType {
        TODO, DEADLINE, EVENT
    }

    private final String description;
    private final String time1;
    private final String time2;
    private final TaskType taskType;


    public AddCommand(String description, TaskType taskType) {
        this.description = description;
        this.time1 = null;
        this.time2 = null;
        this.taskType = taskType;
    }


    public AddCommand(String description, String deadline, TaskType taskType) {
        this.description = description;
        this.time1 = deadline;
        this.time2 = null;
        this.taskType = taskType;
    }


    public AddCommand(String description, String fromTime, String toTime, TaskType taskType) {
        this.description = description;
        this.time1 = fromTime;
        this.time2 = toTime;
        this.taskType = taskType;
    }

    @Override
    public void execute(ArrayList<Task> tasks, UI ui, Storage storage) throws CoachException {
        Task task = createTask();
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
        storage.save(tasks);
    }

    private Task createTask() {
        switch (taskType) {
        case TODO:
            return new ToDo(description);
        case DEADLINE:
            return new Deadline(description, time1);
        case EVENT:
            return new Event(description, time1, time2);
        default:
            throw new IllegalStateException("Unknown task type");
        }
    }
}
