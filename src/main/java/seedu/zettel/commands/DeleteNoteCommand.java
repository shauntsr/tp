package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;
import seedu.zettel.exceptions.NoNoteException;
import seedu.zettel.exceptions.ZettelException;

public class DeleteNoteCommand extends Command {
    private final String id;
    private boolean force;

    public DeleteNoteCommand(String noteId, boolean force) {
        this.id = noteId;
        this.force = force;
    }

    @Override
    public void execute(ArrayList<Note> notes, UI ui, Storage storage) throws ZettelException {
        Optional<Note> maybe = notes.stream().filter(n -> n.getId().equals(id)).findFirst();

        if (maybe.isEmpty()) {
            ui.showDeleteNotFound(id);
            throw new NoNoteException("You have no notes to delete.");
        }

        Note note = maybe.get();

        boolean shouldDelete = force;
        if (!force) {
            ui.showDeleteConfirmation("Confirm deletion on '" + note.getTitle() + "', id " + id + "? (y/n)");

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().trim().toLowerCase();
            shouldDelete = input.equals("y") || input.equals("yes");
        }

        if (shouldDelete) {
            notes.remove(note);
            storage.save(notes);
            ui.showNoteDeleted(id);
        } else {
            ui.showDeletionCancelled();
        }
    }


}
