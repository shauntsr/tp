package chatbot.commands;

import chatbot.exceptions.CoachException;
import chatbot.Storage;
import chatbot.UI;
import chatbot.exceptions.InvalidTaskFormatException;
import chatbot.tasks.Deadline;
import chatbot.tasks.Event;
import chatbot.tasks.Task;
import chatbot.tasks.ToDo;

import java.util.ArrayList;

/**
 * Command to add a new task to the task list.
 * An AddCommand creates and adds a task of the specified type
 * (ToDo, Deadline, or Event) to the task list and saves it to storage.
 */
public class AddCommand extends Command {
    /**
     * Represents the type of task to be added.
     */
    public enum TaskType {
        TODO,
        DEADLINE,
        EVENT
    }

    private final String description;
    private final String time1;
    private final String time2;
    private final TaskType taskType;

    /**
     * Constructs an AddCommand for a ToDo task.
     *
     * @param description The description of the task.
     * @param taskType    The type of task to add (should be TODO).
     */
    public AddCommand(String description, TaskType taskType) {
        this.description = description;
        this.time1 = null;
        this.time2 = null;
        this.taskType = taskType;
    }

    /**
     * Constructs an AddCommand for a Deadline task.
     *
     * @param description The description of the task.
     * @param deadline    The deadline for the task.
     * @param taskType    The type of task to add (should be DEADLINE).
     */
    public AddCommand(String description, String deadline, TaskType taskType) {
        this.description = description;
        this.time1 = deadline;
        this.time2 = null;
        this.taskType = taskType;
    }

    /**
     * Constructs an AddCommand for an Event task.
     *
     * @param description The description of the task.
     * @param fromTime    The start time of the event.
     * @param toTime      The end time of the event.
     * @param taskType    The type of task to add (should be EVENT).
     */
    public AddCommand(String description, String fromTime, String toTime, TaskType taskType) {
        this.description = description;
        this.time1 = fromTime;
        this.time2 = toTime;
        this.taskType = taskType;
    }

    /**
     * Executes the add command by creating a new task, adding it to the task list,
     * displaying a confirmation message, and saving the updated list to storage.
     *
     * @param tasks   The list of tasks to add to.
     * @param ui      The UI object for displaying the confirmation message.
     * @param storage The storage object for saving the updated task list.
     * @throws CoachException If task creation fails.
     */
    @Override
    public void execute(ArrayList<Task> tasks, UI ui, Storage storage) throws CoachException {
        Task task = createTask();
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
        storage.save(tasks);
    }

    /**
     * Creates a task object based on the task type and provided parameters.
     *
     * @return The created Task object (ToDo, Deadline, or Event).
     * @throws CoachException If the task type is unknown or task creation fails.
     */
    private Task createTask() throws CoachException {
        return switch (taskType) {
            case TODO -> new ToDo(description);
            case DEADLINE -> new Deadline(description, time1);
            case EVENT -> new Event(description, time1, time2);
            default -> throw new InvalidTaskFormatException("Unknown task type");
        };
    }
}
