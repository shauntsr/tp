public class Task {
    private String name;
    private boolean isDone;
    private final int id;
    private static int taskCount = 0;

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

    public static int getTaskCount() {
        return taskCount;
    }

    public static void setTaskCount(int taskCount) {
        Task.taskCount = taskCount;
    }

    public static void incrementTaskCount() {
        Task.taskCount = taskCount++;
    }

    public int getId() {
        return id;
    }

    public Task(String name, boolean isDone) {
        this.name = name;
        this.isDone = isDone;
        taskCount++;
        this.id = taskCount;
    }

    public String toString() {
        String mark = isDone ? "[X]" : "[ ]";
        return mark + " " + name;
    }

}
