package chatbot.tasks;

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

    @Override
    public abstract String toString();
}
