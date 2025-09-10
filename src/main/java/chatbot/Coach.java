package chatbot;

import chatbot.tasks.Deadline;
import chatbot.tasks.Event;
import chatbot.tasks.Task;
import chatbot.tasks.ToDo;

import java.util.Scanner;

public class Coach {
    public static final int MAX_TASKS = 100;
    private static final String TODO_EMPTY = "Task description cannot be empty!";
    private static final String DEADLINE_FORMAT = "Deadline format should be: deadline <description> /by <time>";
    private static final String DEADLINE_EMPTY = "Task description and deadline cannot be empty!";
    private static final String EVENT_FORMAT = "Event format should be: event <description> /from <time> /to <time>";
    private static final String EVENT_EMPTY = "Task description, start and end time cannot be empty!";

    static Task[] tasks = new Task[MAX_TASKS];

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
            tasks[Task.getTaskCount()] = new ToDo(content);
            Task.incrementTaskCount();
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

            tasks[Task.getTaskCount()] = new Deadline(name, deadline);
            Task.incrementTaskCount();
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

            tasks[Task.getTaskCount()] = new Event(taskName, fromTime, toTime);
            Task.incrementTaskCount();
            printAddTask();

        } catch (StringIndexOutOfBoundsException e) { //produces this error when trying to generate string after deadline but no string to be found
            throw new EmptyDescriptionException(EVENT_FORMAT);
        }
        printLine();
    }

    private static void handleMark(String[] inputs, boolean isMark) throws CoachException {
        printLine();
        try {
            if (inputs.length < 2) {
                throw new EmptyDescriptionException("Please specify a task number to mark/unmark!");
            }
            int markIndex = Integer.parseInt(inputs[1]) - 1;
            if (markIndex < 0 || markIndex >= Task.getTaskCount()) {
                throw new InvalidTaskIndexException("Task number out of range!");
            }

            tasks[markIndex].setDone(isMark);
            String action = isMark ? "marked this task as done" : "marked this task as not done yet";
            System.out.println("Nice! I've " + action + ":");
            System.out.println(" " + tasks[markIndex]);

        } catch (NumberFormatException e) {
            throw new InvalidTaskIndexException("Task number must be a valid integer!");
        }
        printLine();
    }

    private static void handleList() {
        printLine();
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < Task.getTaskCount(); i++) {
            System.out.println(" " + (i + 1) + ". " + tasks[i]);
        }
        if (Task.getTaskCount() == 0) {
            System.out.println(" No tasks yet!");
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

    private static void printAddTask() {
        System.out.println("Got it. I've added this task:");
        System.out.println(" added: " + tasks[Task.getTaskCount() - 1]);
        System.out.println(" Now you have " + Task.getTaskCount() + " tasks in the list.");
    }

    private static void printLine() {
        System.out.println("____________________________________________________________");
    }

}

