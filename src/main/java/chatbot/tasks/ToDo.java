package chatbot.tasks;

public class ToDo extends Task {

    public ToDo(String name) {
        super(name,  false);
    }

    public ToDo(String name, boolean isDone) {
        super(name, false);
    }

    public String toString() {
        return "[T]" + getBaseString();
    }
}
