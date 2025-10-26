package seedu.zettel.commands;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidFormatException;
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.NotesAlreadyLinkedException;
import seedu.zettel.exceptions.SelfLinkException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

public class LinkNotesCommandTest {
    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOutputStream = System.out;
    private ArrayList<Note> notes;
    private UI ui;
    private Storage storage;
    private List<String> tags;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
        notes = new ArrayList<>();
        tags = new ArrayList<>();
        ui = new UI();
        storage = new Storage(tempDir.toString());
        storage.init();

        // Create two sample notes for linking
        Note note1 = new Note("abcd1234", "First Note", "First_Note.txt",
                "Body of first note", Instant.now(), Instant.now());
        Note note2 = new Note("ef567890", "Second Note", "Second_Note.txt",
                "Body of second note", Instant.now(), Instant.now());
        notes.add(note1);
        notes.add(note2);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOutputStream);
    }

    // ==================== Format Validation Tests ====================

    @Test
    void testInvalidFormatTooShortFirstNoteIdThrowsException() {
        LinkNotesCommand cmd = new LinkNotesCommand("abc", "ef567890");

        ZettelException e = assertThrows(InvalidFormatException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertTrue(e.getMessage().contains("Note ID must be exactly 8 characters long"));
    }

    @Test
    void testInvalidFormatTooShortSecondNoteIdThrowsException() {
        LinkNotesCommand cmd = new LinkNotesCommand("abcd1234", "ef5");

        ZettelException e = assertThrows(InvalidFormatException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertTrue(e.getMessage().contains("Note ID must be exactly 8 characters long"));
    }

    @Test
    void testInvalidFormatTooLongFirstNoteIdThrowsException() {
        LinkNotesCommand cmd = new LinkNotesCommand("abcd12345", "ef567890");

        ZettelException e = assertThrows(InvalidFormatException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertTrue(e.getMessage().contains("Note ID must be exactly 8 characters long"));
    }

    @Test
    void testInvalidFormatTooLongSecondNoteIdThrowsException() {
        LinkNotesCommand cmd = new LinkNotesCommand("abcd1234", "ef567890123");

        ZettelException e = assertThrows(InvalidFormatException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertTrue(e.getMessage().contains("Note ID must be exactly 8 characters long"));
    }

    @Test
    void testInvalidFormatSpecialCharactersFirstNoteIdThrowsException() {
        LinkNotesCommand cmd = new LinkNotesCommand("abcd-234", "ef567890");

        ZettelException e = assertThrows(InvalidFormatException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertTrue(e.getMessage().contains("lowercase hexadecimal characters"));
    }

    @Test
    void testInvalidFormatSpecialCharactersSecondNoteIdThrowsException() {
        LinkNotesCommand cmd = new LinkNotesCommand("abcd1234", "ef56@890");

        ZettelException e = assertThrows(InvalidFormatException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertTrue(e.getMessage().contains("lowercase hexadecimal characters"));
    }

    @Test
    void testInvalidFormatUppercaseFirstNoteIdThrowsException() {
        LinkNotesCommand cmd = new LinkNotesCommand("ABCD1234", "ef567890");

        ZettelException e = assertThrows(InvalidFormatException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertTrue(e.getMessage().contains("lowercase hexadecimal characters"));
    }

    @Test
    void testInvalidFormatUppercaseSecondNoteIdThrowsException() {
        LinkNotesCommand cmd = new LinkNotesCommand("abcd1234", "EF567890");

        ZettelException e = assertThrows(InvalidFormatException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertTrue(e.getMessage().contains("lowercase hexadecimal characters"));
    }

    @Test
    void testInvalidFormatNullFirstNoteIdThrowsException() {
        LinkNotesCommand cmd = new LinkNotesCommand(null, "ef567890");

        ZettelException e = assertThrows(InvalidFormatException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertTrue(e.getMessage().contains("Note ID must be exactly 8 characters long"));
    }

    @Test
    void testInvalidFormatNullSecondNoteIdThrowsException() {
        LinkNotesCommand cmd = new LinkNotesCommand("abcd1234", null);

        ZettelException e = assertThrows(InvalidFormatException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertTrue(e.getMessage().contains("Note ID must be exactly 8 characters long"));
    }

    // ==================== Self-Link Validation Test ====================

    @Test
    void testSelfLinkThrowsException() {
        LinkNotesCommand cmd = new LinkNotesCommand("abcd1234", "abcd1234");

        ZettelException e = assertThrows(SelfLinkException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals("Cannot link a note to itself. Note ID: 'abcd1234'.", e.getMessage());
    }

    // ==================== Empty Notes List Test ====================

    @Test
    void testEmptyNotesListThrowsException() {
        notes.clear(); // Clear all notes
        LinkNotesCommand cmd = new LinkNotesCommand("abcd1234", "ef567890");

        ZettelException e = assertThrows(NoNotesException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals("You have no notes to link.", e.getMessage());
    }

    // ==================== Note Not Found Tests ====================

    @Test
    void testFirstNoteIdNotFoundThrowsException() {
        LinkNotesCommand cmd = new LinkNotesCommand("deadbeef", "ef567890");

        ZettelException e = assertThrows(InvalidNoteIdException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals("Invalid Note ID! Note with ID 'deadbeef' does not exist.", e.getMessage());
    }

    @Test
    void testSecondNoteIdNotFoundThrowsException() {
        LinkNotesCommand cmd = new LinkNotesCommand("abcd1234", "deadbeef");

        ZettelException e = assertThrows(InvalidNoteIdException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals("Invalid Note ID! Note with ID 'deadbeef' does not exist.", e.getMessage());
    }

    @Test
    void testBothNoteIdsNotFoundThrowsException() {
        LinkNotesCommand cmd = new LinkNotesCommand("deadbeef", "cafebabe");

        ZettelException e = assertThrows(InvalidNoteIdException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        // Should fail on the first note ID check
        assertEquals("Invalid Note ID! Note with ID 'deadbeef' does not exist.", e.getMessage());
    }

    // ==================== Already Linked Test ====================

    @Test
    void testLinkAlreadyExistsThrowsException() throws ZettelException {
        // The command checks: noteLinkedTo.get().isLinkedTo(noteIdLinkedBy)
        // which checks if noteIdLinkedBy is in noteLinkedTo's linkedTo set
        // So we need to add ef567890 to abcd1234's linkedTo set to simulate existing link
        Note note1 = notes.get(0);
        note1.addLinkedTo("ef567890");  // Simulate the link already exists

        // Try to link them again
        LinkNotesCommand cmd = new LinkNotesCommand("abcd1234", "ef567890");

        ZettelException e = assertThrows(NotesAlreadyLinkedException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals("Note with ID 'abcd1234' already links to note with ID 'ef567890'.", e.getMessage());
    }

    // ==================== Happy Path Tests ====================

    @Test
    void testLinkNotesSuccessfully() throws ZettelException {
        LinkNotesCommand cmd = new LinkNotesCommand("abcd1234", "ef567890");

        cmd.execute(notes, tags, ui, storage);

        Note note1 = notes.get(0); // abcd1234
        Note note2 = notes.get(1); // ef567890

        // After linking abcd1234 → ef567890:
        // - abcd1234's linkedBy set contains ef567890
        // - ef567890's linkedTo set contains abcd1234
        assertTrue(note1.getLinkedBy().contains("ef567890"), 
                "First note should have second note in its linkedBy set");
        assertTrue(note2.getLinkedTo().contains("abcd1234"), 
                "Second note should have first note in its linkedTo set");
    }

    @Test
    void testLinkNotesReverseOrderSuccessfully() throws ZettelException {
        // Link in reverse order: ef567890 → abcd1234
        LinkNotesCommand cmd = new LinkNotesCommand("ef567890", "abcd1234");

        cmd.execute(notes, tags, ui, storage);

        Note note1 = notes.get(0); // abcd1234
        Note note2 = notes.get(1); // ef567890

        // After linking ef567890 → abcd1234:
        // - ef567890's linkedBy set contains abcd1234
        // - abcd1234's linkedTo set contains ef567890
        assertTrue(note2.getLinkedBy().contains("abcd1234"), 
                "Second note should have first note in its linkedBy set");
        assertTrue(note1.getLinkedTo().contains("ef567890"), 
                "First note should have second note in its linkedTo set");
    }

    @Test
    void testLinkMultipleNotesToSameNote() throws ZettelException {
        // Add a third note
        Note note3 = new Note("12345678", "Third Note", "Third_Note.txt",
                "Body of third note", Instant.now(), Instant.now());
        notes.add(note3);

        // Link note1 → note2
        LinkNotesCommand cmd1 = new LinkNotesCommand("abcd1234", "ef567890");
        cmd1.execute(notes, tags, ui, storage);

        // Link note3 → note2 (multiple notes linking to the same note)
        LinkNotesCommand cmd2 = new LinkNotesCommand("12345678", "ef567890");
        cmd2.execute(notes, tags, ui, storage);

        Note note1 = notes.get(0); // abcd1234
        Note note2 = notes.get(1); // ef567890
        Note note3Updated = notes.get(2); // 12345678

        // After linking abcd1234 → ef567890 and 12345678 → ef567890:
        // - abcd1234's linkedBy set contains ef567890
        // - 12345678's linkedBy set contains ef567890
        // - ef567890's linkedTo set contains both abcd1234 and 12345678
        assertTrue(note1.getLinkedBy().contains("ef567890"), 
                "First note should have second note in linkedBy");
        assertTrue(note3Updated.getLinkedBy().contains("ef567890"), 
                "Third note should have second note in linkedBy");
        assertTrue(note2.getLinkedTo().contains("abcd1234"), 
                "Second note should have first note in linkedTo");
        assertTrue(note2.getLinkedTo().contains("12345678"), 
                "Second note should have third note in linkedTo");
    }

    @Test
    void testLinkSameNoteToMultipleNotes() throws ZettelException {
        // Add a third note
        Note note3 = new Note("12345678", "Third Note", "Third_Note.txt",
                "Body of third note", Instant.now(), Instant.now());
        notes.add(note3);

        // Link note1 → note2
        LinkNotesCommand cmd1 = new LinkNotesCommand("abcd1234", "ef567890");
        cmd1.execute(notes, tags, ui, storage);

        // Link note1 → note3 (same note linking to multiple notes)
        LinkNotesCommand cmd2 = new LinkNotesCommand("abcd1234", "12345678");
        cmd2.execute(notes, tags, ui, storage);

        Note note1 = notes.get(0); // abcd1234
        Note note2 = notes.get(1); // ef567890
        Note note3Updated = notes.get(2); // 12345678

        // After linking abcd1234 → ef567890 and abcd1234 → 12345678:
        // - abcd1234's linkedBy set contains both ef567890 and 12345678
        // - ef567890's linkedTo set contains abcd1234
        // - 12345678's linkedTo set contains abcd1234
        assertTrue(note1.getLinkedBy().contains("ef567890"), 
                "First note should have second note in linkedBy");
        assertTrue(note1.getLinkedBy().contains("12345678"), 
                "First note should have third note in linkedBy");
        assertTrue(note2.getLinkedTo().contains("abcd1234"), 
                "Second note should have first note in linkedTo");
        assertTrue(note3Updated.getLinkedTo().contains("abcd1234"), 
                "Third note should have first note in linkedTo");
    }
}

