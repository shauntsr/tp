package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.Optional;
import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;
import seedu.zettel.exceptions.NoNoteException;
import seedu.zettel.exceptions.ZettelException;

public class PinNoteCommand extends Command {
    private final boolean isPin;
    private final String noteId;

    public PinNoteCommand(String noteId, boolean isPin) {
        this.noteId = noteId;
        this.isPin = isPin;
    }

    @Override
    public void execute(ArrayList<Note> notes, UI ui, Storage storage) throws ZettelException {
        Optional<Note> maybe = notes.stream().filter(n -> n.getId().equals(noteId)).findFirst();
        if (maybe.isEmpty()) {
            throw new NoNoteException("No note found with id " + noteId);
        }
        Note note = maybe.get();
        note.setPinned(isPin);
        // Update modified timestamp when pin state changes
        note.touchModified();
        ui.showJustPinnedNote(note, noteId);
        storage.save(notes);
    }
}
