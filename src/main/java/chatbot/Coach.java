package chatbot;

import chatbot.tasks.Deadline;
import chatbot.tasks.Event;
import chatbot.tasks.Task;
import chatbot.tasks.ToDo;

import java.util.Scanner;
import java.util.ArrayList;

public class Coach {
    private static final String TODO_EMPTY = "Task description cannot be empty!";
    private static final String DEADLINE_FORMAT = "Deadline format should be: deadline <description> /by <time>";
    private static final String DEADLINE_EMPTY = "Task description and deadline cannot be empty!";
    private static final String EVENT_FORMAT = "Event format should be: event <description> /from <time> /to <time>";
    private static final String EVENT_EMPTY = "Task description, start and end time cannot be empty!";
    private static final String INDEX_EMPTY = "Please specify a task number to ";
    private static final String INDEX_OOR = "Task number out of range!";
    private static final String INDEX_INVALID = "Task number must be a valid integer!";

    static ArrayList<Task> tasks = new ArrayList<>();

    public static void main(String[] args) throws CoachException {
        Scanner sc = new Scanner(System.in);

        handleWelcome();

        while (true) {
            String input = sc.nextLine().trim();
            String[] inputs = input.split(" ");
            String command = inputs[0].toLowerCase();

            try {
                switch (command) {
                case "bye":
                    handleBye();
                    sc.close();
                    return;

                case "list":
                    handleList();
                    break;

                case "mark":
                    handleMark(inputs, true);
                    break;

                case "unmark":
                    handleMark(inputs, false);
                    break;

                case "todo":
                    handleToDo(input);
                    break;

                case "deadline":
                    handleDeadline(input);
                    break;

                case "event":
                    handleEvent(input);
                    break;

                case "delete":
                    handleDelete(inputs);
                    break;

                default:
                    printLine();
                    throw new InvalidInputException(command);
                }
            } catch (CoachException e) {
                System.out.println(e.getMessage());
                printLine();
            }
        }
    }

    private static void handleToDo(String input) throws CoachException {
        printLine();
        String content = input.substring(4);
        if (content.isEmpty())
        {
            throw new EmptyDescriptionException(TODO_EMPTY);
        }
        else{
            tasks.add(new ToDo(content));
            printAddTask();
        }
        printLine();
    }

    private static void handleDeadline(String input) throws CoachException {
        printLine();
        try {
            String content = input.substring(9);
            int byIndex = content.indexOf("/by ");

            if (byIndex == -1) {
                throw new InvalidTaskFormatException(DEADLINE_FORMAT);
            }

            String name = content.substring(0, byIndex).trim();
            String deadline = content.substring(byIndex + 4).trim();

            if (name.isEmpty() || deadline.isEmpty()) {
                throw new EmptyDescriptionException(DEADLINE_EMPTY);
            }

            tasks.add(new Deadline(name, deadline));
            printAddTask();

        } catch (StringIndexOutOfBoundsException e) { //produces this error when trying to generate string after deadline but no string to be found
            throw new EmptyDescriptionException(DEADLINE_EMPTY);
        }
        printLine();
    }

    private static void handleEvent(String input) throws CoachException {
        printLine();
        try {

            String content = input.substring(6);

            int fromIndex = content.indexOf("/from");
            int toIndex = content.indexOf("/to");

            if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex) {
                throw new InvalidTaskFormatException(EVENT_FORMAT);
            }

            String taskName = content.substring(0, fromIndex).trim();
            String fromTime = content.substring(fromIndex + 6, toIndex).trim();
            String toTime = content.substring(toIndex + 4).trim();

            if (taskName.isEmpty() || fromTime.isEmpty() || toTime.isEmpty()) {
                throw new EmptyDescriptionException(EVENT_EMPTY);
            }

            tasks.add(new Event(taskName, fromTime, toTime));
            printAddTask();

        } catch (StringIndexOutOfBoundsException e) { //produces this error when trying to generate string after deadline but no string to be found
            throw new EmptyDescriptionException(EVENT_FORMAT);
        }
        printLine();
    }

    private static void handleMark(String[] inputs, boolean isMark) throws CoachException {
        printLine();
        int markIndex = parseTaskIndex(inputs, "mark/unmark");

        tasks.get(markIndex).setDone(isMark);
        String action = isMark ? "marked this task as done" : "marked this task as not done yet";
        System.out.println("Nice! I've " + action + ":");
        System.out.println(" " + tasks.get(markIndex));
        printLine();
    }

    private static void handleList() {
        printLine();
        System.out.println(" Here are the tasks in your list:");
        if (tasks.isEmpty()) {
            System.out.println(" No tasks yet!");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println(" " + (i + 1) + ". " + tasks.get(i));
            }
        }
        printLine();
    }

    private static void handleBye() {
        printLine();
        System.out.println(" Bye. Hope to see you again soon!");
        printLine();
    }

    private static void handleWelcome() {
        printLine();
        System.out.println(" Hello! I'm Coach");
        System.out.println(" What can I do for you?");
        printLine();
    }

    private static void handleDelete(String[] inputs) throws CoachException {
        printLine();
        int deleteIndex = parseTaskIndex(inputs, "delete");

        Task removedTask = tasks.remove(deleteIndex);

        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + removedTask);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        printLine();
    }

    private static int parseTaskIndex(String[] inputs, String actionName) throws CoachException {
        if (inputs.length < 2) {
            throw new EmptyDescriptionException(INDEX_EMPTY + actionName + "!");
        }

        try {
            int index = Integer.parseInt(inputs[1]) - 1;
            if (index < 0 || index >= tasks.size()) {
                throw new InvalidTaskIndexException(INDEX_OOR);
            }
            return index;
        } catch (NumberFormatException e) {
            throw new InvalidTaskIndexException(INDEX_INVALID);
        }
    }

    private static void printAddTask() {
        System.out.println("Got it. I've added this task:");
        System.out.println(" added: " + tasks.get(tasks.size() - 1));
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
    }



    private static void printLine() {
        System.out.println("____________________________________________________________");
    }

}

