package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidInputException;
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.NoTagsException;
import seedu.zettel.storage.Storage;

public class ListTagsSingleNoteCommand extends Command {
    private String noteId;

    public ListTagsSingleNoteCommand(String noteId) {
        this.noteId = noteId;
    }

    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) 
            throws NoNotesException, InvalidInputException, InvalidNoteIdException, NoTagsException {
        
        // Validation 1: Check if notes list is empty
        if (notes.isEmpty()) {
            throw new NoNotesException("You have no notes to list tags of.");
        }

        // Validation 2: Check if note with the given ID exists
        Optional<Note> maybe = notes.stream().filter(n -> n.getId().equals(noteId)).findFirst();
        if (maybe.isEmpty()) {
            throw new InvalidNoteIdException("Note with ID '" + noteId + "' does not exist.");
        }

        // Validation 3: Check if tag list is empty for that single note
        if (maybe.get().getTags().isEmpty()) {
            throw new NoTagsException("No tags are tagged to note with ID '" + noteId + "'.");
        }
        // Show the tags for the single note

        ui.showTagsSingleNote(maybe.get().getTags(), noteId);
    }
}
