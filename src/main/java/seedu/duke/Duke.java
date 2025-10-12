package seedu.duke;

import java.util.Scanner;

public class Duke {
    /**
     * Main entry-point for the java.duke.Duke application.
     */
    public static void main(String[] args) {
        String logo = " ______    _   _       _  ____ _     ___ \n" +
                "|__  / ___| |_| |_ ___| |/ ___| |   |_ _|\n" +
                "  / / / _ \\ __| __/ _ \\ | |   | |    | | \n" +
                " / /_|  __/ |_| ||  __/ | |___| |___ | | \n" +
                "/____|\\___|\\___|\\__\\___|_|\\____|_____|___|\n";
        System.out.println("Hello from\n" + logo);
        System.out.println("What is your name?");

        Scanner in = new Scanner(System.in);
        System.out.println("Hello " + in.nextLine());
    }
}
