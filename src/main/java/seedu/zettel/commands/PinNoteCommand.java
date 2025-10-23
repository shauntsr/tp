package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.zettel.Note;
import seedu.zettel.storage.Storage;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidFormatException;
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.ZettelException;

/**
 * Command to pin or unpin a note by its ID.
 * Pinned notes can be listed separately for quick access.
 */
public class PinNoteCommand extends Command {
    private static final int VALID_NOTE_ID_LENGTH = 8;
    private static final String VALID_NOTE_ID_REGEX = "^[a-f0-9]{8}$";
    private static final Logger logger = Logger.getLogger(PinNoteCommand.class.getName());

    private final boolean isPin;
    private final String noteId;

    /**
     * Constructs a PinNoteCommand with the specified note ID and pin status.
     *
     * @param noteId The 8-character hexadecimal note ID
     * @param isPin true to pin the note, false to unpin
     */
    public PinNoteCommand(String noteId, boolean isPin) {
        this.noteId = noteId;
        this.isPin = isPin;
    }

    /**
     * Validates that the noteId has the correct format.
     * Must be exactly 8 lowercase hexadecimal characters (0-9, a-f).
     *
     * @param noteId The noteId to validate
     * @throws InvalidFormatException If the noteId format is invalid
     */
    private void validateNoteIdFormat(String noteId) throws InvalidFormatException {
        if (noteId == null || noteId.length() != VALID_NOTE_ID_LENGTH) {
            throw new InvalidFormatException(
                    "Note ID must be exactly " + VALID_NOTE_ID_LENGTH + " characters long.");
        }
        if (!noteId.matches(VALID_NOTE_ID_REGEX)) {
            throw new InvalidFormatException(
                    "Note ID must contain only lowercase hexadecimal characters (0-9, a-f).");
        }
    }

    /**
     * Executes the pin/unpin command on the specified note.
     *
     * @param notes The list of all notes
     * @param ui The UI instance for user interaction
     * @param storage The storage instance for persistence
     * @throws ZettelException If the note ID is invalid or the note doesn't exist
     */
    @Override
    public void execute(ArrayList<Note> notes, UI ui, Storage storage) throws ZettelException {
        logger.info("Executing PinNoteCommand for noteId: " + noteId);
        // Validation 1: Check if noteId format is valid
        validateNoteIdFormat(noteId);

        // Validation 2: Check if the notes list is empty
        if (notes.isEmpty()) {
            throw new NoNotesException("You have no notes to pin/unpin.");
        }

        // Validation 3: Check if note with the given ID exists
        Optional<Note> maybe = notes.stream().filter(n -> n.getId().equals(noteId)).findFirst();
        if (maybe.isEmpty()) {
            throw new InvalidNoteIdException("Note with ID '" + noteId + "' does not exist.");
        }

        // Happy path: Execute the pin/unpin operation
        Note note = maybe.get();
        note.setPinned(isPin);
        ui.showJustPinnedNote(note, noteId);
        storage.save(notes);
    }
}
