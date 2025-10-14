package seedu.zettel;

import java.util.Scanner;

public class Zettel {
    /**
     * Main entry-point for the java.zettel.Zettel application.
     */
    public static void main(String[] args) {
        System.out.println("Hello from ZettelCLI");
        System.out.println("What is your name?");

        Scanner in = new Scanner(System.in);
        System.out.println("Hello " + in.nextLine());
    }
}
