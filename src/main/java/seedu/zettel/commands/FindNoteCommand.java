package seedu.zettel.commands;

import java.util.ArrayList;

import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;
import seedu.zettel.exceptions.ZettelException;

public class FindNoteCommand extends Command{
    private final String keyword;

    public FindNoteCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(ArrayList<Note> notes, UI ui, Storage storage) throws ZettelException {
        if (notes.isEmpty()) {
            throw new ZettelException("There are no notes available to search.");
        }
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
