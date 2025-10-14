package seedu.zettel;

import java.util.Scanner;

public class UI {
    private static final String LINE = "____________________________________________________________";
    private final Scanner scanner;

    public UI() {
        this.scanner = new Scanner(System.in);
    }


    public String readCommand() {
        if (!scanner.hasNextLine()) {
            return "";
        }
        return scanner.nextLine();
    }

    public void showWelcome() {
        printLine();
        System.out.println(" Hello! I'm Zettel");
        System.out.println(" What can I do for you?");
        printLine();
    }

    public void showDeletionNotFound() {

    }

    public void showDeletionCancelled() {
        System.out.println("Deletion cancelled");
    }

    public void showNoteDeleted(String id) {
        System.out.println("Note at " + id + " deleted");
    }

    public void showDeleteConfirmation(String note) {
        System.out.println(note);
    }

    public void showDeleteNotFound(String id) {
        System.out.println("No note found with id " + id);
    }

    public void showError(String message) {
        System.out.println(message);
    }

    public void showBye() {
        System.out.println(" Bye. Hope to see you again soon!");
    }

    public void printLine() {
        System.out.println(LINE);
    }

    public void close() {
        scanner.close();
    }
}
