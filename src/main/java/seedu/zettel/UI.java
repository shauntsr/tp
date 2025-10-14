package seedu.zettel;

import java.util.ArrayList;
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

    public void printLine() {
        System.out.println(LINE);
    }

    public void close() {
        scanner.close();
    }

    public void showNoNotesFoundMessage() {
        System.out.println(" No notes found matching the search criteria.");
    }

    public void showFoundNotesMessage(ArrayList<Note> matchedNotes) {
        System.out.println(" Here are the matching notes in your list:");
        for (int i = 0; i < matchedNotes.size(); i++) {
            System.out.println(" " + (i + 1) + ". " + matchedNotes.get(i).toStringWithIndex(i));
        }
    }

    public void showJustPinnedNote(Note note, int idx) {
        System.out.println(" Got it. I've " + (note.isPinned() ? "pinned" : "unpinned") + " this note:");
        System.out.println(note.toStringWithIndex(idx));
    }
}
