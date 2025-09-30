package chatbot;

import chatbot.tasks.Deadline;
import chatbot.tasks.Event;
import chatbot.tasks.Task;
import chatbot.tasks.ToDo;

import java.util.Scanner;
import java.util.ArrayList;

public class UI {
    private static final String LINE = "____________________________________________________________";
    private final Scanner scanner;

    public UI() {
        this.scanner = new Scanner(System.in);
    }

    public String readCommand() {
        return scanner.nextLine().trim();
    }

    public void showWelcome() {
        showLine();
        System.out.println(" Hello! I'm Coach");
        System.out.println(" What can I do for you?");
        showLine();
    }

    public void showBye() {
        System.out.println(" Bye. Hope to see you again soon!");
    }

    public void showLine() {
        System.out.println(LINE);
    }

    public void showError(String message) {
        System.out.println(message);
    }


    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println(" added: " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    public void showTaskMarked(Task task, boolean isMark) {
        String action = isMark ? "marked this task as done" : "marked this task as not done yet";
        System.out.println("Nice! I've " + action + ":");
        System.out.println(" " + task);
    }

    public void showTaskList(ArrayList<Task> tasks) {
        System.out.println(" Here are the tasks in your list:");
        if (tasks.isEmpty()) {
            System.out.println(" No tasks yet!");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println(" " + (i + 1) + ". " + tasks.get(i));
            }
        }
    }

    public void close() {
        scanner.close();
    }
}
