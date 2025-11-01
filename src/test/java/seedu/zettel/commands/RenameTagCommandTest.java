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
import seedu.zettel.exceptions.InvalidFormatException;
import seedu.zettel.exceptions.TagAlreadyExistsException;
import seedu.zettel.exceptions.TagNotFoundException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

public class RenameTagCommandTest {
    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private ArrayList<Note> notes;
    private List<String> tags;
    private UI ui;
    private Storage storage;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));

        notes = new ArrayList<>();
        tags = new ArrayList<>();
        ui = new UI();
        storage = new Storage(tempDir.toString());
        storage.init();

        // Notes
        Note n1 = new Note("abcd1234", "First", "first.txt", "body1", Instant.now(), Instant.now());
        n1.addTag("java");
        n1.addTag("common");
        Note n2 = new Note("ef567890", "Second", "second.txt", "body2", Instant.now(), Instant.now());
        n2.addTag("python");
        n2.addTag("common");
        notes.add(n1);
        notes.add(n2);

        // Global tags
        tags.add("java");
        tags.add("python");
        tags.add("common");
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testSuccessfulRename_updatesGlobalAndAllNotes_andPrintsMessage() throws ZettelException {
        RenameTagCommand cmd = new RenameTagCommand("common", "shared");
        cmd.execute(notes, tags, ui, storage);

        // Global tags updated
        assertFalse(tags.contains("common"));
        assertTrue(tags.contains("shared"));
        assertEquals(2, tags.indexOf("shared")); // position preserved

        // Notes updated
        assertFalse(notes.get(0).getTags().contains("common"));
        assertTrue(notes.get(0).getTags().contains("shared"));
        assertFalse(notes.get(1).getTags().contains("common"));
        assertTrue(notes.get(1).getTags().contains("shared"));

        String out = outputStream.toString();
        assertTrue(out.contains("Tag 'common' has been renamed to 'shared'"
                + " across all notes. All affected notes have been updated."));
    }

    @Test
    void testOldTagDoesNotExist_throwsTagNotFound() {
        RenameTagCommand cmd = new RenameTagCommand("does-not-exist", "new");
        ZettelException ex = assertThrows(TagNotFoundException.class, () -> cmd.execute(notes, tags, ui, storage));
        assertEquals("Tag 'does-not-exist' does not exist.", ex.getMessage());
    }

    @Test
    void testEmptyOldTag_throwsInvalidFormat() {
        RenameTagCommand cmd = new RenameTagCommand(" ", "new");
        ZettelException ex = assertThrows(InvalidFormatException.class, () -> cmd.execute(notes, tags, ui, storage));
        assertEquals("No tag provided to rename.", ex.getMessage());
    }

    @Test
    void testEmptyNewTag_throwsInvalidFormat() {
        RenameTagCommand cmd = new RenameTagCommand("java", " ");
        ZettelException ex = assertThrows(InvalidFormatException.class, () -> cmd.execute(notes, tags, ui, storage));
        assertEquals("No new tag name provided.", ex.getMessage());
    }

    @Test
    void testNewTagAlreadyExists_throwsTagAlreadyExists_andNoChangesApplied() {
        // Trying to rename 'java' to 'python' when 'python' already exists
        RenameTagCommand cmd = new RenameTagCommand("java", "python");
        ZettelException ex = assertThrows(TagAlreadyExistsException.class, () -> cmd.execute(notes, tags, ui, storage));
        assertEquals("Tag 'python' already exists.", ex.getMessage());

        // Ensure no changes to global tags or notes
        assertTrue(tags.contains("java"));
        assertTrue(tags.contains("python"));
        assertTrue(notes.get(0).getTags().contains("java"));
        assertTrue(notes.get(1).getTags().contains("python"));
    }

    @Test
    void testRenameToSameName_throwsTagAlreadyExists() {
        RenameTagCommand cmd = new RenameTagCommand("java", "java");
        ZettelException ex = assertThrows(TagAlreadyExistsException.class, () -> cmd.execute(notes, tags, ui, storage));
        assertEquals("Tag 'java' already exists.", ex.getMessage());
    }
}
