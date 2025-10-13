package seedu.duke.commands;

import seedu.duke.exceptions.CoachException;
import seedu.duke.Storage;
import seedu.duke.UI;
import seedu.duke.exceptions.InvalidTaskFormatException;
import seedu.duke.tasks.Deadline;
import seedu.duke.tasks.Event;
import seedu.duke.tasks.Task;
import seedu.duke.tasks.ToDo;

import java.util.ArrayList;

public class AddCommand extends Command {

    public enum TaskType {
        TODO,
        DEADLINE,
        EVENT
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

    private Task createTask() throws CoachException {
        return switch (taskType) {
        case TODO -> new ToDo(description);
        case DEADLINE -> new Deadline(description, time1);
        case EVENT -> new Event(description, time1, time2);
        default -> throw new InvalidTaskFormatException("Unknown task type");
        };
    }
}
