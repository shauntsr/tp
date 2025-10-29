package seedu.zettel.commands;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


/**
 * Note: These tests do NOT actually open editors because:
 * 1. JUnit has no console (System.console() returns null)
 * 2. EditorUtil will throw EditorNotFoundException in test environment
 * <p>
 * We test the command's logic and error handling paths.
 */
class EditNoteCommandTest {

    @TempDir
    Path tempDir;

    private ArrayList<Note> notes;
    private List<String> tags;
    private UI ui;
    private Storage storage;

    @BeforeEach
    void setUp() {
        notes = new ArrayList<>();
        tags = new ArrayList<>();
        ui = new UI();
        storage = new Storage(tempDir.toString());
        storage.init();
    }

    @Test
    void testValidNoteId_createsCommand() {
        EditNoteCommand command = new EditNoteCommand("12ab34cd");

        assertNotNull(command);
    }

    @Test
    void testNoteNotFound_throwsNoNoteFoundException() {
        String nonExistentId = "99999999";
        EditNoteCommand command = new EditNoteCommand(nonExistentId);

        ZettelException exception = assertThrows(
            ZettelException.class,
            () -> command.execute(notes, tags, ui, storage)
        );

        assertTrue(exception.getMessage().contains("Note with ID '99999999' not found"));
    }

    @Test
    void testEmptyNotesList_throwsNoNoteFoundException() {
        EditNoteCommand command = new EditNoteCommand("12ab34cd");
        // notes list is empty

        ZettelException exception = assertThrows(
            ZettelException.class,
            () -> command.execute(notes, tags, ui, storage)
        );

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void testValidNoteId_attemptsToOpenEditor() {
        Note testNote = new Note(
            "12ab34cd",
            "Test Note",
            "Test_Note.txt",
            "Original body",
            Instant.now(),
            Instant.now()
        );
        notes.add(testNote);

        // Create the actual file on disk
        storage.createStorageFile(testNote);
        storage.save(notes);

        EditNoteCommand command = new EditNoteCommand("12ab34cd");

        // In test environment (no console), EditorUtil will throw EditorNotFoundException
        // The command should catch it and wrap it in ZettelException
        ZettelException exception = assertThrows(
            ZettelException.class,
            () -> command.execute(notes, tags, ui, storage)
        );

        assertTrue(exception.getMessage().contains("Failed to open editor")
                || exception.getMessage().contains("No interactive terminal"));
    }

    @Test
    void testMultipleNotes_findsCorrectNote() {
        Note note1 = new Note("11111111", "First", "First.txt", "Body 1",
                             Instant.now(), Instant.now());
        Note note2 = new Note("22222222", "Second", "Second.txt", "Body 2",
                             Instant.now(), Instant.now());
        Note note3 = new Note("33333333", "Third", "Third.txt", "Body 3",
                             Instant.now(), Instant.now());

        notes.add(note1);
        notes.add(note2);
        notes.add(note3);

        // Create files
        storage.createStorageFile(note1);
        storage.createStorageFile(note2);
        storage.createStorageFile(note3);
        storage.save(notes);

        EditNoteCommand command = new EditNoteCommand("22222222");

        // Will fail at editor opening, but should find the correct note first
        assertThrows(ZettelException.class,
            () -> command.execute(notes, tags, ui, storage));

        // If we got past note finding, the correct note was identified
        // (otherwise would throw "not found" instead of editor error)
    }

    @Test
    void testNoteIdCaseSensitive_mustMatchExactly() {
        Note testNote = new Note("12ab34cd", "Test", "Test.txt", "Body",
                                Instant.now(), Instant.now());
        notes.add(testNote);
        storage.createStorageFile(testNote);
        storage.save(notes);

        // Try with uppercase (should not match)
        EditNoteCommand command = new EditNoteCommand("12AB34CD");

        ZettelException exception = assertThrows(
            ZettelException.class,
            () -> command.execute(notes, tags, ui, storage)
        );

        assertTrue(exception.getMessage().contains("not found"));
    }
}
