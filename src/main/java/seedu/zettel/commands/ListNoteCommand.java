package seedu.zettel.commands;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command to list notes in the current repository.
 * <p>
 * Notes are sorted by creation time in descending order (most recent first).
 * Can optionally filter to display only pinned notes and/or only archived notes.
 * Archived notes are NOT listed by default.
 */
public class ListNoteCommand extends Command {
    private final boolean showsPinnedOnly;
    private final boolean showsArchivedOnly;

    /**
     * Constructs a ListNoteCommand.
     *
     * @param showsPinnedOnly  If true, only pinned notes will be listed.
     * @param showsArchivedOnly If true, only archived notes will be listed;
     *                          if false, only non-archived notes will be listed.
     */
    public ListNoteCommand(boolean showsPinnedOnly, boolean showsArchivedOnly) {
        this.showsPinnedOnly = showsPinnedOnly;
        this.showsArchivedOnly = showsArchivedOnly;
    }

    /**
     * Executes the command to display notes.
     *
     * @param notes   The list of all notes
     * @param tags    The list of current tags.
     * @param ui      The UI instance for user interaction
     * @param storage The storage instance (not used)
     * @throws ZettelException If an error occurs during command execution
     */
    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) throws ZettelException {
        // sort newest first
        notes.sort(Comparator.comparing(Note::getCreatedAt).reversed());

        // filter by archived state (either archived-only or non-archived-only),
        // and by pinned state if requested.
        List<Note> filtered = notes.stream()
                .filter(n -> showsArchivedOnly == n.isArchived())
                .filter(n -> !showsPinnedOnly || n.isPinned())
                .collect(Collectors.toList());

        if (filtered.isEmpty() && showsPinnedOnly) {
            String scope = showsArchivedOnly ? "pinned archived notes" : "pinned notes";
            throw new NoNotesException("No " + scope + " found. Pin a note to add to this list.");
        }

        // No notes at all in the requested archive scope
        if (filtered.isEmpty()) {
            if (showsArchivedOnly) {
                throw new NoNotesException("No archived notes found.");
            } else {
                throw new NoNotesException("No notes found.");
            }
        }

        ui.showNoteList(filtered, showsPinnedOnly, showsArchivedOnly);
    }
}
