package seedu.duke.tasks;

public abstract class Task {
    private String name;
    private boolean isDone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public Task(String name, boolean isDone) {
        this.name = name;
        this.isDone = isDone;
    }

    public Task(String name) {
        this(name, false);
    }

    protected String getBaseString() {
        String mark = isDone ? "[X]" : "[ ]";
        return mark + " " + name;
    }

    protected String getSaveString() {
        // 1 = done, 0 = not done
        return (isDone ? "1" : "0") + " | " + name;
    }

    @Override
    public abstract String toString();

    public abstract String toSaveFormat();
}
