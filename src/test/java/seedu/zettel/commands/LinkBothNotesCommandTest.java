package seedu.zettel.commands;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.NoteSelfLinkException;
import seedu.zettel.exceptions.NotesAlreadyLinkedException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

public class LinkBothNotesCommandTest {
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

        // Create three sample notes for linking
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
    void execute_emptyNotesList_throwsNoNotesException() {
        ArrayList<Note> emptyNotes = new ArrayList<>();
        LinkBothNotesCommand command = new LinkBothNotesCommand("abcd1234", "ef567890");

        NoNotesException exception = assertThrows(NoNotesException.class, () -> {
            command.execute(emptyNotes, tags, ui, storage);
        });

        assertEquals("You have no notes to link.", exception.getMessage());
    }

    @Test
    void execute_selfLink_throwsNoteSelfLinkException() {
        LinkBothNotesCommand command = new LinkBothNotesCommand("abcd1234", "abcd1234");

        NoteSelfLinkException exception = assertThrows(NoteSelfLinkException.class, () -> {
            command.execute(notes, tags, ui, storage);
        });

        assertEquals("Cannot link a note to itself. Note ID: 'abcd1234'.", exception.getMessage());
    }

    @Test
    void execute_sourceNoteNotFound_throwsInvalidNoteIdException() {
        LinkBothNotesCommand command = new LinkBothNotesCommand("99999999", "ef567890");

        InvalidNoteIdException exception = assertThrows(InvalidNoteIdException.class, () -> {
            command.execute(notes, tags, ui, storage);
        });

        assertEquals("Invalid Note ID! Note with ID '99999999' does not exist.", exception.getMessage());
    }

    @Test
    void execute_targetNoteNotFound_throwsInvalidNoteIdException() {
        LinkBothNotesCommand command = new LinkBothNotesCommand("abcd1234", "99999999");

        InvalidNoteIdException exception = assertThrows(InvalidNoteIdException.class, () -> {
            command.execute(notes, tags, ui, storage);
        });

        assertEquals("Invalid Note ID! Note with ID '99999999' does not exist.", exception.getMessage());
    }

    @Test
    void execute_bothNotesNotFound_throwsInvalidNoteIdException() {
        LinkBothNotesCommand command = new LinkBothNotesCommand("99999999", "88888888");

        InvalidNoteIdException exception = assertThrows(InvalidNoteIdException.class, () -> {
            command.execute(notes, tags, ui, storage);
        });

        // Should fail on the first note ID check
        assertEquals("Invalid Note ID! Note with ID '99999999' does not exist.", exception.getMessage());
    }

    @Test
    void execute_bidirectionalLinkAlreadyExists_throwsNotesAlreadyLinkedException() throws ZettelException {
        Note note1 = notes.get(0);
        Note note2 = notes.get(1);

        // Manually create a bidirectional link
        note1.addOutgoingLink("ef567890");
        note1.addIncomingLink("ef567890");
        note2.addOutgoingLink("abcd1234");
        note2.addIncomingLink("abcd1234");

        // Try to link them again
        LinkBothNotesCommand command = new LinkBothNotesCommand("abcd1234", "ef567890");

        NotesAlreadyLinkedException exception = assertThrows(NotesAlreadyLinkedException.class, () -> {
            command.execute(notes, tags, ui, storage);
        });

        assertEquals("Note with ID 'abcd1234' already links in both directions to note with ID 'ef567890'.",
                exception.getMessage());
    }

    // ==================== Happy Path Tests ====================

    @Test
    void execute_successfulBidirectionalLink_createsLinksBothDirections() throws ZettelException {
        LinkBothNotesCommand command = new LinkBothNotesCommand("abcd1234", "ef567890");
        command.execute(notes, tags, ui, storage);

        Note note1 = notes.get(0); // abcd1234
        Note note2 = notes.get(1); // ef567890

        // Verify bidirectional links exist
        assertTrue(note1.isLinkedTo("ef567890"), "Note1 should have Note2 in outgoingLinks");
        assertTrue(note1.isLinkedBy("ef567890"), "Note1 should have Note2 in incomingLinks");
        assertTrue(note2.isLinkedTo("abcd1234"), "Note2 should have Note1 in outgoingLinks");
        assertTrue(note2.isLinkedBy("abcd1234"), "Note2 should have Note1 in incomingLinks");

        // Verify UI message
        String output = outputStream.toString();
        assertTrue(output.contains("Notes 'First Note' and 'Second Note' are now linked in both directions"));
    }

    @Test
    void execute_reverseOrderBidirectionalLink_createsLinksBothDirections() throws ZettelException {
        // Link in reverse order: ef567890 ↔ abcd1234
        LinkBothNotesCommand command = new LinkBothNotesCommand("ef567890", "abcd1234");
        command.execute(notes, tags, ui, storage);

        Note note1 = notes.get(0); // abcd1234
        Note note2 = notes.get(1); // ef567890

        // Verify bidirectional links exist
        assertTrue(note1.isLinkedTo("ef567890"), "Note1 should have Note2 in outgoingLinks");
        assertTrue(note1.isLinkedBy("ef567890"), "Note1 should have Note2 in incomingLinks");
        assertTrue(note2.isLinkedTo("abcd1234"), "Note2 should have Note1 in outgoingLinks");
        assertTrue(note2.isLinkedBy("abcd1234"), "Note2 should have Note1 in incomingLinks");

        // Verify UI message
        String output = outputStream.toString();
        assertTrue(output.contains("Notes 'Second Note' and 'First Note' are now linked in both directions"));
    }

    @Test
    void execute_unidirectionalLinkExists_addsOnlyMissingDirection() throws ZettelException {
        Note note1 = notes.get(0);
        Note note2 = notes.get(1);

        // Create only a unidirectional link: note1 → note2 (but not note2 → note1)
        note1.addOutgoingLink("ef567890");
        note2.addIncomingLink("abcd1234");

        // Verify unidirectional link exists
        assertTrue(note1.isLinkedTo("ef567890"));
        assertFalse(note2.isLinkedTo("abcd1234"));

        // Execute bidirectional link command
        LinkBothNotesCommand command = new LinkBothNotesCommand("abcd1234", "ef567890");
        command.execute(notes, tags, ui, storage);

        // Verify now bidirectional
        assertTrue(note1.isLinkedTo("ef567890"));
        assertTrue(note1.isLinkedBy("ef567890"));
        assertTrue(note2.isLinkedTo("abcd1234"));
        assertTrue(note2.isLinkedBy("abcd1234"));
    }

    @Test
    void execute_reversUnidirectionalLinkExists_addsOnlyMissingDirection() throws ZettelException {
        Note note1 = notes.get(0);
        Note note2 = notes.get(1);

        // Create only a reverse unidirectional link: note2 → note1 (but not note1 → note2)
        note2.addOutgoingLink("abcd1234");
        note1.addIncomingLink("ef567890");

        // Verify unidirectional link exists
        assertFalse(note1.isLinkedTo("ef567890"));
        assertTrue(note2.isLinkedTo("abcd1234"));

        // Execute bidirectional link command
        LinkBothNotesCommand command = new LinkBothNotesCommand("abcd1234", "ef567890");
        command.execute(notes, tags, ui, storage);

        // Verify now bidirectional
        assertTrue(note1.isLinkedTo("ef567890"));
        assertTrue(note1.isLinkedBy("ef567890"));
        assertTrue(note2.isLinkedTo("abcd1234"));
        assertTrue(note2.isLinkedBy("abcd1234"));
    }

    @Test
    void execute_multipleBidirectionalLinks_eachNoteHasMultipleLinks() throws ZettelException {
        // Create bidirectional link: note1 ↔ note2
        LinkBothNotesCommand command1 = new LinkBothNotesCommand("abcd1234", "ef567890");
        command1.execute(notes, tags, ui, storage);

        // Create bidirectional link: note1 ↔ note3
        LinkBothNotesCommand command2 = new LinkBothNotesCommand("abcd1234", "12345678");
        command2.execute(notes, tags, ui, storage);

        Note note1 = notes.get(0); // abcd1234
        Note note2 = notes.get(1); // ef567890
        Note note3 = notes.get(2); // 12345678

        // Verify note1 has bidirectional links to both note2 and note3
        assertTrue(note1.isLinkedTo("ef567890"));
        assertTrue(note1.isLinkedBy("ef567890"));
        assertTrue(note1.isLinkedTo("12345678"));
        assertTrue(note1.isLinkedBy("12345678"));

        // Verify note2 only has bidirectional link to note1
        assertTrue(note2.isLinkedTo("abcd1234"));
        assertTrue(note2.isLinkedBy("abcd1234"));
        assertFalse(note2.isLinkedTo("12345678"));
        assertFalse(note2.isLinkedBy("12345678"));

        // Verify note3 only has bidirectional link to note1
        assertTrue(note3.isLinkedTo("abcd1234"));
        assertTrue(note3.isLinkedBy("abcd1234"));
        assertFalse(note3.isLinkedTo("ef567890"));
        assertFalse(note3.isLinkedBy("ef567890"));
    }

    @Test
    void execute_linkCountVerification_correctNumberOfLinks() throws ZettelException {
        LinkBothNotesCommand command = new LinkBothNotesCommand("abcd1234", "ef567890");
        command.execute(notes, tags, ui, storage);

        Note note1 = notes.get(0);
        Note note2 = notes.get(1);

        // Each note should have exactly 1 outgoing and 1 incoming link
        assertEquals(1, note1.getOutgoingLinks().size());
        assertEquals(1, note1.getIncomingLinks().size());
        assertEquals(1, note2.getOutgoingLinks().size());
        assertEquals(1, note2.getIncomingLinks().size());
    }

    @Test
    void execute_threeWayBidirectionalLinks_allNotesLinked() throws ZettelException {
        // Create bidirectional links: note1 ↔ note2 ↔ note3
        LinkBothNotesCommand command1 = new LinkBothNotesCommand("abcd1234", "ef567890");
        command1.execute(notes, tags, ui, storage);

        LinkBothNotesCommand command2 = new LinkBothNotesCommand("ef567890", "12345678");
        command2.execute(notes, tags, ui, storage);

        Note note1 = notes.get(0);
        Note note2 = notes.get(1);
        Note note3 = notes.get(2);

        // Note2 should have bidirectional links to both note1 and note3
        assertEquals(2, note2.getOutgoingLinks().size());
        assertEquals(2, note2.getIncomingLinks().size());
        assertTrue(note2.isLinkedTo("abcd1234"));
        assertTrue(note2.isLinkedBy("abcd1234"));
        assertTrue(note2.isLinkedTo("12345678"));
        assertTrue(note2.isLinkedBy("12345678"));
    }

    @Test
    void execute_noDuplicateLinksCreated_linksRemainsUnique() throws ZettelException {
        Note note1 = notes.get(0);
        Note note2 = notes.get(1);

        // Create partial link manually
        note1.addOutgoingLink("ef567890");

        // Execute bidirectional link command
        LinkBothNotesCommand command = new LinkBothNotesCommand("abcd1234", "ef567890");
        command.execute(notes, tags, ui, storage);

        // Verify no duplicates (each link set should have size 1)
        assertEquals(1, note1.getOutgoingLinks().size());
        assertEquals(1, note1.getIncomingLinks().size());
        assertEquals(1, note2.getOutgoingLinks().size());
        assertEquals(1, note2.getIncomingLinks().size());
    }
}
