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

    private final String noteId1;
    private final String noteId2;

    /**
     * Constructs a LinkBothNotesCommand for linking two notes by their IDs in both directions.
     *
     * @param noteId1 The ID of the first note to link.
     * @param noteId2 The ID of the second note to link.
     */
    public LinkBothNotesCommand(String noteId1, String noteId2) {
        this.noteId1 = noteId1;
        this.noteId2 = noteId2;
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

        // Check if attempting to link a note to itself
        if (noteId1.equals(noteId2)) {
            throw new NoteSelfLinkException("Cannot link a note to itself. Note ID: '" + noteId1 + "'.");
        }
        
        // Check if two-way link already exists
        if (note1.get().isLinkedTo(noteId2) && note2.get().isLinkedTo(noteId1)) {
            throw new NotesAlreadyLinkedException("Note with ID '" + noteId1
                    + "' already links in both directions to note with ID '" + noteId2 + "'.");
        }
        

        // Create the two-way link
        // If a partial link already exists, only add the missing direction(s)
        // to prevent duplicates

        // Add links from note1 to note2 if not already present
        if (!note1.get().isLinkedTo(noteId2)) {
            note1.get().addOutgoingLink(noteId2);
        }
        if (!note1.get().isLinkedBy(noteId2)) {
            note1.get().addIncomingLink(noteId2);
        }

        // Add links from note2 to note1 if not already present
        if (!note2.get().isLinkedTo(noteId1)) {
            note2.get().addOutgoingLink(noteId1);
        }
        if (!note2.get().isLinkedBy(noteId1)) {
            note2.get().addIncomingLink(noteId1);
        }

        ui.showSuccessfullyDoubleLinkedNotes(note1.get().getTitle(), note2.get().getTitle());
    }

}
