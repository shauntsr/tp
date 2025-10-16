package seedu.zettel.commands;

import seedu.zettel.exceptions.InvalidInputException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

public class NewNoteCommand extends Command {
    private final String title;
    private final String body;

    public NewNoteCommand(String title, String body) {
        this.title = title;
        this.body = body;
    }

    @Override
    public void execute(ArrayList<Note> notes, UI ui, Storage storage) throws ZettelException {
        assert title != null;

        String id = UUID.randomUUID().toString().substring(0,8);
        String filename = title.replaceAll("\\s+", "_") + ".txt";

        // Check if filename already exists
        boolean filenameExists = notes.stream()
                .anyMatch(n-> n.getFilename().equals(filename));

        if (filenameExists) {
            throw new InvalidInputException("Note already exists!");
        }

        Instant now = Instant.now();

        Note newNote = new Note(
                id,
                title,
                filename,
                body,
                now,
                now
        );

        notes.add(newNote);
        storage.save(notes);
        ui.showAddedNote(newNote);
    }
}
