package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.Optional;

import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidFormatException;
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.ZettelException;

public class PinNoteCommand extends Command {
    private static final int VALID_NOTE_ID_LENGTH = 8;
    private final boolean isPin;
    private final String noteId;

    public PinNoteCommand(String noteId, boolean isPin) {
        this.noteId = noteId;
        this.isPin = isPin;
    }

    /**
     * Validates that the noteId has the correct format (8 alphanumeric characters).
     *
     * @param noteId The noteId to validate.
     * @throws InvalidFormatException If the noteId format is invalid.
     */
    private void validateNoteIdFormat(String noteId) throws InvalidFormatException {
        if (noteId == null || noteId.length() != VALID_NOTE_ID_LENGTH) {
            throw new InvalidFormatException(
                "Note ID must be exactly " + VALID_NOTE_ID_LENGTH + " characters long.");
        }
        if (!noteId.matches("[a-zA-Z0-9]+")) {
            throw new InvalidFormatException(
                "Note ID must contain only alphanumeric characters.");
        }
    }

    @Override
    public void execute(ArrayList<Note> notes, UI ui, Storage storage) throws ZettelException {
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
