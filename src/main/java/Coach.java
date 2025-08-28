import java.util.Scanner;

public class Coach {
    static Task[] tasks = new Task[100];
    static int taskCount = 0;

    private static String printTask(Task task) {
        String mark = task.isDone() ? "[X]" : "[ ]";
        return mark + " " + task.getName();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("____________________________________________________________");
        System.out.println(" Hello! I'm Coach");
        System.out.println(" What can I do for you?");
        System.out.println("____________________________________________________________");

        while (true) {
            String input = sc.nextLine().trim();
            String[] inputs = input.split(" ");

            if (inputs[0].equalsIgnoreCase("bye")) {
                System.out.println("____________________________________________________________");
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println("____________________________________________________________");
                break;
            } else if (inputs[0].equalsIgnoreCase("list")) {
                System.out.println("____________________________________________________________");
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + ". " + printTask(tasks[i]));
                }
                if (taskCount == 0) {
                    System.out.println(" No tasks yet!");
                }
                System.out.println("____________________________________________________________");
            } else if (inputs[0].equalsIgnoreCase("mark")) {
                System.out.println("____________________________________________________________");
                int index = Integer.parseInt(inputs[1]) - 1;
                tasks[index].setDone(true);
                System.out.println("Nice! I've marked this task as done:");
                System.out.println(" " + printTask(tasks[index]));
                System.out.println("____________________________________________________________");
            } else if (inputs[0].equalsIgnoreCase("unmark")) {
                System.out.println("____________________________________________________________");
                int index = Integer.parseInt(inputs[1]) - 1;
                tasks[index].setDone(false);
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println(" " + printTask(tasks[index]));
                System.out.println("____________________________________________________________");
            } else {
                tasks[taskCount] = new Task(input, false);
                taskCount++;
                System.out.println("____________________________________________________________");
                System.out.println(" added: " + input);
                System.out.println("____________________________________________________________");
            }
        }
        sc.close();
    }
}

