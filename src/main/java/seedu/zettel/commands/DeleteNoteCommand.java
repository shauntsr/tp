package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;

public class DeleteNoteCommand extends Command {
    private final String id;
    private boolean force = false;

    public DeleteNoteCommand(String noteId, boolean force) {
        this.id = noteId;
        this.force = force;
    }

    @Override
    public void execute(ArrayList<Note> notes, UI ui, Storage storage) {
        Optional<Note> maybe = notes.stream().filter(n -> n.getId().equals(id)).findFirst();

        if (!maybe.isPresent()) {
            System.out.println("No note found with id " + id);
            return;
        }

        Note note = maybe.get();

        boolean shouldDelete = force;
        if (!force) {
            System.out.println("Are you sure you want to delete '" + note.getTitle() + "', id " + id + "? (y/n)");

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().trim().toLowerCase();
            shouldDelete = input.equals("y") || input.equals("yes");
        }

        if (shouldDelete) {
            notes.remove(note);
            // storage.deleteNoteFile(note);
            System.out.println("note at " + id + " deleted");
        } else {
            System.out.println("Deletion cancelled");
        }
    }
}
