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

public class UnlinkNotesCommandTest {
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

        // Pre-establish some links for testing
        note1.addOutgoingLink("ef567890");
        note2.addIncomingLink("abcd1234");

        note1.addOutgoingLink("12345678");
        note3.addIncomingLink("abcd1234");
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOutputStream);
    }

    @Test
    void execute_emptyNotesList_throwsNoNotesException() {
        ArrayList<Note> emptyNotes = new ArrayList<>();
        UnlinkNotesCommand command = new UnlinkNotesCommand("abcd1234", "ef567890");

        NoNotesException exception = assertThrows(NoNotesException.class, () -> {
            command.execute(emptyNotes, tags, ui, storage);
        });

        assertEquals("You have no notes to unlink.", exception.getMessage());
    }

    @Test
    void execute_selfUnlink_throwsNoteSelfLinkException() {
        UnlinkNotesCommand command = new UnlinkNotesCommand("abcd1234", "abcd1234");

        NoteSelfLinkException exception = assertThrows(NoteSelfLinkException.class, () -> {
            command.execute(notes, tags, ui, storage);
        });

        assertEquals("Cannot unlink a note from itself. Note ID: 'abcd1234'.", exception.getMessage());
    }

    @Test
    void execute_sourceNoteNotFound_throwsInvalidNoteIdException() {
        UnlinkNotesCommand command = new UnlinkNotesCommand("99999999", "ef567890");

        InvalidNoteIdException exception = assertThrows(InvalidNoteIdException.class, () -> {
            command.execute(notes, tags, ui, storage);
        });

        assertEquals("Invalid Note ID! Note with ID '99999999' does not exist.", exception.getMessage());
    }

    @Test
    void execute_targetNoteNotFound_throwsInvalidNoteIdException() {
        UnlinkNotesCommand command = new UnlinkNotesCommand("abcd1234", "99999999");

        InvalidNoteIdException exception = assertThrows(InvalidNoteIdException.class, () -> {
            command.execute(notes, tags, ui, storage);
        });

        assertEquals("Invalid Note ID! Note with ID '99999999' does not exist.", exception.getMessage());
    }

    @Test
    void execute_linkDoesNotExist_throwsNotesAlreadyUnlinkedException() {
        // Note2 and Note3 are not linked
        UnlinkNotesCommand command = new UnlinkNotesCommand("ef567890", "12345678");

        NotesAlreadyUnlinkedException exception = assertThrows(NotesAlreadyUnlinkedException.class, () -> {
            command.execute(notes, tags, ui, storage);
        });

        assertEquals("Link from note 'ef567890' to note '12345678' does not exist. Nothing to unlink.",
                exception.getMessage());
    }

    @Test
    void execute_successfulUnlink_removesLinkBothDirections() throws ZettelException {
        // Verify link exists before unlinking
        Note note1 = notes.get(0);
        Note note2 = notes.get(1);
        assertTrue(note1.isLinkedTo("ef567890"));
        assertTrue(note2.isLinkedBy("abcd1234"));

        UnlinkNotesCommand command = new UnlinkNotesCommand("abcd1234", "ef567890");
        command.execute(notes, tags, ui, storage);

        // Verify link is removed from both directions
        assertFalse(note1.isLinkedTo("ef567890"));
        assertFalse(note2.isLinkedBy("abcd1234"));

        String output = outputStream.toString();
        assertTrue(output.contains("The link from note #abcd1234 to note #ef567890 has been removed"));
    }

    @Test
    void execute_unlinkOneOfMultipleLinks_onlyRemovesSpecifiedLink() throws ZettelException {
        // Note1 has links to both Note2 and Note3
        Note note1 = notes.get(0);
        Note note2 = notes.get(1);
        Note note3 = notes.get(2);

        // Verify both links exist
        assertTrue(note1.isLinkedTo("ef567890"));
        assertTrue(note1.isLinkedTo("12345678"));

        // Unlink only Note1 -> Note2
        UnlinkNotesCommand command = new UnlinkNotesCommand("abcd1234", "ef567890");
        command.execute(notes, tags, ui, storage);

        // Verify Note1 -> Note2 is removed
        assertFalse(note1.isLinkedTo("ef567890"));
        assertFalse(note2.isLinkedBy("abcd1234"));

        // Verify Note1 -> Note3 still exists
        assertTrue(note1.isLinkedTo("12345678"));
        assertTrue(note3.isLinkedBy("abcd1234"));
    }

    @Test
    void execute_unlinkAlreadyUnlinkedNotes_throwsNotesAlreadyUnlinkedException() throws ZettelException {
        // First unlink
        UnlinkNotesCommand command1 = new UnlinkNotesCommand("abcd1234", "ef567890");
        command1.execute(notes, tags, ui, storage);

        // Try to unlink again
        UnlinkNotesCommand command2 = new UnlinkNotesCommand("abcd1234", "ef567890");

        NotesAlreadyUnlinkedException exception = assertThrows(NotesAlreadyUnlinkedException.class, () -> {
            command2.execute(notes, tags, ui, storage);
        });

        assertEquals("Link from note 'abcd1234' to note 'ef567890' does not exist. Nothing to unlink.",
                exception.getMessage());
    }

    @Test
    void execute_unlinkMultipleLinksSequentially_allLinksRemoved() throws ZettelException {
        Note note1 = notes.get(0);

        // Verify both links exist
        assertTrue(note1.isLinkedTo("ef567890"));
        assertTrue(note1.isLinkedTo("12345678"));

        // Unlink first link
        UnlinkNotesCommand command1 = new UnlinkNotesCommand("abcd1234", "ef567890");
        command1.execute(notes, tags, ui, storage);

        assertFalse(note1.isLinkedTo("ef567890"));
        assertTrue(note1.isLinkedTo("12345678"));

        // Unlink second link
        UnlinkNotesCommand command2 = new UnlinkNotesCommand("abcd1234", "12345678");
        command2.execute(notes, tags, ui, storage);

        assertFalse(note1.isLinkedTo("ef567890"));
        assertFalse(note1.isLinkedTo("12345678"));
    }

    @Test
    void execute_unlinkWithNoOtherLinks_notesHaveEmptyLinkSets() throws ZettelException {
        // Create two notes with only one link between them
        ArrayList<Note> twoNotes = new ArrayList<>();
        Note noteA = new Note("aaaaaaaa", "Note A", "Note_A.txt",
                "Body A", Instant.now(), Instant.now());
        Note noteB = new Note("bbbbbbbb", "Note B", "Note_B.txt",
                "Body B", Instant.now(), Instant.now());

        noteA.addOutgoingLink("bbbbbbbb");
        noteB.addIncomingLink("aaaaaaaa");

        twoNotes.add(noteA);
        twoNotes.add(noteB);

        UnlinkNotesCommand command = new UnlinkNotesCommand("aaaaaaaa", "bbbbbbbb");
        command.execute(twoNotes, tags, ui, storage);

        // Verify both notes have no links
        assertEquals(0, noteA.getOutgoingLinks().size());
        assertEquals(0, noteA.getIncomingLinks().size());
        assertEquals(0, noteB.getOutgoingLinks().size());
        assertEquals(0, noteB.getIncomingLinks().size());
    }

    @Test
    void execute_unlinkPreservesOtherNoteLinks_onlyAffectsSpecifiedNotes() throws ZettelException {
        // Note1 -> Note2 and Note1 -> Note3
        // We'll unlink Note1 -> Note2 and verify Note3's incoming link is preserved
        Note note3 = notes.get(2);

        UnlinkNotesCommand command = new UnlinkNotesCommand("abcd1234", "ef567890");
        command.execute(notes, tags, ui, storage);

        // Verify Note3 still has incoming link from Note1
        assertTrue(note3.isLinkedBy("abcd1234"));
    }
}
