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


public class LinkNotesCommand extends Command {

    private final String sourceNoteId;
    private final String targetNoteId;

    /**
     * Constructs a LinkNotesCommand for linking two notes by their IDs.
     *
     * @param sourceNoteId The ID of the source note that creates the link.
     * @param targetNoteId The ID of the target note that receives the link.
     */

    public LinkNotesCommand(String sourceNoteId, String targetNoteId) {
        this.sourceNoteId = sourceNoteId;
        this.targetNoteId = targetNoteId;
    }

    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) throws 
            NoNotesException, InvalidNoteIdException, NotesAlreadyLinkedException,
            NoteSelfLinkException {

        // If no notes at all in the list, throw an exception
        if (notes.isEmpty()) {
            throw new NoNotesException("You have no notes to link.");
        }

        // Check if attempting to link a note to itself
        if (sourceNoteId.equals(targetNoteId)) {
            throw new NoteSelfLinkException("Cannot link a note to itself. Note ID: '" + sourceNoteId + "'.");
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
        
        // Check if link already exists
        if (sourceNote.get().isLinkedTo(targetNoteId)) {
            throw new NotesAlreadyLinkedException("Note with ID '" + sourceNoteId
                    + "' already links to note with ID '" + targetNoteId + "'.");
        }

        // Create unidirectional link
        sourceNote.get().addIncomingLink(targetNoteId);
        targetNote.get().addOutgoingLink(sourceNoteId);
        ui.showSuccessfulLinking(sourceNote.get().getTitle(), targetNote.get().getTitle());
    }

}
