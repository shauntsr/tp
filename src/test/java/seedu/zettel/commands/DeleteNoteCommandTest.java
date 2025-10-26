
package seedu.zettel.commands;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.zettel.Note;
import seedu.zettel.storage.Storage;
import seedu.zettel.UI;
import seedu.zettel.exceptions.NoNoteFoundException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.ZettelException;

public class DeleteNoteCommandTest {

    private static final String FILE_PATH = "./data/zettel.txt";

    // Simple UI test double that records calls
    private static class TestUI extends UI {
        private final ArrayList<String> events = new ArrayList<>();

        public void showDeleteNotFound(String id) {
            events.add("notFound:" + id);
        }
        
        @Override
        public void showDeleteConfirmation(String noteId, String title) {
            events.add("confirm:" + noteId + ":" + title);
        }

        @Override
        public void showNoteDeleted(String id) {
            events.add("noteDeleted:" + id);
        }

        @Override
        public void showDeletionCancelled() {
            events.add("cancelled");
        }

        public boolean containsEvent(String e) {
            return events.stream().anyMatch(s -> s.equals(e));
        }
    }

    @BeforeEach
    public void setUp() {
        // nothing to set up globally
    }

    @AfterEach
    public void tearDown() {
        // restore System.in in case a test changed it
        System.setIn(System.in);
    }

    
    @Test
    public void testEmptyNotesListThrowsException() {
        ArrayList<Note> notes = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        TestUI ui = new TestUI();
        Storage storage = new Storage(FILE_PATH) {
            @Override
            public void save(List<Note> notesToSave) {
                // no-op for tests
            }
        };

        DeleteNoteCommand cmd = new DeleteNoteCommand("12345678", true);
        assertThrows(NoNotesException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });
    }
    
    @Test
    public void testNoteIdNotFoundException() {
        // Create a list with some notes
        ArrayList<Note> notes = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        notes.add(new Note("12345678", "Note One", "note1.txt", "Body 1", Instant.now(), Instant.now()));
        notes.add(new Note("abcdefgh", "Note Two", "note2.txt", "Body 2", Instant.now(), Instant.now()));

        TestUI ui = new TestUI();
        Storage storage = new Storage(FILE_PATH) {
            @Override
            public void save(List<Note> notesToSave) {
                // no-op
            }
        };

        // Try to delete a note with a valid format ID (8 alphanumeric chars) that doesn't exist
        DeleteNoteCommand cmd = new DeleteNoteCommand("99999999", true);

        // Should throw NoNoteFoundException because the noteId format is valid but the note doesn't exist
        assertThrows(NoNoteFoundException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        // Verify that notes list is unchanged (nothing was deleted)
        assertEquals(2, notes.size());
    }

    
    @Test // test deleting an existing note with force
    public void testDeleteNoteWithForce() throws ZettelException {
        ArrayList<Note> notes = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        notes.add(new Note("12345678", "Note One", "note1.txt", "Body 1", Instant.now(), Instant.now()));
        notes.add(new Note("abcdefgh", "Note Two", "note2.txt", "Body 2", Instant.now(), Instant.now()));

        TestUI ui = new TestUI();
        Storage storage = new Storage(FILE_PATH) {
            @Override
            public void save(List<Note> notesToSave) {
                // no-op for tests
            }
        };

        DeleteNoteCommand cmd = new DeleteNoteCommand("12345678", true);
        cmd.execute(notes, tags, ui, storage);

        assertTrue(ui.containsEvent("noteDeleted:12345678"), "expected UI to record noteDeleted:12345678");
        assertEquals(1, notes.size());
    }

    @Test // test deleting a note without force, user confirms
    public void testDeleteNoteUserConfirms() throws ZettelException {
        String userInput = "y\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        ArrayList<Note> notes = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        notes.add(new Note("12345678", "Note One", "note1.txt", "Body 1", Instant.now(), Instant.now()));
        notes.add(new Note("abcdefgh", "Note Two", "note2.txt", "Body 2", Instant.now(), Instant.now()));

        TestUI ui = new TestUI();
        Storage storage = new Storage(FILE_PATH) {
            @Override
            public void save(List<Note> notesToSave) {
                // no-op for tests
            }
        };

        DeleteNoteCommand cmd = new DeleteNoteCommand("abcdefgh", false);
        cmd.execute(notes, tags, ui, storage);

        assertTrue(ui.containsEvent("noteDeleted:abcdefgh"), "expected UI to record noteDeleted:abcdefgh");
        assertEquals(1, notes.size());
    }

    @Test // test deleting a note without force, user cancels
    public void testDeleteNoteUserCancels() throws ZettelException {
        String userInput = "n\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        ArrayList<Note> notes = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        notes.add(new Note("12345678", "Note One", "note1.txt", "Body 1", Instant.now(), Instant.now()));
        notes.add(new Note("abcdefgh", "Note Two", "note2.txt", "Body 2", Instant.now(), Instant.now()));

        TestUI ui = new TestUI();
        Storage storage = new Storage(FILE_PATH) {
            @Override
            public void save(List<Note> notesToSave) {
                // no-op for tests
            }
        };

        DeleteNoteCommand cmd = new DeleteNoteCommand("12345678", false);
        cmd.execute(notes, tags, ui, storage);

        assertTrue(ui.containsEvent("cancelled"), "expected UI to record cancelled");
        assertEquals(2, notes.size());
    }
    
    @Test // test deleting a note without force, user types "yes"
    public void testDeleteNoteUserTypesYesNoteIsDeleted() throws ZettelException {
        String userInput = "yes\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        ArrayList<Note> notes = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        notes.add(new Note("12345678", "Note One", "note1.txt", "Body 1", Instant.now(), Instant.now()));

        TestUI ui = new TestUI();
        Storage storage = new Storage(FILE_PATH) {
            @Override
            public void save(List<Note> notesToSave) {
                // no-op for tests
            }
        };

        DeleteNoteCommand cmd = new DeleteNoteCommand("12345678", false);
        cmd.execute(notes, tags, ui, storage);

        assertTrue(ui.containsEvent("noteDeleted:12345678"), "expected UI to record noteDeleted:12345678");
        assertEquals(0, notes.size());
    }
}
