package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidInputException;
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.storage.Storage;

public class ListLinkedNotesCommand extends Command {
    private static final String LIST_INCOMING = "incoming";
    private static final String LIST_OUTGOING = "outgoing";
    private HashSet<String> outgoingLinks;
    private HashSet<String> incomingLinks;
    private String listToShow; // "incoming" or "outgoing" 
    private String noteId;

    public ListLinkedNotesCommand(String listToShow, String noteId) {
        this.listToShow = listToShow;
        this.noteId = noteId;
    }

    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) 
            throws NoNotesException, InvalidInputException, InvalidNoteIdException {
        
        // Validation 1: Check if notes list is empty
        if (notes.isEmpty()) {
            throw new NoNotesException("You have no notes to list links from.");
        }
        
        // Validation 2: Validate listToShow parameter
        if (!listToShow.equals(LIST_INCOMING) && !listToShow.equals(LIST_OUTGOING)) {
            throw new InvalidInputException("Invalid list type: '" + listToShow 
                    + "'. Must be 'incoming' or 'outgoing'.");
        }
        
        // Validation 3: Check if note with the given ID exists
        Optional<Note> maybe = notes.stream().filter(n -> n.getId().equals(noteId)).findFirst();
        if (maybe.isEmpty()) {
            throw new InvalidNoteIdException("Note with ID '" + noteId + "' does not exist.");
        }
        Note note = maybe.get();

        // get the links from the note
        outgoingLinks = note.getOutgoingLinks();
        incomingLinks = note.getIncomingLinks();
        
        // Validation 4: Check if the note has any links of the requested type
        if (listToShow.equals(LIST_INCOMING) && incomingLinks.isEmpty()) {
            throw new NoNotesException("Note with ID '" + noteId + "' has no incoming links.");
        } else if (listToShow.equals(LIST_OUTGOING) && outgoingLinks.isEmpty()) {
            throw new NoNotesException("Note with ID '" + noteId + "' has no outgoing links.");
        }
        
        // at this point, we know we have links to show
        // so we can proceed to show them in UI

        ArrayList<Note> linkedNotes = new ArrayList<>();
        for (Note n : notes) {
            if (listToShow.equals(LIST_OUTGOING) && outgoingLinks.contains(n.getId())) {
                linkedNotes.add(n);
            } else if (listToShow.equals(LIST_INCOMING) && incomingLinks.contains(n.getId())) {
                linkedNotes.add(n);
            }
        }
        
        // Edge case: Check if we found any actual notes (in case of orphaned/broken links)
        if (linkedNotes.isEmpty()) {
            throw new NoNotesException("Note with ID '" + noteId + "' has " + listToShow 
                    + " links, but the linked notes no longer exist.");
        }
        
        ui.showLinkedNotes(linkedNotes, noteId, listToShow);
    }
}
