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
 * Represents a command to remove a unidirectional link between two notes.
 * This command removes the link from the source note to the target note,
 * updating both the source note's outgoing links and the target note's incoming links.
 */
public class UnlinkNotesCommand extends Command {
    private String sourceNoteId;
    private String targetNoteId;

    /**
     * Constructs an UnlinkNotesCommand with the specified source and target note IDs.
     *
     * @param sourceNoteId The ID of the note from which the link originates.
     * @param targetNoteId The ID of the note to which the link points.
     */
    public UnlinkNotesCommand(String sourceNoteId, String targetNoteId) {
        this.sourceNoteId = sourceNoteId;
        this.targetNoteId = targetNoteId;
    }

    /**
     * Executes the unlink command by removing the link between the source and target notes.
     * Performs multiple validations before unlinking:
     * 1. Checks if the notes list is empty
     * 2. Checks for self-unlink attempts (fail-fast)
     * 3. Verifies both notes exist in the notes list
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
        Optional<Note> sourceNote = notes.stream()
                .filter(n -> n.getId().equals(sourceNoteId))
                .findFirst();
        if (sourceNote.isEmpty()) {
            throw new InvalidNoteIdException("Note with ID '"+ sourceNoteId + "' does not exist.");
        }

        Optional<Note> targetNote = notes.stream()
                .filter(n -> n.getId().equals(targetNoteId))
                .findFirst();
        if (targetNote.isEmpty()) {
            throw new InvalidNoteIdException("Note with ID '"+ targetNoteId + "' does not exist.");
        }

        // Validation 3: Check for self-unlink attempt (fail fast)
        if (sourceNoteId.equals(targetNoteId)) {
            throw new NoteSelfLinkException("Cannot unlink a note from itself. Note ID: '" 
                    + sourceNoteId + "'.");
        }

        Note srcNote = sourceNote.get();
        Note tgtNote = targetNote.get();

        // Validation 4: Check if the notes are actually linked
        if (!srcNote.isLinkedTo(targetNoteId)) {
            throw new NotesAlreadyUnlinkedException("Link from note '" + sourceNoteId 
                    + "' to note '" + targetNoteId + "' does not exist. Nothing to unlink.");
        }

        // Unlink the notes - use the Note class methods to modify the actual sets
        srcNote.removeOutgoingLink(targetNoteId);
        tgtNote.removeIncomingLink(sourceNoteId);

        ui.showSuccessfullyUnlinkedNotes(sourceNoteId, targetNoteId);
    }  
}
