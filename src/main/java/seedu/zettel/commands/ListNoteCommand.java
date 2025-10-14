package seedu.zettel.commands;

import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListNoteCommand extends Command{
    public ListNoteCommand(boolean showsPinnedOnly) {
        this.showsPinnedOnly = showsPinnedOnly;
    }

    private final boolean showsPinnedOnly;

    @Override
    public void execute(ArrayList<Note> notes, UI ui, Storage storage) {
        notes.sort(Comparator.comparing(Note::getCreatedAt).reversed());

        List<Note> filtered = notes.stream()
                .filter(n -> !showsPinnedOnly || n.isPinned())
                .toList();

        // Handle no notes found
        if (filtered.isEmpty() && showsPinnedOnly) {
           System.out.println("No pinned notes found. Pin a note to add to this list.\n");
           return;
        }

        if (filtered.isEmpty()) {
            System.out.println("No notes found.\n");
            return;
        }

        // Show number of notes found.
        if (showsPinnedOnly) {
            System.out.println("You have " + filtered.size() + " pinned notes:\n");
        } else {
            System.out.println("You have " + filtered.size() + " notes:\n");
        }

        for (int idx = 0; idx < filtered.size(); idx++) {
            System.out.println("    " + (idx + 1) + ". " + filtered.get(idx));
        }
    }
}
