package seedu.zettel.commands;

import seedu.duke.exceptions.ZettelException;
import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListNoteCommand extends Command{
    private final boolean showsPinnedOnly;

    public ListNoteCommand(boolean showsPinnedOnly) {
        this.showsPinnedOnly = showsPinnedOnly;
    }

    @Override
    public void execute(ArrayList<Note> notes, UI ui, Storage storage) throws ZettelException{
        // Add notes
        notes.sort(Comparator.comparing(Note::getCreatedAt).reversed());

        List<Note> filtered = notes.stream()
                .filter(n -> !showsPinnedOnly || n.isPinned())
                .toList();

        // Handle no notes found
        if (filtered.isEmpty() && showsPinnedOnly) {
            ui.showNoPinnedNotes();
            return;
        }

        if (filtered.isEmpty()) {
            ui.showNoNotes();
            return;
        }

        ui.showNoteList(filtered, showsPinnedOnly);
    }
}
