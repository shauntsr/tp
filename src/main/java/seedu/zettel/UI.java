package seedu.zettel;

import java.util.ArrayList;
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
            return "bye";  // Auto-exit when no more input (prevents hanging in CI)
        }
        return scanner.nextLine();
    }

    public void showWelcome() {
        printLine();
        System.out.println(" Hello! I'm Zettel");
        System.out.println(" What can I do for you?");
        printLine();
        System.out.println();
        System.out.println(" Available Commands:");
        System.out.println("   init <repo-name>           - Initialize a new repository");
        System.out.println("   new -t <title> [-b <body>] - Create a new note");
        System.out.println("   list [-p]                  - List all notes (or pinned only)");
        System.out.println("   delete [-f] <note-id>      - Delete a note by ID");
        System.out.println("   pin <note-id>              - Pin a note");
        System.out.println("   unpin <note-id>            - Unpin a note");
        System.out.println("   find <text>                - Search for notes");
        System.out.println("   bye                        - Exit the application");
        System.out.println();
        printLine();
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

    public void showAddedNote(Note note) {
        System.out.println("Note created: " + note.getFilename() + " #" + note.getId());
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

    public void showNoNotesFoundMessage() {
        System.out.println(" No notes found matching the search criteria.");
    }

    public void showFoundNotesMessage(ArrayList<Note> matchedNotes) {
        System.out.println(" Here are the matching notes in your list:");
        for (int i = 0; i < matchedNotes.size(); i++) {
            System.out.println(" " + (i + 1) + ". " + matchedNotes.get(i));
        }
    }

    public void showJustPinnedNote(Note note, String noteId) {
        System.out.println(" Got it. I've " + (note.isPinned() ? "pinned" : "unpinned") + " this note: " + noteId);
    }

    public void showRepoInit(String repoName) {
        System.out.println(" Repository /" + repoName + " has been created.");
    }
}
