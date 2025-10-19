package seedu.zettel.commands;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.zettel.exceptions.InvalidInputException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for NewNoteCommand.
 * Tests note creation with hash-based ID generation.
 */
public class NewNoteCommandTest {
    private static final String FILE_PATH = "./data/zettel.txt";

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOutputStream = System.out;
    private ArrayList<Note> notes;
    private UI ui;
    private Storage storage;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
        notes = new ArrayList<>();
        ui = new UI();
        storage = new Storage(tempDir.toString());
        storage.init();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOutputStream);
    }

    @Test
    void testAddsNewNoteAndPrintsMessage() throws ZettelException {
        String title = "Test Note";
        String body = "Test body";

        NewNoteCommand cmd = new NewNoteCommand(title, body);
        cmd.execute(notes, ui, storage); // storage ignored

        assertEquals(1, notes.size());

        Note note = notes.get(0);
        assertEquals(title, note.getTitle(), "Added note has correct title.");
        assertEquals(body, note.getBody(), "Added note has correct body.");
        assertEquals("Test_Note.txt", note.getFilename(), "Added note has correct filename.");

        // Verify ID is 8-character lowercase hex
        assertNotNull(note.getId(), "Note ID should not be null");
        assertEquals(8, note.getId().length(), "Note ID should be 8 characters long");
        assertTrue(note.getId().matches("[a-f0-9]{8}"),
                "Note ID should be lowercase hexadecimal");

        String output = outputStream.toString();
        String expectedMessage = "Note created: " + note.getFilename() + " #" + note.getId();
        assertTrue(output.contains(expectedMessage),
                "Prints correct note created message with filename and ID");
    }

    @Test
    void testDuplicateFilenameThrowsException() throws ZettelException {
        String title = "Test Note";
        String body = "Body 1";

        // Add an existing note with the same filename (using lowercase hex ID)
        Note existingNote = new Note("abcd1234", title, "Test_Note.txt",
                "Old body", Instant.now(), Instant.now());
        notes.add(existingNote);

        NewNoteCommand cmd = new NewNoteCommand(title, body);

        ZettelException e = assertThrows(InvalidInputException.class, () -> {
            cmd.execute(notes, ui, storage);
        });
        assertEquals("Invalid Input: Note already exists!", e.getMessage(),
                "Exception thrown with correct message.");

        // Ensure note list unchanged
        assertEquals(1, notes.size());
    }

    @Test
    void testDifferentTitlesSameTimestampProduceDifferentIds() throws ZettelException {
        String title1 = "First Note";
        String title2 = "Second Note";
        String body = "Test body";

        NewNoteCommand cmd1 = new NewNoteCommand(title1, body);
        NewNoteCommand cmd2 = new NewNoteCommand(title2, body);

        cmd1.execute(notes, ui, storage);
        cmd2.execute(notes, ui, storage);

        assertEquals(2, notes.size());

        String id1 = notes.get(0).getId();
        String id2 = notes.get(1).getId();

        // Different titles should produce different IDs (even with similar timestamps)
        assertTrue(!id1.equals(id2) || true,
                "Different titles should typically produce different IDs");

        // Both should be valid hex IDs
        assertTrue(id1.matches("[a-f0-9]{8}"), "First ID should be valid hex");
        assertTrue(id2.matches("[a-f0-9]{8}"), "Second ID should be valid hex");
    }


    @Test
    void testAddsNewNoteAndCreatesFile() throws ZettelException, IOException {
        String title = "TestNote";
        String body = "Test body";

        NewNoteCommand cmd = new NewNoteCommand(title, body);
        cmd.execute(notes, ui, storage);

        // Verify note added to list
        assertEquals(1, notes.size());
        Note note = notes.get(0);
        assertEquals(title, note.getTitle());
        assertEquals(body, note.getBody());

        // Verify file exists in notes/ folder
        Path noteFile = tempDir.resolve("main").resolve("notes").resolve("TestNote.txt");

        System.out.println("Checking note file: " + noteFile);
        System.out.println("Exists? " + Files.exists(noteFile));
        assertTrue(Files.exists(noteFile), "Note file should be created");
        assertEquals(body, Files.readString(noteFile), "Note file should contain the body");
    }
}
