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
import seedu.zettel.exceptions.NotesAlreadyUnlinkedException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

public class UnlinkBothNotesCommandTest {
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

        // Create three sample notes for unlinking
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
        UnlinkBothNotesCommand command = new UnlinkBothNotesCommand("abcd1234", "ef567890");

        NoNotesException exception = assertThrows(NoNotesException.class, () -> {
            command.execute(emptyNotes, tags, ui, storage);
        });

        assertEquals("You have no notes to unlink.", exception.getMessage());
    }

    @Test
    void execute_selfUnlink_throwsNoteSelfLinkException() {
        UnlinkBothNotesCommand command = new UnlinkBothNotesCommand("abcd1234", "abcd1234");

        NoteSelfLinkException exception = assertThrows(NoteSelfLinkException.class, () -> {
            command.execute(notes, tags, ui, storage);
        });

        assertEquals("Cannot unlink a note from itself. Note ID: 'abcd1234'.", exception.getMessage());
    }

    @Test
    void execute_firstNoteNotFound_throwsInvalidNoteIdException() {
        UnlinkBothNotesCommand command = new UnlinkBothNotesCommand("99999999", "ef567890");

        InvalidNoteIdException exception = assertThrows(InvalidNoteIdException.class, () -> {
            command.execute(notes, tags, ui, storage);
        });

        assertEquals("Invalid Note ID! Note with ID '99999999' does not exist.", exception.getMessage());
    }

    @Test
    void execute_secondNoteNotFound_throwsInvalidNoteIdException() {
        UnlinkBothNotesCommand command = new UnlinkBothNotesCommand("abcd1234", "99999999");

        InvalidNoteIdException exception = assertThrows(InvalidNoteIdException.class, () -> {
            command.execute(notes, tags, ui, storage);
        });

        assertEquals("Invalid Note ID! Note with ID '99999999' does not exist.", exception.getMessage());
    }

    @Test
    void execute_bothNotesNotFound_throwsInvalidNoteIdException() {
        UnlinkBothNotesCommand command = new UnlinkBothNotesCommand("99999999", "88888888");

        InvalidNoteIdException exception = assertThrows(InvalidNoteIdException.class, () -> {
            command.execute(notes, tags, ui, storage);
        });

        // Should fail on the first note ID check
        assertEquals("Invalid Note ID! Note with ID '99999999' does not exist.", exception.getMessage());
    }

    @Test
    void execute_noLinksExist_throwsNotesAlreadyUnlinkedException() {
        // Note1 and Note2 have no links between them
        UnlinkBothNotesCommand command = new UnlinkBothNotesCommand("abcd1234", "ef567890");

        NotesAlreadyUnlinkedException exception = assertThrows(NotesAlreadyUnlinkedException.class, () -> {
            command.execute(notes, tags, ui, storage);
        });

        assertEquals("Any link between note 'abcd1234' and note 'ef567890' does not exist. Nothing to unlink.",
                exception.getMessage());
    }

    // ==================== Happy Path Tests - Unidirectional Links ====================

    @Test
    void execute_unidirectionalLinkNote1ToNote2_removesLinkSuccessfully() throws ZettelException {
        Note note1 = notes.get(0);
        Note note2 = notes.get(1);

        // Create unidirectional link: note1 → note2
        note1.addOutgoingLink("ef567890");
        note2.addIncomingLink("abcd1234");

        // Verify link exists
        assertTrue(note1.isLinkedTo("ef567890"));
        assertTrue(note2.isLinkedBy("abcd1234"));

        UnlinkBothNotesCommand command = new UnlinkBothNotesCommand("abcd1234", "ef567890");
        command.execute(notes, tags, ui, storage);

        // Verify link is completely removed
        assertFalse(note1.isLinkedTo("ef567890"));
        assertFalse(note2.isLinkedBy("abcd1234"));
        assertEquals(0, note1.getOutgoingLinks().size());
        assertEquals(0, note2.getIncomingLinks().size());

        String output = outputStream.toString();
        assertTrue(output.contains("All links between note #abcd1234 and note #ef567890 have been removed"));
    }

    @Test
    void execute_unidirectionalLinkNote2ToNote1_removesLinkSuccessfully() throws ZettelException {
        Note note1 = notes.get(0);
        Note note2 = notes.get(1);

        // Create unidirectional link: note2 → note1
        note2.addOutgoingLink("abcd1234");
        note1.addIncomingLink("ef567890");

        // Verify link exists
        assertTrue(note2.isLinkedTo("abcd1234"));
        assertTrue(note1.isLinkedBy("ef567890"));

        UnlinkBothNotesCommand command = new UnlinkBothNotesCommand("abcd1234", "ef567890");
        command.execute(notes, tags, ui, storage);

        // Verify link is completely removed
        assertFalse(note2.isLinkedTo("abcd1234"));
        assertFalse(note1.isLinkedBy("ef567890"));
        assertEquals(0, note1.getIncomingLinks().size());
        assertEquals(0, note2.getOutgoingLinks().size());
    }

    // ==================== Happy Path Tests - Bidirectional Links ====================

    @Test
    void execute_bidirectionalLink_removesBothDirections() throws ZettelException {
        Note note1 = notes.get(0);
        Note note2 = notes.get(1);

        // Create bidirectional link
        note1.addOutgoingLink("ef567890");
        note1.addIncomingLink("ef567890");
        note2.addOutgoingLink("abcd1234");
        note2.addIncomingLink("abcd1234");

        // Verify bidirectional link exists
        assertTrue(note1.isLinkedTo("ef567890"));
        assertTrue(note1.isLinkedBy("ef567890"));
        assertTrue(note2.isLinkedTo("abcd1234"));
        assertTrue(note2.isLinkedBy("abcd1234"));

        UnlinkBothNotesCommand command = new UnlinkBothNotesCommand("abcd1234", "ef567890");
        command.execute(notes, tags, ui, storage);

        // Verify all links are removed
        assertFalse(note1.isLinkedTo("ef567890"));
        assertFalse(note1.isLinkedBy("ef567890"));
        assertFalse(note2.isLinkedTo("abcd1234"));
        assertFalse(note2.isLinkedBy("abcd1234"));
        assertEquals(0, note1.getOutgoingLinks().size());
        assertEquals(0, note1.getIncomingLinks().size());
        assertEquals(0, note2.getOutgoingLinks().size());
        assertEquals(0, note2.getIncomingLinks().size());
    }

    @Test
    void execute_bidirectionalLinkWithOtherLinks_removesOnlySpecifiedLink() throws ZettelException {
        Note note1 = notes.get(0);
        Note note2 = notes.get(1);
        Note note3 = notes.get(2);

        // Create bidirectional link between note1 and note2
        note1.addOutgoingLink("ef567890");
        note1.addIncomingLink("ef567890");
        note2.addOutgoingLink("abcd1234");
        note2.addIncomingLink("abcd1234");

        // Create additional link: note1 → note3
        note1.addOutgoingLink("12345678");
        note3.addIncomingLink("abcd1234");

        UnlinkBothNotesCommand command = new UnlinkBothNotesCommand("abcd1234", "ef567890");
        command.execute(notes, tags, ui, storage);

        // Verify note1 ↔ note2 link is removed
        assertFalse(note1.isLinkedTo("ef567890"));
        assertFalse(note1.isLinkedBy("ef567890"));
        assertFalse(note2.isLinkedTo("abcd1234"));
        assertFalse(note2.isLinkedBy("abcd1234"));

        // Verify note1 → note3 link still exists
        assertTrue(note1.isLinkedTo("12345678"));
        assertTrue(note3.isLinkedBy("abcd1234"));
    }

    // ==================== Edge Cases ====================

    @Test
    void execute_reverseOrderBidirectionalLink_removesSuccessfully() throws ZettelException {
        Note note1 = notes.get(0);
        Note note2 = notes.get(1);

        // Create bidirectional link
        note1.addOutgoingLink("ef567890");
        note1.addIncomingLink("ef567890");
        note2.addOutgoingLink("abcd1234");
        note2.addIncomingLink("abcd1234");

        // Call with reversed parameters
        UnlinkBothNotesCommand command = new UnlinkBothNotesCommand("ef567890", "abcd1234");
        command.execute(notes, tags, ui, storage);

        // Verify all links are removed
        assertFalse(note1.isLinkedTo("ef567890"));
        assertFalse(note1.isLinkedBy("ef567890"));
        assertFalse(note2.isLinkedTo("abcd1234"));
        assertFalse(note2.isLinkedBy("abcd1234"));
    }

    @Test
    void execute_unlinkThenUnlinkAgain_throwsException() throws ZettelException {
        Note note1 = notes.get(0);
        Note note2 = notes.get(1);

        // Create bidirectional link
        note1.addOutgoingLink("ef567890");
        note1.addIncomingLink("ef567890");
        note2.addOutgoingLink("abcd1234");
        note2.addIncomingLink("abcd1234");

        // First unlink
        UnlinkBothNotesCommand command1 = new UnlinkBothNotesCommand("abcd1234", "ef567890");
        command1.execute(notes, tags, ui, storage);

        // Try to unlink again
        UnlinkBothNotesCommand command2 = new UnlinkBothNotesCommand("abcd1234", "ef567890");

        NotesAlreadyUnlinkedException exception = assertThrows(NotesAlreadyUnlinkedException.class, () -> {
            command2.execute(notes, tags, ui, storage);
        });

        assertEquals("Any link between note 'abcd1234' and note 'ef567890' does not exist. Nothing to unlink.",
                exception.getMessage());
    }

    @Test
    void execute_multipleLinksSequentially_allRemoved() throws ZettelException {
        Note note1 = notes.get(0);
        Note note2 = notes.get(1);
        Note note3 = notes.get(2);

        // Create bidirectional links: note1 ↔ note2 and note1 ↔ note3
        note1.addOutgoingLink("ef567890");
        note1.addIncomingLink("ef567890");
        note2.addOutgoingLink("abcd1234");
        note2.addIncomingLink("abcd1234");

        note1.addOutgoingLink("12345678");
        note1.addIncomingLink("12345678");
        note3.addOutgoingLink("abcd1234");
        note3.addIncomingLink("abcd1234");

        // Unlink note1 ↔ note2
        UnlinkBothNotesCommand command1 = new UnlinkBothNotesCommand("abcd1234", "ef567890");
        command1.execute(notes, tags, ui, storage);

        // Verify note1 ↔ note2 removed, note1 ↔ note3 still exists
        assertFalse(note1.isLinkedTo("ef567890"));
        assertTrue(note1.isLinkedTo("12345678"));

        // Unlink note1 ↔ note3
        UnlinkBothNotesCommand command2 = new UnlinkBothNotesCommand("abcd1234", "12345678");
        command2.execute(notes, tags, ui, storage);

        // Verify all links removed
        assertEquals(0, note1.getOutgoingLinks().size());
        assertEquals(0, note1.getIncomingLinks().size());
    }

    @Test
    void execute_partialUnidirectionalLink_removesOnlyExistingDirection() throws ZettelException {
        Note note1 = notes.get(0);
        Note note2 = notes.get(1);

        // Create only forward link (missing incoming on note1 and outgoing on note2 for bidirectional)
        note1.addOutgoingLink("ef567890");
        note2.addIncomingLink("abcd1234");

        UnlinkBothNotesCommand command = new UnlinkBothNotesCommand("abcd1234", "ef567890");
        command.execute(notes, tags, ui, storage);

        // Verify link is removed (should not crash even if some directions don't exist)
        assertFalse(note1.isLinkedTo("ef567890"));
        assertFalse(note2.isLinkedBy("abcd1234"));
        assertEquals(0, note1.getOutgoingLinks().size());
        assertEquals(0, note2.getIncomingLinks().size());
    }

    @Test
    void execute_linkCountVerification_correctNumberRemoved() throws ZettelException {
        Note note1 = notes.get(0);
        Note note2 = notes.get(1);
        Note note3 = notes.get(2);

        // Create multiple links
        note1.addOutgoingLink("ef567890");
        note1.addIncomingLink("ef567890");
        note2.addOutgoingLink("abcd1234");
        note2.addIncomingLink("abcd1234");

        note1.addOutgoingLink("12345678");
        note3.addIncomingLink("abcd1234");

        // Note1 has 2 outgoing and 1 incoming
        assertEquals(2, note1.getOutgoingLinks().size());
        assertEquals(1, note1.getIncomingLinks().size());

        UnlinkBothNotesCommand command = new UnlinkBothNotesCommand("abcd1234", "ef567890");
        command.execute(notes, tags, ui, storage);

        // After removing bidirectional link with note2, note1 should have 1 outgoing and 0 incoming
        assertEquals(1, note1.getOutgoingLinks().size());
        assertEquals(0, note1.getIncomingLinks().size());
        assertTrue(note1.isLinkedTo("12345678")); // Link to note3 still exists
    }
}
