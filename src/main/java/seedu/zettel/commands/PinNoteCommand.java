package seedu.zettel.commands;

import java.util.ArrayList;

import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;

public class PinNoteCommand extends Command {
    private final boolean isPin;
    private final String noteId;

    public PinNoteCommand(String noteId, boolean isPin) {
        this.noteId = noteId;
        this.isPin = isPin;
    }

    @Override
    public void execute(ArrayList<Note> notes, UI ui, Storage storage) {
        int idx = Integer.parseInt(noteId);
        if (idx < 0 || idx >= notes.size()) {
            throw new IndexOutOfBoundsException("Note with ID " + idx + " does not exist.");
        }
        Note note = notes.get(Integer.parseInt(noteId));
        note.setPinned(isPin);
        // Update modified timestamp when pin state changes
        note.touchModified();
        ui.showJustPinnedNote(note, idx);
        storage.save(notes);
    }
}
