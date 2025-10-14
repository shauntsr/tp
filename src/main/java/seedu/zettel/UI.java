package seedu.zettel;

import java.util.List;
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

    public void showError(String message) {
        System.out.println(message);
    }

    public void showBye() {
        System.out.println(" Bye. Hope to see you again soon!");
    }

    public void showNoPinnedNotes() {
        System.out.println("No pinned notes found. Pin a note to add to this list.\n");
    }

    public void showNoNotes() {
        System.out.println("No notes found.\n");
    }
    public void showNoteList(List<Note> notes, boolean isPinned) {
        // Show number of notes found.
        if (isPinned) {
            System.out.println("You have " + notes.size() + " pinned notes:");
        } else {
            System.out.println("You have " + notes.size() + " notes:");
        }

        // Show the list of notes.
        for (int idx = 0; idx < notes.size(); idx++) {
            System.out.println("    " + (idx + 1) + ". " + notes.get(idx));
        }
    }

    public void printLine() {
        System.out.println(LINE);
    }

    public void close() {
        scanner.close();
    }
}
