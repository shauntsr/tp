package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

/**
 * Command to delete a note by its ID.
 * Supports optional force deletion to skip confirmation prompt.
 */
public class DeleteNoteCommand extends Command {

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
     * @param notes   The list of all notes
     * @param tags    The list of current tags.
     * @param ui      The UI instance for user interaction
     * @param storage The storage instance for persistence
     * @throws ZettelException If the notes list is empty or the note doesn't exist
     */
    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) throws ZettelException {

        // Validation 1: Check if the notes list is empty
        if (notes.isEmpty()) {
            throw new NoNotesException("You have no notes to delete.");
        }

        // Validation 2: Check if note with the given ID exists
        Optional<Note> maybe = notes.stream().filter(n -> n.getId().equals(noteId)).findFirst();

        if (maybe.isEmpty()) {
            throw new InvalidNoteIdException("Note with ID '" + noteId + "' does not exist.");
        }

        // Happy path: Execute the delete operation
        Note note = maybe.get();

        assert note != null : "Note should not be null";

        boolean shouldDelete = isForce;
        if (!isForce) {
            ui.showDeleteNoteConfirmation(noteId, note.getTitle());

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().trim().toLowerCase();
            shouldDelete = input.equals("y") || input.equals("yes");
        }

        if (shouldDelete) {
            // Delete the physical body file from notes/ directory
            storage.deleteStorageFile(note.getFilename());

            // Remove note from ArrayList (this removes metadata)
            notes.remove(note);

            // Save updated index (writes to index.txt)
            storage.save(notes);

            // Log and show confirmation
            ui.showNoteDeleted(noteId);
        } else {
            ui.showDeletionCancelled();
        }
    }
}
