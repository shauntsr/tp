package seedu.zettel.commands;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import seedu.zettel.Note;
import seedu.zettel.storage.Storage;
import seedu.zettel.UI;
import seedu.zettel.exceptions.AlreadyPinnedException;
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.NoNotesException;

public class PinNoteCommandTest {

    // ==================== Exception Tests ====================

    @Test
    public void testEmptyNotesListException() {
        ArrayList<Note> notes = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        UI ui = new UI();
        Storage storage = new Storage("build/testdata/pinnote-test.txt");

        // Test with empty notes list
        PinNoteCommand command = new PinNoteCommand("abcd1234", true);
        assertThrows(NoNotesException.class, () -> {
            command.execute(notes, tags, ui, storage);
        });
    }

    @Test
    public void testNoteIdNotFoundException() {
        ArrayList<Note> notes = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        notes.add(new Note("abcd1234", "Title 0", "file0.txt", "Body 0", Instant.now(), Instant.now()));
        notes.add(new Note("1234abcd", "Title 1", "file1.txt", "Body 1", Instant.now(), Instant.now()));
        UI ui = new UI();
        Storage storage = new Storage("build/testdata/pinnote-test.txt");

        // Test with valid format but non-existent ID
        PinNoteCommand command = new PinNoteCommand("99999999", true);
        assertThrows(InvalidNoteIdException.class, () -> {
            command.execute(notes, tags, ui, storage);
        });
    }

    // ==================== Happy Path Tests ====================

    @Test
    public void testValidPinNoteCommandNoteIsPinned() throws Exception {
        ArrayList<Note> notes = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        notes.add(new Note("abcd1234", "Title 0", "file0.txt", "Body 0", Instant.now(), Instant.now()));
        notes.add(new Note("1234abcd", "Title 1", "file1.txt", "Body 1", Instant.now(), Instant.now()));
        Note target = notes.get(1);
        assertFalse(target.isPinned());

        Instant before = target.getModifiedAt();
        // Ensure clock advances on platforms with coarse Instant resolution
        Thread.sleep(2);
        UI ui = new UI();
        Storage storage = new Storage("build/testdata/pinnote-test.txt");

        new PinNoteCommand("1234abcd", true).execute(notes, tags, ui, storage);

        assertTrue(target.isPinned());
        assertTrue(target.getModifiedAt().isAfter(before), "modifiedAt should be updated on pinning");
    }

    @Test
    void testUnpinsNoteAtValidIndexUpdatesPinnedAndModifiedAt() throws Exception {
        ArrayList<Note> notes = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        notes.add(new Note("abcd1234", "Title0", "file0.txt", "Body0",
                Instant.now(), Instant.now()));
        Note target = notes.get(0);

        target.setPinned(true);
        assertTrue(target.isPinned());
        Instant before = target.getModifiedAt();
        // Ensure clock advances on platforms with coarse Instant resolution
        Thread.sleep(2);
        UI ui = new UI();
        Storage storage = new Storage("build/testdata/pinnote-test.txt");

        new PinNoteCommand("abcd1234", false).execute(notes, tags, ui, storage);

        assertFalse(target.isPinned(), "Note should be unpinned");
        assertTrue(target.getModifiedAt().isAfter(before), "modifiedAt should be updated on unpin");
    }

    @Test
    public void testPinAlreadyPinnedNoteThrowsException() throws Exception {
        ArrayList<Note> notes = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        notes.add(new Note("abcd1234", "Title", "file.txt", "Body", Instant.now(), Instant.now()));
        Note target = notes.get(0);

        // First, pin the note
        target.setPinned(true);
        assertTrue(target.isPinned());

        UI ui = new UI();
        Storage storage = new Storage("build/testdata/pinnote-test.txt");

        // Try to pin it again - should throw AlreadyPinnedException
        PinNoteCommand command = new PinNoteCommand("abcd1234", true);
        assertThrows(AlreadyPinnedException.class, () -> {
            command.execute(notes, tags, ui, storage);
        });

        // Note should still be pinned
        assertTrue(target.isPinned(), "Note should still be pinned after exception");
    }

    @Test
    public void testUnpinAlreadyUnpinnedNoteThrowsException() throws Exception {
        ArrayList<Note> notes = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        notes.add(new Note("abcd1234", "Title", "file.txt", "Body", Instant.now(), Instant.now()));
        Note target = notes.get(0);

        // Note starts unpinned by default
        assertFalse(target.isPinned());

        UI ui = new UI();
        Storage storage = new Storage("build/testdata/pinnote-test.txt");

        // Try to unpin an already unpinned note - should throw AlreadyPinnedException
        PinNoteCommand command = new PinNoteCommand("abcd1234", false);
        assertThrows(AlreadyPinnedException.class, () -> {
            command.execute(notes, tags, ui, storage);
        });

        // Note should still be unpinned
        assertFalse(target.isPinned(), "Note should still be unpinned after exception");
    }
}
