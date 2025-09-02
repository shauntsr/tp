import java.util.Scanner;

public class Coach {
    static Task[] tasks = new Task[100];

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        handleWelcome();

        while (true) {
            String input = sc.nextLine().trim();
            String[] inputs = input.split(" ");
            String command = inputs[0].toLowerCase();

            switch (command) {
            case "bye":
                handleBye();
                sc.close();
                return;

            case "list":
                handleList();
                break;

            case "mark":
                handleMark(inputs,true);
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
                System.out.println("Invalid command!");
                printLine();
                break;
            }
        }
    }

    private static void handleToDo(String input) {
        printLine();
        String content = input.substring(4);
        if (content.isEmpty())
        {
            printEmpty();
            return;
        }
        else{
            tasks[Task.getTaskCount()] = new ToDo(content);
            Task.incrementTaskCount();
            printAddTask();
        }
        printLine();
    }

    private static void handleDeadline(String input) {
        printLine();
        try {
            String content = input.substring(9);
            int byIndex = content.indexOf("/by ");
            if (byIndex == -1) {
                printInvalidFormat();
                printLine();
                return;
            }

            String name = content.substring(0, byIndex).trim();
            String deadline = content.substring(byIndex + 4).trim();

            if (name.isEmpty() || deadline.isEmpty()) {
                printEmpty();
                return;
            }

            tasks[Task.getTaskCount()] = new Deadline(name, deadline);
            Task.incrementTaskCount();
            printAddTask();

        } catch (Exception e) {
            printInvalidFormat();
            return;
        }
        printLine();
    }

    private static void handleEvent(String input) {
        printLine();
        try {
            String content = input.substring(6);

            int fromIndex = content.indexOf("/from ");
            int toIndex = content.indexOf("/to ");

            if (fromIndex == -1 || toIndex == -1 || fromIndex >= toIndex) {
                printInvalidFormat();
                return;
            }

            String taskName = content.substring(0, fromIndex).trim();
            String fromTime = content.substring(fromIndex + 6, toIndex).trim();
            String toTime = content.substring(toIndex + 4).trim();

            if (taskName.isEmpty() || fromTime.isEmpty() || toTime.isEmpty()) {
                printEmpty();
                return;
            }

            tasks[Task.getTaskCount()] = new Event(taskName, fromTime, toTime);
            Task.incrementTaskCount();
            printAddTask();

        } catch (Exception e) {
            printInvalidFormat();
            return;
        }
        printLine();
    }

    private static void handleMark(String[] inputs, boolean isMark) {
        printLine();
        try {
            int markIndex = Integer.parseInt(inputs[1]) - 1;
            if (markIndex < 0 || markIndex >= Task.getTaskCount()) {
                printInvalidIndex();
                return;
            } else {
                if (isMark) {
                    tasks[markIndex].setDone(true);
                    System.out.println("Nice! I've marked this task as done:");
                }
                else{
                    tasks[markIndex].setDone(false);
                    System.out.println("OK, I've marked this task as not done yet:");
                }
                System.out.println(" " + tasks[markIndex]);
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            printInvalidIndex();
            return;
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

    private static void printEmpty() {
        System.out.println("Empty input!");
        printLine();
    }

    private static void printInvalidFormat() {
        System.out.println("Invalid format!");
        printLine();
    }

    private static void printInvalidIndex() {
        System.out.println("Invalid index!");
        printLine();
    }
}

