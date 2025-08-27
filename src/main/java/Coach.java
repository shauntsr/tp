import java.util.Scanner;

public class Coach {


    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("____________________________________________________________");
        System.out.println(" Hello! I'm Coach");
        System.out.println(" What can I do for you?");
        System.out.println("____________________________________________________________");

        while (true) {
            String input = in.nextLine().trim();

            if (input.equalsIgnoreCase("bye")) {
                System.out.println("____________________________________________________________");
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println("____________________________________________________________");
                break;
            }
            else {

                System.out.println("____________________________________________________________");
                System.out.println(input);
                System.out.println("____________________________________________________________");
            }
        }
        in.close();

    }
}

