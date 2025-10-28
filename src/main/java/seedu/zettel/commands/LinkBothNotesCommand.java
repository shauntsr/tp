package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.NoteSelfLinkException;
import seedu.zettel.exceptions.NotesAlreadyLinkedException;
import seedu.zettel.storage.Storage;

/**
 * Represents a command to create a bidirectional link between two notes.
 * This command creates links in both directions, meaning both notes will have
 * outgoing and incoming links to each other, allowing navigation in either direction.
 */
public class LinkBothNotesCommand extends Command {

    private final String sourceNoteId;
    private final String targetNoteId;

    /**
     * Constructs a LinkBothNotesCommand for linking two notes by their IDs in both directions.
     *
     * @param sourceNoteId The ID of the first note to link.
     * @param targetNoteId The ID of the second note to link.
     */
    public LinkBothNotesCommand(String sourceNoteId, String targetNoteId) {
        this.sourceNoteId = sourceNoteId;
        this.targetNoteId = targetNoteId;
    }

    /**
     * Executes the bidirectional link command by creating links between the two notes in both directions.
     * Performs multiple validations before linking:
     * 1. Checks if the notes list is empty
     * 2. Verifies both notes exist in the notes list
     * 3. Checks for self-link attempts
     * 4. Verifies a bidirectional link doesn't already exist
     * 
     * If a unidirectional link already exists, only the missing direction will be added.
     *
     * @param notes The list of all notes in the system.
     * @param tags The list of all tags in the system (unused in this command).
     * @param ui The UI object to display messages to the user.
     * @param storage The storage object to persist changes (unused in this command).
     * @throws NoNotesException If the notes list is empty.
     * @throws InvalidNoteIdException If either note does not exist.
     * @throws NoteSelfLinkException If attempting to link a note to itself.
     * @throws NotesAlreadyLinkedException If a bidirectional link already exists between the notes.
     */
    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) throws 
            NoNotesException, InvalidNoteIdException, NotesAlreadyLinkedException,
            NoteSelfLinkException {

        // If no notes at all in the list, throw an exception
        if (notes.isEmpty()) {
            throw new NoNotesException("You have no notes to link.");
        }

        // Try to find both notes
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

        // Check if attempting to link a note to itself
        if (sourceNoteId.equals(targetNoteId)) {
            throw new NoteSelfLinkException("Cannot link a note to itself. Note ID: '" + sourceNoteId + "'.");
        }
        
        // Check if two-way link already exists
        if (sourceNote.get().isLinkedTo(targetNoteId) && targetNote.get().isLinkedTo(sourceNoteId)) {
            throw new NotesAlreadyLinkedException("Note with ID '" + sourceNoteId
                    + "' already links in both directions to note with ID '" + targetNoteId + "'.");
        }
        

        // Create the two-way link
        // If a partial link already exists, only add the missing direction(s)
        // to prevent duplicates
        
        // Add links from source to target if not already present
        if (!sourceNote.get().isLinkedTo(targetNoteId)) {
            sourceNote.get().addOutgoingLink(targetNoteId);
        }
        if (!sourceNote.get().isLinkedBy(targetNoteId)) {
            sourceNote.get().addIncomingLink(targetNoteId);
        }

        // Add links from target to source if not already present
        if (!targetNote.get().isLinkedTo(sourceNoteId)) {
            targetNote.get().addOutgoingLink(sourceNoteId);
        }
        if (!targetNote.get().isLinkedBy(sourceNoteId)) {
            targetNote.get().addIncomingLink(sourceNoteId);
        }

        ui.showSuccessfulDoubleLinking(sourceNote.get().getTitle(), targetNote.get().getTitle());
    }

}
