package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Logger;

import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;
import seedu.zettel.exceptions.NoNoteFoundException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.ZettelException;

/**
 * Command to delete a note by its ID.
 * Supports optional force deletion to skip confirmation prompt.
 */
public class DeleteNoteCommand extends Command {
    private static final Logger logger = Logger.getLogger(DeleteNoteCommand.class.getName());

    private final String noteId;
    private final boolean isForce;

    /**
     * Constructs a DeleteNoteCommand with the specified note ID and force flag.
     *
     * @param noteId The 8-character hexadecimal note ID to delete
     * @param force true to skip confirmation, false to prompt user
     */
    public DeleteNoteCommand(String noteId, boolean force) {
        this.noteId = noteId;
        this.isForce = force;
    }

    /**
     * Executes the delete command on the specified note.
     * Prompts for confirmation unless force flag is set.
     *
     * @param notes The list of all notes
     * @param ui The UI instance for user interaction
     * @param storage The storage instance for persistence
     * @throws ZettelException If the notes list is empty or the note doesn't exist
     */
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

        assert note != null : "Note should not be null after validation";

        boolean shouldDelete = isForce;
        if (!isForce) {
            ui.showDeleteConfirmation(noteId, note.getTitle());

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().trim().toLowerCase();
            shouldDelete = input.equals("y") || input.equals("yes");
        }

        if (shouldDelete) {
            notes.remove(note);
            storage.save(notes);
            logger.info("Note " + note.getTitle() + ", id " + noteId + " deleted.");
            ui.showNoteDeleted(noteId);
        } else {
            ui.showDeletionCancelled();
        }
    }
}
