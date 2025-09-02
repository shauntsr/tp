import java.util.Scanner;

public class Coach {
    static Task[] tasks = new Task[100];

    private static void printLine() {
        System.out.println("____________________________________________________________");
    }

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

            default:
                handleInput(input);
                break;
            }
        }
    }

    private static void handleInput(String input) {
        tasks[Task.getTaskCount()] = new Task(input, false);
        Task.incrementTaskCount();
        printLine();
        System.out.println(" added: " + input);
        printLine();
    }

    private static void handleMark(String[] inputs, boolean isMark) {
        printLine();
        try {
            int markIndex = Integer.parseInt(inputs[1]) - 1;
            if (markIndex < 0 || markIndex >= Task.getTaskCount()) {
                System.out.println("Invalid index!");
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
            System.out.println("Invalid index!");
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
}

