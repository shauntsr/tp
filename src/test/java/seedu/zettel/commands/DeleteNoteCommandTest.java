
package seedu.zettel.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;
import seedu.zettel.exceptions.NoNoteException;
import seedu.zettel.exceptions.ZettelException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeleteNoteCommandTest {

    private static final String FILE_PATH = "./data/zettel.txt";

    // Simple UI test double that records calls
    private static class TestUI extends UI {
        private final ArrayList<String> events = new ArrayList<>();

        public void showDeleteNotFound(String id) {
            events.add("notFound:" + id);
        }

        @Override
        public void showDeleteConfirmation(String message) {
            events.add("confirm:" + message);
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

    @Test // test deleting an existing note with force
    public void testDeleteNoteWithForce() throws ZettelException {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("1", "Note One", "note1.txt", "Body 1", Instant.now(), Instant.now()));
        notes.add(new Note("2", "Note Two", "note2.txt", "Body 2", Instant.now(), Instant.now()));

        TestUI ui = new TestUI();
        Storage storage = new Storage(FILE_PATH) {
            @Override
            public void save(List<Note> notesToSave) {
                // no-op for tests
            }
        };

        DeleteNoteCommand cmd = new DeleteNoteCommand("1", true);
        cmd.execute(notes, ui, storage);

        assertTrue(ui.containsEvent("noteDeleted:1"), "expected UI to record noteDeleted:1");
        assertEquals(1, notes.size());
    }

    @Test // test deleting a non-existing note (should throw NoNoteException)
    public void testDeleteNonExistingNote() {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("1", "Note One", "note1.txt", "Body 1", Instant.now(), Instant.now()));

        TestUI ui = new TestUI();
        Storage storage = new Storage(FILE_PATH) {
            @Override
            public void save(List<Note> notesToSave) {
                // no-op
            }
        };

        DeleteNoteCommand cmd = new DeleteNoteCommand("3", true);

        NoNoteException ex = assertThrows(NoNoteException.class, () -> {
            cmd.execute(notes, ui, storage);
        });

        // UI should have been informed before the exception
        assertTrue(ui.containsEvent("notFound:3"), "expected UI to record notFound:3");
        assertEquals(1, notes.size());
    }

    @Test // test deleting a note without force, user confirms
    public void testDeleteNoteUserConfirms() throws ZettelException {
        String userInput = "y\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("1", "Note One", "note1.txt", "Body 1", Instant.now(), Instant.now()));
        notes.add(new Note("2", "Note Two", "note2.txt", "Body 2", Instant.now(), Instant.now()));

        TestUI ui = new TestUI();
        Storage storage = new Storage(FILE_PATH) {
            @Override
            public void save(List<Note> notesToSave) {
                // no-op for tests
            }
        };

        DeleteNoteCommand cmd = new DeleteNoteCommand("2", false);
        cmd.execute(notes, ui, storage);

        assertTrue(ui.containsEvent("noteDeleted:2"), "expected UI to record noteDeleted:2");
        assertEquals(1, notes.size());
    }

    @Test // test deleting a note without force, user cancels
    public void testDeleteNoteUserCancels() throws ZettelException {
        String userInput = "n\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("1", "Note One", "note1.txt", "Body 1", Instant.now(), Instant.now()));
        notes.add(new Note("2", "Note Two", "note2.txt", "Body 2", Instant.now(), Instant.now()));

        TestUI ui = new TestUI();
        Storage storage = new Storage(FILE_PATH) {
            @Override
            public void save(List<Note> notesToSave) {
                // no-op for tests
            }
        };

        DeleteNoteCommand cmd = new DeleteNoteCommand("1", false);
        cmd.execute(notes, ui, storage);

        assertTrue(ui.containsEvent("cancelled"), "expected UI to record cancelled");
        assertEquals(2, notes.size());
    }
}
