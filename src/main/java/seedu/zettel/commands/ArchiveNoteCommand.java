package seedu.zettel.commands;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidInputException;
import seedu.zettel.exceptions.NoNoteFoundException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

import java.util.ArrayList;
import java.util.List;

/**
 * Command to archive or unarchive a note.
 * Archiving moves the note file to the archive folder and marks it as archived.
 * Unarchiving moves it back to the notes folder and marks it as active.
 */
public class ArchiveNoteCommand extends Command {
    private final String noteId;
    private final boolean shouldArchive;

    /**
     * Constructs an ArchiveNoteCommand with the specified note ID and archive action.
     *
     * @param noteId        The ID of the note to archive/unarchive
     * @param shouldArchive True to archive, false to unarchive
     */
    public ArchiveNoteCommand(String noteId, boolean shouldArchive) {
        this.noteId = noteId;
        this.shouldArchive = shouldArchive;
    }

    /**
     * Executes the command to archive or unarchive a note.
     * Moves the physical file between notes/ and archive/ folders
     * and updates the note's archived status.
     *
     * @param notes   The list of existing notes
     * @param tags    The list of current tags
     * @param ui      The UI instance for user interaction
     * @param storage The storage instance for persistence
     * @throws ZettelException If the note is not found or file operations fail
     */
    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage)
            throws ZettelException {
        assert noteId != null : "Note ID should not be null";

        // Find the note with matching ID
        Note targetNote = notes.stream()
                .filter(n -> n.getId().equals(noteId))
                .findFirst()
                .orElseThrow(() -> new NoNoteFoundException("Note with ID '" + noteId + "' not found."));

        // Check if note is already in desired state
        if (shouldArchive && targetNote.isArchived()) {
            throw new InvalidInputException("Note is already archived.");
        }
        if (!shouldArchive && !targetNote.isArchived()) {
            throw new InvalidInputException("Note is not archived.");
        }

        String filename = targetNote.getFilename();

        // Move the physical file using Storage
        storage.moveNoteBetweenDirectories(filename, shouldArchive);

        // Update note metadata
        targetNote.setArchived(shouldArchive);
        targetNote.setArchiveName(shouldArchive ? storage.getArchiveFolderName() : null);

        // Save changes and show feedback
        storage.save(notes);
        if (shouldArchive) {
            ui.showArchivedNote(targetNote);
        } else {
            ui.showUnarchivedNote(targetNote);
        }
    }

}
