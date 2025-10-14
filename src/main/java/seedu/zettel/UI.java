package seedu.zettel;

import java.util.List;
import java.util.Scanner;

public class UI {
    private static final String LINE = "____________________________________________________________";
    private final Scanner scanner;

    public UI() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads a command from the user.
     *
     * @return the user's input as a trimmed string
     */
    public String readCommand() {
        System.out.print("> ");
        return scanner.nextLine().trim();
    }

    /**
     * Displays the welcome greeting and available commands.
     */
    public void showWelcome() {
        printLine();
        System.out.println(" Hello! I'm Zettel");
        System.out.println(" What can I do for you?");
        System.out.println();
        System.out.println(" Available commands:");
        System.out.println("   init <repo-name>           - Initialize a new repository");
        System.out.println("   new -t <title> [-b <body>] - Create a new note");
        System.out.println("   list [-p]                  - List all notes (or pinned only)");
        System.out.println("   delete [-f] <note-id>      - Delete a note");
        System.out.println("   pin <note-id>              - Pin/unpin a note");
        System.out.println("   find \"<text>\"              - Search for notes");
        System.out.println("   exit                       - Exit the application");
        printLine();
    }

    /**
     * Displays the goodbye message.
     */
    public void showBye() {
        printLine();
        System.out.println(" Bye. Hope to see you again soon!");
        printLine();
    }

    /**
     * Displays a general message to the user.
     *
     * @param message the message to display
     */
    public void showMessage(String message) {
        System.out.println(" " + message);
    }

    /**
     * Displays an error message to the user.
     *
     * @param message the error message to display
     */
    public void showError(String message) {
        System.out.println(" [ERROR] " + message);
    }

    /**
     * Displays a list of notes.
     *
     * @param notes the list of notes to display
     * @param header optional header message
     */
    public void showNotes(List<Note> notes, String header) {
        printLine();
        if (header != null && !header.isEmpty()) {
            System.out.println(" " + header);
        }

        if (notes.isEmpty()) {
            System.out.println(" No notes found.");
        } else {
            for (int i = 0; i < notes.size(); i++) {
                System.out.println(" " + notes.get(i).toStringWithIndex(i + 1));
            }
        }
        printLine();
    }

    /**
     * Prompts the user for confirmation (yes/no).
     *
     * @param message the confirmation prompt message
     * @return true if user confirms (y/yes), false otherwise
     */
    public boolean confirm(String message) {
        System.out.print(" " + message + " (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        return response.equals("y") || response.equals("yes");
    }

    /**
     * Displays a horizontal line separator.
     */
    public void printLine() {
        System.out.println(LINE);
    }

    /**
     * Closes the scanner resource.
     */
    public void close() {
        scanner.close();
    }
}
