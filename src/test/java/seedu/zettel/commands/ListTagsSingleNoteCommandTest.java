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
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.NoTagsException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

//
/**
 * JUnit test class for {@link ListTagsIndividualNoteCommand}.
 * Tests all execution paths including exception cases and successful tag listing.
 */
public class ListTagsIndividualNoteCommandTest {
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
        
        // Add tags to some notes
        note1.addTag("java");
        note1.addTag("programming");
        note2.addTag("testing");
        // note3 has no tags
        
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
    void testEmptyNotesListThrowsNoNotesException() {
        ArrayList<Note> emptyNotes = new ArrayList<>();
        ListTagsIndividualNoteCommand cmd = new ListTagsIndividualNoteCommand("abcd1234");

        NoNotesException ex = assertThrows(NoNotesException.class, () -> {
            cmd.execute(emptyNotes, tags, ui, storage);
        });

        assertEquals("You have no notes to list tags of.", ex.getMessage());
    }

    @Test
    void testNoteIdNotFoundThrowsInvalidNoteIdException() {
        ListTagsIndividualNoteCommand cmd = new ListTagsIndividualNoteCommand("deadbeef");

        InvalidNoteIdException ex = assertThrows(InvalidNoteIdException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals("Invalid Note ID! Note with ID 'deadbeef' does not exist.", ex.getMessage());
    }

    @Test
    void testNoteWithNoTagsThrowsNoTagsException() {
        // Note with ID "12345678" has no tags
        ListTagsIndividualNoteCommand cmd = new ListTagsIndividualNoteCommand("12345678");

        NoTagsException ex = assertThrows(NoTagsException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals("No tags are tagged to note with ID '12345678'.", ex.getMessage());
    }

    // ==================== Success Tests ====================

    @Test
    void testSuccessfullyListSingleTag() throws ZettelException {
        // Note with ID "ef567890" has one tag: "testing"
        ListTagsIndividualNoteCommand cmd = new ListTagsIndividualNoteCommand("ef567890");
        cmd.execute(notes, tags, ui, storage);

        String output = outputStream.toString();
        assertTrue(output.contains("Tags for note #ef567890:"));
        assertTrue(output.contains("testing"));
    }

    @Test
    void testSuccessfullyListMultipleTags() throws ZettelException {
        // Note with ID "abcd1234" has two tags: "java" and "programming"
        ListTagsIndividualNoteCommand cmd = new ListTagsIndividualNoteCommand("abcd1234");
        cmd.execute(notes, tags, ui, storage);

        String output = outputStream.toString();
        assertTrue(output.contains("Tags for note #abcd1234:"));
        assertTrue(output.contains("java"));
        assertTrue(output.contains("programming"));
    }

    @Test
    void testCorrectNoteTagsDisplayedNotGlobalTags() throws ZettelException {
        // Add some global tags that shouldn't be displayed
        tags.add("global-tag-1");
        tags.add("global-tag-2");
        
        // Note with ID "ef567890" has only "testing" tag
        ListTagsIndividualNoteCommand cmd = new ListTagsIndividualNoteCommand("ef567890");
        cmd.execute(notes, tags, ui, storage);

        String output = outputStream.toString();
        assertTrue(output.contains("testing"));
        // Should NOT contain global tags
        assertTrue(!output.contains("global-tag-1"));
        assertTrue(!output.contains("global-tag-2"));
    }

    @Test
    void testListTagsFromFirstNoteInList() throws ZettelException {
        // Test with the first note in the list
        ListTagsIndividualNoteCommand cmd = new ListTagsIndividualNoteCommand("abcd1234");
        cmd.execute(notes, tags, ui, storage);

        String output = outputStream.toString();
        assertTrue(output.contains("Tags for note #abcd1234:"));
        assertTrue(output.contains("java"));
        assertTrue(output.contains("programming"));
    }

    @Test
    void testListTagsFromMiddleNoteInList() throws ZettelException {
        // Test with a note in the middle of the list
        ListTagsIndividualNoteCommand cmd = new ListTagsIndividualNoteCommand("ef567890");
        cmd.execute(notes, tags, ui, storage);

        String output = outputStream.toString();
        assertTrue(output.contains("Tags for note #ef567890:"));
        assertTrue(output.contains("testing"));
    }

    @Test
    void testNoteIdCaseSensitivity() {
        // Note IDs should be case-sensitive (lowercase only valid)
        // This test verifies that "ABCD1234" doesn't match "abcd1234"
        ListTagsIndividualNoteCommand cmd = new ListTagsIndividualNoteCommand("ABCD1234");

        InvalidNoteIdException ex = assertThrows(InvalidNoteIdException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals("Invalid Note ID! Note with ID 'ABCD1234' does not exist.", ex.getMessage());
    }
}
//@@author

