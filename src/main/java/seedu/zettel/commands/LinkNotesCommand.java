package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidFormatException;
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.NoteSelfLinkException;
import seedu.zettel.exceptions.NotesAlreadyLinkedException;
import seedu.zettel.storage.Storage;


public class LinkNotesCommand extends Command {
    private static final int VALID_NOTE_ID_LENGTH = 8;
    private static final String VALID_NOTE_ID_REGEX = "^[a-f0-9]{8}$";

    private final String noteIdLinkedTo;
    private final String noteIdLinkedBy;

    /**
     * Constructs a LinkNotesCommand for linking two notes by their IDs.
     *
     * @param noteIdLinkedTo The ID of the note that will link to another note.
     * @param noteIdLinkedBy The ID of the note that is linked by the first note.
     */

    public LinkNotesCommand(String noteIdLinkedTo, String noteIdLinkedBy) {
        this.noteIdLinkedTo = noteIdLinkedTo;
        this.noteIdLinkedBy = noteIdLinkedBy;
    }

    
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

    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) throws 
            InvalidFormatException, NoNotesException, InvalidNoteIdException, NotesAlreadyLinkedException,
            NoteSelfLinkException {
        // Validate Inputs
        validateNoteIdFormat(noteIdLinkedTo);
        validateNoteIdFormat(noteIdLinkedBy);

        // Check if attempting to link a note to itself
        if (noteIdLinkedTo.equals(noteIdLinkedBy)) {
            throw new NoteSelfLinkException("Cannot link a note to itself. Note ID: '" + noteIdLinkedTo + "'.");
        }

        // If no notes at all in the list, throw an exception
        if (notes.isEmpty()) {
            throw new NoNotesException("You have no notes to link.");
        }

        // Try to find both notes
        Optional<Note> noteLinkedTo = notes.stream()
                .filter(n -> n.getId().equals(noteIdLinkedTo))
                .findFirst();
        if (noteLinkedTo.isEmpty()) {
            throw new InvalidNoteIdException("Note with ID '"+ noteIdLinkedTo + "' does not exist.");
        }

        Optional<Note> noteLinkedBy = notes.stream()
                .filter(n -> n.getId().equals(noteIdLinkedBy))
                .findFirst();
        if (noteLinkedBy.isEmpty()) {
            throw new InvalidNoteIdException("Note with ID '"+ noteIdLinkedBy + "' does not exist.");
        }
        
        // Check if link already exists
        if (noteLinkedTo.get().isLinkedTo(noteIdLinkedBy)) {
            throw new NotesAlreadyLinkedException("Note with ID '" + noteIdLinkedTo
                    + "' already links to note with ID '" + noteIdLinkedBy + "'.");
        }

        // Create unidirectional link
        noteLinkedTo.get().addLinkedBy(noteIdLinkedBy);
        noteLinkedBy.get().addLinkedTo(noteIdLinkedTo);
        ui.showSuccessfulLinking(noteLinkedTo.get().getTitle(), noteLinkedBy.get().getTitle());
    }

}
