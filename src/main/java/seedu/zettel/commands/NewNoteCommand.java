package seedu.zettel.commands;

import seedu.zettel.IdGenerator;
import seedu.zettel.exceptions.InvalidInputException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;

import java.time.Instant;
import java.util.ArrayList;

/**
 * Command to create a new note with a given title and optional body.
 * Generates a deterministic ID based on the title and creation timestamp.
 */
public class NewNoteCommand extends Command {
    private final String title;
    private final String body;

    /**
     * Constructs a NewNoteCommand with the specified title and body.
     *
     * @param title The title of the note
     * @param body The body content of the note
     */
    public NewNoteCommand(String title, String body) {
        this.title = title;
        this.body = body;
    }

    /**
     * Executes the command to create a new note.
     * Generates a hash-based ID from title and timestamp,
     * checks for duplicate filenames, and saves the note.
     *
     * @param notes The list of existing notes
     * @param ui The UI instance for user interaction
     * @param storage The storage instance for persistence
     * @throws ZettelException If a note with the same filename already exists
     */
    @Override
    public void execute(ArrayList<Note> notes, UI ui, Storage storage) throws ZettelException {
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

        notes.add(newNote);
        storage.save(notes);
        ui.showAddedNote(newNote);
    }
}
