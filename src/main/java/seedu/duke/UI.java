package seedu.duke;

import seedu.duke.tasks.Task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * Handles all user interface interactions for the Coach chatbot.
 * The UI class manages input reading and output display,
 * including welcome or goodbye messages, task listing and error messages.
 */
public class UI {
    private static final String LINE = "____________________________________________________________";
    private final Scanner scanner;

    /**
     * Constructs a UI object and initializes the Scanner for reading user input.
     */
    public UI() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads a command from the user input.
     *
     * @return The trimmed user input string.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays the welcome message when the application starts.
     */
    public void showWelcome() {
        printLine();
        System.out.println(" Hello! I'm Coach");
        System.out.println(" What can I do for you?");
        printLine();
    }

    /**
     * Displays the goodbye message when the user exits the application.
     */
    public void showBye() {
        System.out.println(" Bye. Hope to see you again soon!");
    }

    /**
     * Prints a horizontal line separator for formatting output.
     */
    public void printLine() {
        System.out.println(LINE);
    }

    /**
     * Displays an error message to the user.
     *
     * @param message The error message to display.
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Displays a confirmation message after a task has been added.
     * Shows the added task and the updated task count.
     *
     * @param task      The task that was added.
     * @param taskCount The total number of tasks after adding.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println(" added: " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays a confirmation message after a task has been deleted.
     * Shows the deleted task and the updated task count.
     *
     * @param task      The task that was deleted.
     * @param taskCount The total number of tasks after deletion.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays a confirmation message after a task has been marked or unmarked.
     * Shows whether the task was marked as done or not done.
     *
     * @param task   The task that was marked or unmarked.
     * @param isMark True if the task was marked as done, false if unmarked.
     */
    public void showTaskMarked(Task task, boolean isMark) {
        String action = isMark ? "marked this task as done" : "marked this task as not done yet";
        System.out.println("Nice! I've " + action + ":");
        System.out.println(" " + task);
    }

    /**
     * Displays the complete list of tasks.
     * Shows a message if the list is empty.
     *
     * @param tasks The list of tasks to display.
     */
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

    /**
     * Displays all tasks on a specific date, deadlines that end on it or events that contain date in its range
     * Shows a message if no tasks are found for the given date.
     *
     * @param date  The date to filter tasks by.
     * @param tasks The list of tasks occurring on the specified date.
     */
    public void showTasksOnDate(LocalDate date, List<Task> tasks) {
        System.out.println("Tasks on " + date.format(DateTimeFormatter.ofPattern("MMM d yyyy")) + ":");
        if (tasks.isEmpty()) {
            System.out.println(" No tasks found for this date.");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println(" " + (i + 1) + ". " + tasks.get(i));
            }
        }
    }

    /**
     * Displays tasks that match a search keyword.
     * Shows a message if no matching tasks are found.
     *
     * @param keyword       The search keyword used to find tasks.
     * @param matchingTasks The list of tasks that match the keyword.
     */
    public void showFinds(String keyword, List<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            System.out.println(" No matching tasks found.");
        } else {
            System.out.println(" Here are the matching tasks in your list:");
            for (int i = 0; i < matchingTasks.size(); i++) {
                System.out.println(" " + (i + 1) + "." + matchingTasks.get(i));
            }
        }
    }

    /**
     * Closes the Scanner to release resources.
     * Should be called when the application terminates.
     */
    public void close() {
        scanner.close();
    }
}