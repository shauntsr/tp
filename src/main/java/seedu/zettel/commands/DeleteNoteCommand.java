package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;
import seedu.zettel.exceptions.NoNoteFoundException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.ZettelException;

public class DeleteNoteCommand extends Command {
    private final String noteId;
    private boolean force;

    public DeleteNoteCommand(String noteId, boolean force) {
        this.noteId = noteId;
        this.force = force;
    }

    @Override
    public void execute(ArrayList<Note> notes, UI ui, Storage storage) throws ZettelException {

        // Validation 1: Check if the notes list is empty
        if (notes.isEmpty()) {
            throw new NoNotesException("You have no notes to delete.");
        }

        // Validation 2: Check if note with the given ID exists
        Optional<Note> maybe = notes.stream().filter(n -> n.getId().equals(noteId)).findFirst();

        if (maybe.isEmpty()) {
            throw new NoNoteFoundException("Note with ID '" + noteId + "' does not exist.");
        }

        // Happy path: Execute the delete operation

        Note note = maybe.get();

        assert note != null;

        boolean shouldDelete = force;
        if (!force) {
            ui.showDeleteConfirmation(noteId, note.getTitle());

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().trim().toLowerCase();
            shouldDelete = input.equals("y") || input.equals("yes");
        }

        if (shouldDelete) {
            notes.remove(note);
            storage.save(notes);
            ui.showNoteDeleted(noteId);
        } else {
            ui.showDeletionCancelled();
        }
    }
}
