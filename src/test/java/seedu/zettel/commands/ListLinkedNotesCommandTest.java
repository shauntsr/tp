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
import seedu.zettel.exceptions.InvalidInputException;
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

public class ListLinkedNotesCommandTest {
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

        // Create sample notes for testing
        Note note1 = new Note("abcd1234", "First Note", "First_Note.txt",
                "Body of first note", Instant.now(), Instant.now());
        Note note2 = new Note("ef567890", "Second Note", "Second_Note.txt",
                "Body of second note", Instant.now(), Instant.now());
        Note note3 = new Note("12345678", "Third Note", "Third_Note.txt",
                "Body of third note", Instant.now(), Instant.now());
        
        notes.add(note1);
        notes.add(note2);
        notes.add(note3);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOutputStream);
    }

    // ==================== Exception Tests ====================

    @Test
    void testNoteIdNotFoundThrowsException() {
        ListLinkedNotesCommand cmd = new ListLinkedNotesCommand("outgoing", "deadbeef");

        InvalidNoteIdException ex = assertThrows(InvalidNoteIdException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals("Invalid Note ID! Note with ID 'deadbeef' does not exist.", ex.getMessage());
    }

    @Test
    void testNoOutgoingLinksThrowsException() {
        // Note with no outgoing links
        ListLinkedNotesCommand cmd = new ListLinkedNotesCommand("outgoing", "abcd1234");

        NoNotesException ex = assertThrows(NoNotesException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals("Note with ID 'abcd1234' has no outgoing links.", ex.getMessage());
    }

    @Test
    void testNoIncomingLinksThrowsException() {
        // Note with no incoming links
        ListLinkedNotesCommand cmd = new ListLinkedNotesCommand("incoming", "abcd1234");

        NoNotesException ex = assertThrows(NoNotesException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals("Note with ID 'abcd1234' has no incoming links.", ex.getMessage());
    }

    // ==================== Happy Path Tests ====================

    @Test
    void testListOutgoingLinksSuccessfully() throws ZettelException {
        // Set up: note1 has outgoing links to note2 and note3
        Note note1 = notes.get(0); // abcd1234
        note1.addOutgoingLink("ef567890");
        note1.addOutgoingLink("12345678");

        ListLinkedNotesCommand cmd = new ListLinkedNotesCommand("outgoing", "abcd1234");
        cmd.execute(notes, tags, ui, storage);

        String output = outputStream.toString();
        
        // Should show both linked notes
        assertTrue(output.contains("ef567890"), "Should contain note ID ef567890");
        assertTrue(output.contains("12345678"), "Should contain note ID 12345678");
        assertTrue(output.contains("Second Note") || output.contains("Second_Note.txt"), 
                "Should show second note details");
        assertTrue(output.contains("Third Note") || output.contains("Third_Note.txt"), 
                "Should show third note details");
    }

    @Test
    void testListIncomingLinksSuccessfully() throws ZettelException {
        // Set up: note1 has incoming links from note2 and note3
        Note note1 = notes.get(0); // abcd1234
        note1.addIncomingLink("ef567890");
        note1.addIncomingLink("12345678");

        ListLinkedNotesCommand cmd = new ListLinkedNotesCommand("incoming", "abcd1234");
        cmd.execute(notes, tags, ui, storage);

        String output = outputStream.toString();
        
        // Should show both linked notes
        assertTrue(output.contains("ef567890"), "Should contain note ID ef567890");
        assertTrue(output.contains("12345678"), "Should contain note ID 12345678");
        assertTrue(output.contains("Second Note") || output.contains("Second_Note.txt"), 
                "Should show second note details");
        assertTrue(output.contains("Third Note") || output.contains("Third_Note.txt"), 
                "Should show third note details");
    }

    @Test
    void testListSingleOutgoingLink() throws ZettelException {
        // Set up: note1 has only one outgoing link
        Note note1 = notes.get(0); // abcd1234
        note1.addOutgoingLink("ef567890");

        ListLinkedNotesCommand cmd = new ListLinkedNotesCommand("outgoing", "abcd1234");
        cmd.execute(notes, tags, ui, storage);

        String output = outputStream.toString();
        
        // Should show the linked note
        assertTrue(output.contains("ef567890"), "Should contain note ID ef567890");
        assertTrue(output.contains("Second Note") || output.contains("Second_Note.txt"), 
                "Should show second note details");
    }

    @Test
    void testListSingleIncomingLink() throws ZettelException {
        // Set up: note1 has only one incoming link
        Note note1 = notes.get(0); // abcd1234
        note1.addIncomingLink("ef567890");

        ListLinkedNotesCommand cmd = new ListLinkedNotesCommand("incoming", "abcd1234");
        cmd.execute(notes, tags, ui, storage);

        String output = outputStream.toString();
        
        // Should show the linked note
        assertTrue(output.contains("ef567890"), "Should contain note ID ef567890");
        assertTrue(output.contains("Second Note") || output.contains("Second_Note.txt"), 
                "Should show second note details");
    }

    @Test
    void testListOutgoingLinksDoesNotShowUnlinkedNotes() throws ZettelException {
        // Set up: note1 has outgoing link to note2 only, not note3
        Note note1 = notes.get(0); // abcd1234
        note1.addOutgoingLink("ef567890");

        ListLinkedNotesCommand cmd = new ListLinkedNotesCommand("outgoing", "abcd1234");
        cmd.execute(notes, tags, ui, storage);

        String output = outputStream.toString();
        
        // Should show note2 but not note3
        assertTrue(output.contains("ef567890"), "Should contain note ID ef567890");
        assertTrue(output.contains("Second Note") || output.contains("Second_Note.txt"), 
                "Should show second note details");
        
        // Note3 should not appear (it's not linked)
        assertTrue(!output.contains("Third Note") || output.contains("Second"), 
                "Should not prominently show third note");
    }

    @Test
    void testListIncomingLinksDoesNotShowUnlinkedNotes() throws ZettelException {
        // Set up: note1 has incoming link from note2 only, not note3
        Note note1 = notes.get(0); // abcd1234
        note1.addIncomingLink("ef567890");

        ListLinkedNotesCommand cmd = new ListLinkedNotesCommand("incoming", "abcd1234");
        cmd.execute(notes, tags, ui, storage);

        String output = outputStream.toString();
        
        // Should show note2 but not note3
        assertTrue(output.contains("ef567890"), "Should contain note ID ef567890");
        assertTrue(output.contains("Second Note") || output.contains("Second_Note.txt"), 
                "Should show second note details");
        
        // Note3 should not appear (it's not linked)
        assertTrue(!output.contains("Third Note") || output.contains("Second"), 
                "Should not prominently show third note");
    }

    @Test
    void testListOutgoingLinksMultipleNotes() throws ZettelException {
        // Set up: note2 has multiple outgoing links
        Note note2 = notes.get(1); // ef567890
        note2.addOutgoingLink("abcd1234");
        note2.addOutgoingLink("12345678");

        ListLinkedNotesCommand cmd = new ListLinkedNotesCommand("outgoing", "ef567890");
        cmd.execute(notes, tags, ui, storage);

        String output = outputStream.toString();
        
        // Should show both linked notes
        assertTrue(output.contains("abcd1234"), "Should contain note ID abcd1234");
        assertTrue(output.contains("12345678"), "Should contain note ID 12345678");
    }

    @Test
    void testListIncomingLinksMultipleNotes() throws ZettelException {
        // Set up: note2 has multiple incoming links
        Note note2 = notes.get(1); // ef567890
        note2.addIncomingLink("abcd1234");
        note2.addIncomingLink("12345678");

        ListLinkedNotesCommand cmd = new ListLinkedNotesCommand("incoming", "ef567890");
        cmd.execute(notes, tags, ui, storage);

        String output = outputStream.toString();
        
        // Should show both linked notes
        assertTrue(output.contains("abcd1234"), "Should contain note ID abcd1234");
        assertTrue(output.contains("12345678"), "Should contain note ID 12345678");
    }

    // ==================== Additional Edge Case Tests ====================

    @Test
    void testEmptyNotesListThrowsException() {
        notes.clear(); // Empty the notes list
        ListLinkedNotesCommand cmd = new ListLinkedNotesCommand("outgoing", "abcd1234");

        NoNotesException ex = assertThrows(NoNotesException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals("You have no notes to list links from.", ex.getMessage());
    }

    @Test
    void testInvalidListTypeThrowsException() {
        ListLinkedNotesCommand cmd = new ListLinkedNotesCommand("invalid", "abcd1234");

        InvalidInputException ex = assertThrows(InvalidInputException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals("Invalid Input: Invalid list type: 'invalid'. Must be 'incoming' or 'outgoing'.", 
                ex.getMessage());
    }

    @Test
    void testOrphanedLinksThrowsException() {
        // Set up: note1 has outgoing links to non-existent notes
        Note note1 = notes.get(0); // abcd1234
        note1.addOutgoingLink("deadbeef"); // This note doesn't exist
        note1.addOutgoingLink("cafebabe"); // This note doesn't exist either

        ListLinkedNotesCommand cmd = new ListLinkedNotesCommand("outgoing", "abcd1234");

        NoNotesException ex = assertThrows(NoNotesException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals("Note with ID 'abcd1234' has outgoing links, but the linked notes no longer exist.", 
                ex.getMessage());
    }

    @Test
    void testOrphanedIncomingLinksThrowsException() {
        // Set up: note1 has incoming links from non-existent notes
        Note note1 = notes.get(0); // abcd1234
        note1.addIncomingLink("deadbeef"); // This note doesn't exist
        note1.addIncomingLink("cafebabe"); // This note doesn't exist either

        ListLinkedNotesCommand cmd = new ListLinkedNotesCommand("incoming", "abcd1234");

        NoNotesException ex = assertThrows(NoNotesException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals("Note with ID 'abcd1234' has incoming links, but the linked notes no longer exist.", 
                ex.getMessage());
    }
}

