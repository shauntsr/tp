package seedu.zettel.commands;

import seedu.zettel.exceptions.EditorNotFoundException;
import seedu.zettel.exceptions.NoNoteFoundException;
import seedu.zettel.util.EditorUtil;
import seedu.zettel.util.IdGenerator;
import seedu.zettel.exceptions.InvalidInputException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.Note;
import seedu.zettel.storage.Storage;
import seedu.zettel.UI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Command to create a new note with a given title and optional body.
 * Generates a deterministic ID based on the title and creation timestamp.
 */
public class NewNoteCommand extends Command {
    private final String title;
    private final String body;
    private final boolean shouldOpenEditor;

    /**
     * Constructs a NewNoteCommand with the specified title and body.
     *
     * @param title The title of the note
     * @param body The body content of the note
     */
    public NewNoteCommand(String title, String body) {
        this.title = title;
        this.body = body == null ? "" : body;
        this.shouldOpenEditor = (body == null);
    }

    /**
     * Executes the command to create a new note.
     * Generates a hash-based ID from title and timestamp,
     * checks for duplicate filenames, and saves the note.
     *
     * @param notes   The list of existing notes
     * @param tags    The list of current tags.
     * @param ui      The UI instance for user interaction
     * @param storage The storage instance for persistence
     * @throws ZettelException If a note with the same filename already exists
     */
    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) throws ZettelException {
        assert title != null : "Title should not be null";

        Instant now = Instant.now();

        // Generate deterministic ID based on title and timestamp
        String idInput = title + now.toString();
        String id = IdGenerator.generateId(idInput);

        String filename = title.replaceAll("\\s+", "_") + ".txt";

        // Check if filename already exists
        boolean filenameExists = notes.stream()
                .anyMatch(n -> n.getFilename().equals(filename));

        if (filenameExists) {
            throw new InvalidInputException("Note already exists!");
        }

        Note newNote = new Note(
                id,
                title,
                filename,
                body,
                now,
                now
        );

        // Save note to disk
        notes.add(newNote);

        storage.createStorageFile(newNote);
        storage.save(notes);

        // If no body was provided, open in editor
        if (shouldOpenEditor) {
            try {
                Path notePath = storage.getNotePath(filename);
                ui.showOpeningEditor();
                EditorUtil.openInEditor(notePath);

                // Read the edited content from disk back into Object
                String editedBody = Files.readString(notePath);
                newNote.setBody(editedBody);

                // Update the modified timestamp and save again (to update timestamp)
                newNote.updateModifiedAt();
                storage.save(notes);

                ui.showNoteSavedFromEditor();
            } catch (EditorNotFoundException e) {
                ui.showError("Could not open text editor, empty body will be used: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // preserves interrupted state
                throw new ZettelException("Editor was interrupted: " + e.getMessage());
            } catch (NoNoteFoundException e) {
                throw new ZettelException("Failed to open note file: " + e.getMessage());
            } catch (IOException e) {
                throw new ZettelException("Failed to read edited content: " + e.getMessage());
            }
        }

        ui.showAddedNote(newNote);
    }
}
