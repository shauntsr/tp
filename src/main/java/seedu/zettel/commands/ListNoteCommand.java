package seedu.zettel.commands;

import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.Note;
import seedu.zettel.storage.Storage;
import seedu.zettel.UI;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Command to list notes in the current repository.
 * <p>
 * Notes are sorted by creation time in descending order (most recent first).
 * Can optionally filter to display only pinned notes.
 * </p>
 */
public class ListNoteCommand extends Command{
    private final boolean showsPinnedOnly;

    /**
     * Constructs a ListNoteCommand.
     *
     * @param showsPinnedOnly If true, only pinned notes will be listed;
     *                        if false, all notes are listed.
     */
    public ListNoteCommand(boolean showsPinnedOnly) {
        this.showsPinnedOnly = showsPinnedOnly;
    }

    /**
     * Executes the command to display notes.
     * <p>
     * The notes are first sorted by creation time (newest first),
     * then filtered based on the showsPinnedOnly flag.
     * If no notes are found, appropriate UI messages are displayed.
     *
     * @param notes   The list of all notes
     * @param tags    The list of current tags.
     * @param ui      The UI instance for user interaction
     * @param storage The storage instance (not used)
     * @throws ZettelException If an error occurs during command execution
     */
    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) throws ZettelException{
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
