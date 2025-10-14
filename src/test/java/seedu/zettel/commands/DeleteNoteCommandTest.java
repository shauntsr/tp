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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeleteNoteCommandTest {

    private static final String FILE_PATH = "./data/zettel.txt";

    // Simple UI test double that records calls
    private static class TestUI extends UI {
        private final List<String> events = new ArrayList<>();

        @Override
        public void showDeleteNotFound(String id) {
            events.add("notFound:" + id);
        }

        @Override
        public void showDeleteConfirmation(String message) {
            // record that confirmation was requested; include message for debugging if needed
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

        public List<String> getEvents() {
            return events;
        }
    }

    @BeforeEach
    public void setUp() {
        // nothing needed here anymore
    }

    @AfterEach
    public void tearDown() {
        // ensure System.in restored to default after tests that change it
        System.setIn(System.in);
    }

    @Test // test deleting an existing note with force
    public void testDeleteNoteWithForce() {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("1", "Note One", "note1.txt", "Body 1", Instant.now(), Instant.now()));
        notes.add(new Note("2", "Note Two", "note2.txt", "Body 2", Instant.now(), Instant.now()));

        TestUI ui = new TestUI();
        // make Storage a no-op save so tests don't write files
        Storage storage = new Storage(FILE_PATH) {
            @Override
            public void save(List<Note> notesToSave) {
                // no-op for tests
            }
        };

        DeleteNoteCommand cmd = new DeleteNoteCommand("1", true);
        cmd.execute(notes, ui, storage);

        // assert UI recorded deletion event and note removed
        assertTrue(ui.containsEvent("noteDeleted:1"), "expected UI to record noteDeleted:1");
        assertEquals(1, notes.size());
    }

    @Test // test deleting a non-existing note
    public void testDeleteNonExistingNote() {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("1", "Note One", "note1.txt", "Body 1", Instant.now(), Instant.now()));

        TestUI ui = new TestUI();
        Storage storage = new Storage(FILE_PATH) {
            @Override
            public void save(List<Note> notesToSave) {
                // no-op for tests
            }
        };

        DeleteNoteCommand cmd = new DeleteNoteCommand("3", true);
        cmd.execute(notes, ui, storage);

        assertTrue(ui.containsEvent("notFound:3"), "expected UI to record notFound:3");
        assertEquals(1, notes.size());
    }

    @Test // test deleting a note without force, user confirms
    public void testDeleteNoteUserConfirms() {
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
    public void testDeleteNoteUserCancels() {
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
