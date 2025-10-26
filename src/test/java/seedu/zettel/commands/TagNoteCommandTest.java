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
import seedu.zettel.exceptions.TagAlreadyExistsException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

public class TagNoteCommandTest {
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

        Note note = new Note("abcd1234", "Sample Note", "Sample_Note.txt",
                "Body", Instant.now(), Instant.now());
        notes.add(note);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOutputStream);
    }

    @Test
    void testTagNoteSuccessfully() throws ZettelException {
        String newTag = "urgent";
        TagNoteCommand cmd = new TagNoteCommand("abcd1234", newTag);

        cmd.execute(notes, tags, ui, storage);

        Note note = notes.get(0);
        assertTrue(note.getTags().contains(newTag), "Note should have the new tag");
        assertTrue(tags.contains(newTag), "Global tags should contain the new tag");
    }

    @Test
    void testAddingDuplicateTagToNoteThrowsException() throws ZettelException {
        Note note = notes.get(0);
        note.addTag("urgent");
        tags.add("urgent");

        TagNoteCommand cmd = new TagNoteCommand("abcd1234", "urgent");

        ZettelException e = assertThrows(TagAlreadyExistsException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals("This note is already tagged with 'urgent'", e.getMessage());
    }

    @Test
    void testTaggingNonexistentNoteThrowsException() {
        TagNoteCommand cmd = new TagNoteCommand("deadbeef", "important");

        ZettelException e = assertThrows(InvalidNoteIdException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals("Invalid Note ID! Note with ID 'deadbeef' does not exist.", e.getMessage());
    }       

    @Test
    void testInvalidNoteIdThrowsException() {
        TagNoteCommand cmd = new TagNoteCommand("123", "tag");

        ZettelException e = assertThrows(InvalidFormatException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertTrue(e.getMessage().contains("Note ID must be exactly 8 characters long"));
    }
}
