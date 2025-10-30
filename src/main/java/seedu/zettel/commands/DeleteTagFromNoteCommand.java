package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidInputException;
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.TagNotFoundException;
import seedu.zettel.storage.Storage;

/**
 * Represents a command to delete a tag from a specific note.
 * This command removes a tag from the note's tag list.
 */
public class DeleteTagFromNoteCommand extends Command {
    private final String noteId;
    private final String tag;
    private final boolean isForce;

    /**
     * Constructs a DeleteTagFromNoteCommand with the specified note ID and tag.
     *
     * @param noteId The ID of the note.
     * @param tag The tag to be deleted.
     */
    public DeleteTagFromNoteCommand(String noteId, String tag, boolean isForce) {
        this.noteId = noteId;
        this.tag = tag;
        this.isForce = isForce;
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

        boolean shouldDelete = isForce;
        if (!isForce) {
            ui.showDeleteTagFromNoteConfirmation(tag, noteId);

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().trim().toLowerCase();
            shouldDelete = input.equals("y") || input.equals("yes");
        }

        if (shouldDelete) {
            note.get().removeTag(tag);
            ui.showSuccessfullyDeletedTagFromNote(noteId, tag);
        } else {
            ui.showDeletionCancelled();
        }
    }
}
