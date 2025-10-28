package seedu.zettel.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.storage.Storage;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DeleteNoteCommandTest {

    @TempDir
    Path tempDir;

    private Storage storage;
    private UI ui;
    private ArrayList<Note> notes;
    private List<String> tags;

    @BeforeEach
    void setUp() {
        storage = new Storage(tempDir.toString());
        storage.init();
        ui = new UI();
        notes = new ArrayList<>();
        tags = new ArrayList<>();
    }

    @Test
    void execute_successfulDelete_removesFileAndMetadata() throws Exception {
        // Create a test note
        Note testNote = new Note(
                "12345678",
                "Test Note",
                "Test_Note.txt",
                "Test body content",
                Instant.now(),
                Instant.now()
        );
        notes.add(testNote);

        // Create the physical file
        storage.createStorageFile(testNote);
        storage.save(notes);

        // Verify file exists before deletion
        Path notesDir = tempDir.resolve("main").resolve("notes");
        Path noteFile = notesDir.resolve("Test_Note.txt");
        assertTrue(Files.exists(noteFile), "Note file should exist before deletion");

        // Execute delete with force flag (skip confirmation)
        DeleteNoteCommand command = new DeleteNoteCommand("12345678", true);
        command.execute(notes, tags, ui, storage);

        // Verify note removed from list
        assertEquals(0, notes.size(), "Note should be removed from ArrayList");

        // Verify physical file deleted
        assertFalse(Files.exists(noteFile), "Note file should be deleted");

        // Verify metadata removed from index.txt
        Path indexPath = tempDir.resolve("main").resolve("index.txt");
        String indexContent = Files.readString(indexPath);
        assertFalse(indexContent.contains("12345678"), "Note ID should not be in index.txt");
    }

    @Test
    void execute_missingBodyFile_stillRemovesMetadata() throws Exception {
        // Create a note
        Note testNote = new Note(
                "abcdefgh",
                "Orphan Note",
                "Orphan_Note.txt",
                "Body content",
                Instant.now(),
                Instant.now()
        );
        notes.add(testNote);

        // Manually write only metadata to index.txt (without body file)
        Path indexPath = tempDir.resolve("main").resolve("index.txt");
        Files.createDirectories(indexPath.getParent());

        // Follow actual index format
        String metadata = String.join(" | ",
                testNote.getId(),
                testNote.getTitle(),
                testNote.getFilename(),
                testNote.getCreatedAt().toString(),
                testNote.getModifiedAt().toString(),
                "0",  // pin flag
                "0",  // archive flag
                "",   // tags (empty)
                "",   // tags (empty)
                ""    // extra placeholder
        );

        Files.writeString(indexPath, metadata + System.lineSeparator());

        // Verify body file is missing
        Path notesDir = tempDir.resolve("main").resolve("notes");
        Path noteFile = notesDir.resolve("Orphan_Note.txt");
        assertFalse(Files.exists(noteFile), "Note file should not exist");

        // Execute delete
        DeleteNoteCommand command = new DeleteNoteCommand("abcdefgh", true);
        command.execute(notes, tags, ui, storage);

        // Verify note removed from list
        assertEquals(0, notes.size(), "Note should be removed even if file is missing");

        // Verify metadata removed
        String indexContent = Files.readString(indexPath);
        assertFalse(indexContent.contains("abcdefgh"), "Note ID should not be in index.txt");
    }


    @Test
    void execute_nonExistentId_throwsException() {
        // Add a different note
        Note testNote = new Note(
                "11111111",
                "Existing Note",
                "Existing_Note.txt",
                "Content",
                Instant.now(),
                Instant.now()
        );
        notes.add(testNote);

        // Try to delete non-existent note
        DeleteNoteCommand command = new DeleteNoteCommand("99999999", true);

        assertThrows(InvalidNoteIdException.class, () -> {
            command.execute(notes, tags, ui, storage);
        }, "Should throw InvalidNoteIdException for non-existent ID");

        // Verify original note still exists
        assertEquals(1, notes.size(), "Original note should remain");
    }

    @Test
    void execute_emptyNotesList_throwsException() {
        // Try to delete from empty list
        DeleteNoteCommand command = new DeleteNoteCommand("12345678", true);

        assertThrows(NoNotesException.class, () -> {
            command.execute(notes, tags, ui, storage);
        }, "Should throw NoNotesException when notes list is empty");
    }

    @Test
    void execute_withConfirmation_deletesOnYes() throws Exception {
        // Create test note
        Note testNote = new Note(
                "22222222",
                "Confirm Delete",
                "Confirm_Delete.txt",
                "Test content",
                Instant.now(),
                Instant.now()
        );
        notes.add(testNote);
        storage.createStorageFile(testNote);
        storage.save(notes);

        // Simulate user typing "yes"
        String userInput = "yes\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        // Execute delete without force flag
        DeleteNoteCommand command = new DeleteNoteCommand("22222222", false);
        command.execute(notes, tags, ui, storage);

        // Verify deletion occurred
        assertEquals(0, notes.size(), "Note should be deleted after confirmation");
    }

    @Test
    void execute_withConfirmation_cancelsOnNo() throws Exception {
        // Create test note
        Note testNote = new Note(
                "33333333",
                "Cancel Delete",
                "Cancel_Delete.txt",
                "Test content",
                Instant.now(),
                Instant.now()
        );
        notes.add(testNote);
        storage.createStorageFile(testNote);
        storage.save(notes);

        // Simulate user typing "no"
        String userInput = "no\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        // Execute delete without force flag
        DeleteNoteCommand command = new DeleteNoteCommand("33333333", false);
        command.execute(notes, tags, ui, storage);

        // Verify deletion was cancelled
        assertEquals(1, notes.size(), "Note should not be deleted after cancellation");

        // Verify file still exists
        Path notesDir = tempDir.resolve("main").resolve("notes");
        Path noteFile = notesDir.resolve("Cancel_Delete.txt");
        assertTrue(Files.exists(noteFile), "Note file should still exist after cancellation");
    }
}
