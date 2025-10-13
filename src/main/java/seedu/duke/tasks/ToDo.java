package seedu.duke.tasks;

public class ToDo extends Task {

    public ToDo(String name) {
        super(name, false);
    }

    public ToDo(String name, boolean isDone) {
        super(name, isDone);
    }

    public String toString() {
        return "[T]" + getBaseString();
    }

    @Override
    public String toSaveFormat() {
        return "T | " + getSaveString();
    }
}
