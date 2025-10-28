package seedu.zettel.commands;

import seedu.zettel.exceptions.EditorNotFoundException;
import seedu.zettel.exceptions.NoNoteFoundException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.util.EditorUtil;
import seedu.zettel.Note;
import seedu.zettel.storage.Storage;
import seedu.zettel.UI;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Command to edit an existing note's body in the default text editor.
 * The note is identified by its ID hash and only the body content can be modified.
 */
public class EditNoteCommand extends Command {
    private final String noteId;

    /**
     * Constructs an EditNoteCommand with the specified note ID.
     *
     * @param noteId The ID of the note to edit
     */
    public EditNoteCommand(String noteId) {
        this.noteId = noteId;
    }

    /**
     * Executes the command to edit a note's body in a text editor.
     * Opens the note file in the default editor, waits for it to close,
     * then refreshes the notes list from storage.
     *
     * @param notes   The list of existing notes
     * @param tags    The list of current tags
     * @param ui      The UI instance for user interaction
     * @param storage The storage instance for persistence
     * @throws ZettelException If the note is not found or editor operations fail
     */
    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage)
            throws ZettelException {

        // Find the note with matching ID
        Note targetNote = notes.stream()
                .filter(n -> n.getId().equals(noteId))
                .findFirst()
                .orElseThrow(() -> new NoNoteFoundException("Note with ID '" + noteId + "' not found."));

        String filename = targetNote.getFilename();
        Path notePath = storage.getNotePath(filename);

        try {
            ui.showOpeningEditor();

            EditorUtil.openInEditor(notePath);

            // Refresh ArrayList with all notes from Storage, after editing on disk
            ArrayList<Note> reloadedNotes = storage.load();
            notes.clear();
            notes.addAll(reloadedNotes);

            // Find the updated note to show confirmation
            Note updatedNote = notes.stream()
                    .filter(n -> n.getId().equals(noteId))
                    .findFirst()
                    .orElseThrow(() -> new NoNoteFoundException("Note disappeared after editing"));

            ui.showNoteEdited(updatedNote);

        } catch (EditorNotFoundException e) {
            ui.showError("Could not open text editor: " + e.getMessage());
            throw new ZettelException("Failed to open editor: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ZettelException("Editor was interrupted: " + e.getMessage());
        } catch (NoNoteFoundException e) {
            throw new ZettelException("Note file not found: " + e.getMessage());
        }
    }
}