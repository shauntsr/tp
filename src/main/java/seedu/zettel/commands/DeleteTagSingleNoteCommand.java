package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidInputException;
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.TagNotFoundException;
import seedu.zettel.storage.Storage;

/**
 * Represents a command to remove a bidirectional link between two notes.
 * This command removes all links between the first and the second note,
 * updating both the first and second note's outgoing links and incoming links.
 */
public class DeleteTagSingleNoteCommand extends Command {
    private String noteId;
    private String tag;

    /**
     * Constructs a DeleteTagSingleNoteCommand with the specified note ID and tag.
     *
     * @param noteId The ID of the note.
     * @param tag The tag to be deleted.
     */
    public DeleteTagSingleNoteCommand(String noteId, String tag) {
        this.noteId = noteId;
        this.tag = tag;
    }

    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) 
            throws NoNotesException, InvalidNoteIdException, InvalidInputException, TagNotFoundException {

        // Validation 1: Check if notes list is empty
        if (notes.isEmpty()) {
            throw new NoNotesException("You have no notes to delete tags from.");
        }

        // Validation 2: Try to find the note
        Optional<Note> note = notes.stream()
                .filter(n -> n.getId().equals(noteId))
                .findFirst();
        if (note.isEmpty()) {
            throw new InvalidNoteIdException("Note with ID '"+ noteId + "' does not exist.");
        }

        // Validation 3: Check if the tag exists for that single note
        if (!note.get().getTags().contains(tag)) {
            throw new TagNotFoundException("Tag '" + tag + "' does not exist for note with ID '" + noteId + "'.");
        }

        ui.showSuccessfullyDeletedTagSingleNote(noteId, tag);
    }
}
