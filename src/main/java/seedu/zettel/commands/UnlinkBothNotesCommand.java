package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidInputException;
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.NoteSelfLinkException;
import seedu.zettel.exceptions.NotesAlreadyUnlinkedException;
import seedu.zettel.storage.Storage;

/**
 * Represents a command to remove a bidirectional link between two notes.
 * This command removes all links between the first and the second note,
 * updating both the first and second note's outgoing links and incoming links.
 */
public class UnlinkBothNotesCommand extends Command {
    private String noteId1;
    private String noteId2;

    /**
     * Constructs an UnlinkBothNotesCommand with the specified note IDs.
     *
     * @param noteId1 The ID of the first note.
     * @param noteId2 The ID of the second note.
     */
    public UnlinkBothNotesCommand(String noteId1, String noteId2) {
        this.noteId1 = noteId1; 
        this.noteId2 = noteId2;
    }

    /**
     * Executes the unlink command by removing all link between the first and second notes.
     * Performs multiple validations before unlinking:
     * 1. Checks if the notes list is empty
     * 2. Verifies both notes exist in the notes list
     * 3. Checks for self-unlink attempts (fail-fast)
     * 4. Verifies the link actually exists before attempting to remove it
     *
     * @param notes The list of all notes in the system.
     * @param tags The list of all tags in the system (unused in this command).
     * @param ui The UI object to display messages to the user.
     * @param storage The storage object to persist changes (unused in this command).
     * @throws NoNotesException If the notes list is empty.
     * @throws NoteSelfLinkException If attempting to unlink a note from itself.
     * @throws InvalidNoteIdException If either the source or target note does not exist.
     * @throws NotesAlreadyUnlinkedException If the link does not exist between the notes.
     */
    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) 
            throws NoNotesException, InvalidNoteIdException, InvalidInputException, NoteSelfLinkException,
            NotesAlreadyUnlinkedException {

        // Validation 1: Check if notes list is empty
        if (notes.isEmpty()) {
            throw new NoNotesException("You have no notes to unlink.");
        }

        // Validation 2: Try to find both notes
        Optional<Note> note1 = notes.stream()
                .filter(n -> n.getId().equals(noteId1))
                .findFirst();
        if (note1.isEmpty()) {
            throw new InvalidNoteIdException("Note with ID '"+ noteId1 + "' does not exist.");
        }

        Optional<Note> note2 = notes.stream()
                .filter(n -> n.getId().equals(noteId2))
                .findFirst();
        if (note2.isEmpty()) {
            throw new InvalidNoteIdException("Note with ID '"+ noteId2 + "' does not exist.");
        }

        // Validation 3: Check for self-unlink attempt (fail fast)
        if (noteId1.equals(noteId2)) {
            throw new NoteSelfLinkException("Cannot unlink a note from itself. Note ID: '" 
                    + noteId1 + "'.");
        }

        Note firstNote = note1.get();
        Note secondNote = note2.get();

        // Validation 4: Check if the notes are actually linked (in any direction)
        if (!firstNote.isLinkedTo(noteId2) && !firstNote.isLinkedBy(noteId2) 
                && !secondNote.isLinkedTo(noteId1) && !secondNote.isLinkedBy(noteId1)) {
            throw new NotesAlreadyUnlinkedException("Any link between note '" + noteId1
                    + "' and note '" + noteId2 + "' does not exist. Nothing to unlink.");
        }

        // Remove all possible link directions between the two notes
        if (firstNote.isLinkedTo(noteId2)) {
            firstNote.removeOutgoingLink(noteId2);
        }
        if (firstNote.isLinkedBy(noteId2)) {
            firstNote.removeIncomingLink(noteId2);
        }
        if (secondNote.isLinkedTo(noteId1)) {
            secondNote.removeOutgoingLink(noteId1);
        }
        if (secondNote.isLinkedBy(noteId1)) {
            secondNote.removeIncomingLink(noteId1);
        }

        ui.showSuccessfullyUnlinkedBothNotes(noteId1, noteId2);
    }
}
