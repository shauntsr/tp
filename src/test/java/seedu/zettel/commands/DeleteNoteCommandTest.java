
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
import seedu.zettel.Storage;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidFormatException;
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.ZettelException;

public class DeleteNoteCommandTest {

    private static final String FILE_PATH = "./data/zettel.txt";

    // Simple UI test double that records calls
    private static class TestUI extends UI {
        private final ArrayList<String> events = new ArrayList<>();

        @Override
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

    // ==================== Exception Tests ====================
    
    @Test
    public void execute_invalidFormatTooShort_throwsInvalidFormatException() {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("12345678", "Note One", "note1.txt", "Body 1", Instant.now(), Instant.now()));

        TestUI ui = new TestUI();
        Storage storage = new Storage(FILE_PATH) {
            @Override
            public void save(List<Note> notesToSave) {
                // no-op for tests
            }
        };

        DeleteNoteCommand cmd = new DeleteNoteCommand("123", true);
        assertThrows(InvalidFormatException.class, () -> {
            cmd.execute(notes, ui, storage);
        });
    }
    
    @Test
    public void execute_invalidFormatTooLong_throwsInvalidFormatException() {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("12345678", "Note One", "note1.txt", "Body 1", Instant.now(), Instant.now()));

        TestUI ui = new TestUI();
        Storage storage = new Storage(FILE_PATH) {
            @Override
            public void save(List<Note> notesToSave) {
                // no-op for tests
            }
        };

        DeleteNoteCommand cmd = new DeleteNoteCommand("123456789", true);
        assertThrows(InvalidFormatException.class, () -> {
            cmd.execute(notes, ui, storage);
        });
    }
    
    @Test
    public void execute_invalidFormatSpecialCharacters_throwsInvalidFormatException() {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("12345678", "Note One", "note1.txt", "Body 1", Instant.now(), Instant.now()));

        TestUI ui = new TestUI();
        Storage storage = new Storage(FILE_PATH) {
            @Override
            public void save(List<Note> notesToSave) {
                // no-op for tests
            }
        };

        DeleteNoteCommand cmd = new DeleteNoteCommand("1234-678", true);
        assertThrows(InvalidFormatException.class, () -> {
            cmd.execute(notes, ui, storage);
        });
    }
    
    @Test
    public void execute_invalidFormatNull_throwsInvalidFormatException() {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("12345678", "Note One", "note1.txt", "Body 1", Instant.now(), Instant.now()));

        TestUI ui = new TestUI();
        Storage storage = new Storage(FILE_PATH) {
            @Override
            public void save(List<Note> notesToSave) {
                // no-op for tests
            }
        };

        DeleteNoteCommand cmd = new DeleteNoteCommand(null, true);
        assertThrows(InvalidFormatException.class, () -> {
            cmd.execute(notes, ui, storage);
        });
    }
    
    @Test
    public void execute_emptyNotesList_throwsNoNotesException() {
        ArrayList<Note> notes = new ArrayList<>();

        TestUI ui = new TestUI();
        Storage storage = new Storage(FILE_PATH) {
            @Override
            public void save(List<Note> notesToSave) {
                // no-op for tests
            }
        };

        DeleteNoteCommand cmd = new DeleteNoteCommand("12345678", true);
        assertThrows(NoNotesException.class, () -> {
            cmd.execute(notes, ui, storage);
        });
    }
    
    @Test
    public void execute_noteIdNotFound_throwsInvalidNoteIdException() {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("12345678", "Note One", "note1.txt", "Body 1", Instant.now(), Instant.now()));
        notes.add(new Note("abcdefgh", "Note Two", "note2.txt", "Body 2", Instant.now(), Instant.now()));

        TestUI ui = new TestUI();
        Storage storage = new Storage(FILE_PATH) {
            @Override
            public void save(List<Note> notesToSave) {
                // no-op
            }
        };

        DeleteNoteCommand cmd = new DeleteNoteCommand("99999999", true);

        assertThrows(InvalidNoteIdException.class, () -> {
            cmd.execute(notes, ui, storage);
        });

        // UI should have been informed before the exception
        assertTrue(ui.containsEvent("notFound:99999999"), "expected UI to record notFound:99999999");
        assertEquals(2, notes.size());
    }

    // ==================== Happy Path Tests ====================
    
    @Test // test deleting an existing note with force
    public void execute_deleteNoteWithForce_noteIsDeleted() throws ZettelException {
        ArrayList<Note> notes = new ArrayList<>();
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
        cmd.execute(notes, ui, storage);

        assertTrue(ui.containsEvent("noteDeleted:12345678"), "expected UI to record noteDeleted:12345678");
        assertEquals(1, notes.size());
    }

    @Test // test deleting a note without force, user confirms
    public void execute_deleteNoteUserConfirms_noteIsDeleted() throws ZettelException {
        String userInput = "y\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        ArrayList<Note> notes = new ArrayList<>();
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
        cmd.execute(notes, ui, storage);

        assertTrue(ui.containsEvent("noteDeleted:abcdefgh"), "expected UI to record noteDeleted:abcdefgh");
        assertEquals(1, notes.size());
    }

    @Test // test deleting a note without force, user cancels
    public void execute_deleteNoteUserCancels_noteIsNotDeleted() throws ZettelException {
        String userInput = "n\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        ArrayList<Note> notes = new ArrayList<>();
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
        cmd.execute(notes, ui, storage);

        assertTrue(ui.containsEvent("cancelled"), "expected UI to record cancelled");
        assertEquals(2, notes.size());
    }
    
    @Test // test deleting a note without force, user types "yes"
    public void execute_deleteNoteUserTypesYes_noteIsDeleted() throws ZettelException {
        String userInput = "yes\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("12345678", "Note One", "note1.txt", "Body 1", Instant.now(), Instant.now()));

        TestUI ui = new TestUI();
        Storage storage = new Storage(FILE_PATH) {
            @Override
            public void save(List<Note> notesToSave) {
                // no-op for tests
            }
        };

        DeleteNoteCommand cmd = new DeleteNoteCommand("12345678", false);
        cmd.execute(notes, ui, storage);

        assertTrue(ui.containsEvent("noteDeleted:12345678"), "expected UI to record noteDeleted:12345678");
        assertEquals(0, notes.size());
    }
}
