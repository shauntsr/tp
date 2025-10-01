package chatbot.tasks;

/**
 * Represents a todo task without any date or time constraints.
 * ToDo is a task that only contains a name.
 */
public class ToDo extends Task {

    /**
     * Constructs a ToDo task with the specified name.
     * The task is initially marked as not done.
     *
     * @param name The name of the todo task.
     */
    public ToDo(String name) {
        super(name, false);
    }

    /**
     * Constructs a ToDo task with the specified name and mark status.
     *
     * @param name   The name of the todo task.
     * @param isDone True if the task is done, false otherwise.
     */
    public ToDo(String name, boolean isDone) {
        super(name, isDone);
    }

    /**
     * Returns a string representation of the todo task for display.
     * Format: [T][X] name (if done) or [T][ ] name (if not done).
     *
     * @return The string representation of the todo task.
     */
    public String toString() {
        return "[T]" + getBaseString();
    }

    /**
     * Returns a string representation of the todo task for saving to file.
     * Format: T | 1 | name (if done) or T | 0 | name (if not done).
     *
     * @return The save format string representation of the todo task.
     */
    @Override
    public String toSaveFormat() {
        return "T | " + getSaveString();
    }
}
