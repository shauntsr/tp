package seedu.duke.tasks;

/**
 * Represents an abstract task in the Coach chatbot.
 * A oTask object contains a name and completion status.
 * This class serves as the base class for specific task types such as ToDo, Deadline, and Event.
 */
public abstract class Task {
    private String name;
    private boolean isDone;

    /**
     * Returns the name of the task.
     *
     * @return The task name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the task.
     *
     * @param name The new task name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Checks if the task is marked as done.
     *
     * @return True if the task is done, false otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Sets the completion status of the task.
     *
     * @param done True to mark the task as done, false otherwise.
     */
    public void setDone(boolean done) {
        isDone = done;
    }

    /**
     * Constructs a Task with the specified name and completion status.
     *
     * @param name   The name of the task.
     * @param isDone True if the task is done, false otherwise.
     */
    public Task(String name, boolean isDone) {
        this.name = name;
        this.isDone = isDone;
    }

    /**
     * Constructs a Task with the specified name.
     * The task is initially marked as not done.
     *
     * @param name The name of the task.
     */
    public Task(String name) {
        this(name, false);
    }

    /**
     * Returns the base string representation of the task with completion status.
     * Format: [X] name (if done) or [ ] name (if not done).
     *
     * @return The base string representation of the task.
     */
    protected String getBaseString() {
        String mark = isDone ? "[X]" : "[ ]";
        return mark + " " + name;
    }

    /**
     * Returns the string representation for saving the task to file.
     * Format: 1 | name (if done) or 0 | name (if not done).
     *
     * @return The save format string representation of the task.
     */
    protected String getSaveString() {
        // 1 = done, 0 = not done
        return (isDone ? "1" : "0") + " | " + name;
    }

    /**
     * Returns a string representation of the task for display to the user.
     * This method must be implemented by subclasses.
     *
     * @return The string representation of the task.
     */
    @Override
    public abstract String toString();

    /**
     * Returns a string representation of the task for saving to file.
     * This method must be implemented by subclasses.
     *
     * @return The save format string representation of the task.
     */
    public abstract String toSaveFormat();
}
