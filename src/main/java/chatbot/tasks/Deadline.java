package chatbot.tasks;

public class Deadline extends ToDo {
    protected String by;

    public Deadline(String name, String by) {
        super(name,  false);
        this.by = by;
    }

    public Deadline(String name, boolean isDone, String by) {
        super(name, false);
        this.by = by;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + getBaseString() + " (by: " + by + ")";
    }
}
