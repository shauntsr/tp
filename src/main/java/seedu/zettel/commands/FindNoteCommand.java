package seedu.zettel.commands;

import java.util.ArrayList;

import seedu.zettel.Note;
import seedu.zettel.storage.Storage;
import seedu.zettel.UI;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.ZettelException;

/**
 * Command to find notes containing a given keyword in their body.
 * Performs a case-insensitive search across all existing notes.
 */
public class FindNoteCommand extends Command{
    private final String keyword;

    /**
     * Constructs a FindNoteCommand with the specified search keyword.
     *
     * @param keyword The keyword to search for in note body.
     */
    public FindNoteCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Executes the find command to search for notes containing the specified keyword.
     * The search is case-insensitive and matches any note whose body includes the keyword.
     * Displays the matching notes or a "no results" message if none are found.
     *
     * @param notes   The list of existing notes
     * @param ui      The UI instance for user interaction
     * @param storage The storage instance for persistence
     * @throws ZettelException If the notes list is empty
     */
    @Override
    public void execute(ArrayList<Note> notes, UI ui, Storage storage) throws ZettelException {
        if (notes.isEmpty()) {
            throw new NoNotesException("There are no notes available to search.");
        }
        assert notes != null : "Notes list should not be null";
        ArrayList<Note> matchedNotes = new ArrayList<>();
        for (Note note: notes) {
            if (note.getBody().toLowerCase().contains(keyword.toLowerCase())) {
                matchedNotes.add(note);
            }
        }
        if (matchedNotes.isEmpty()) {
            ui.showNoNotesFoundMessage();
        } else {
            ui.showFoundNotesMessage(matchedNotes);
        }
    }
}
