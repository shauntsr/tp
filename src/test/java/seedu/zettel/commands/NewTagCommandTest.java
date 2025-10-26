package seedu.zettel.commands;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidFormatException;
import seedu.zettel.exceptions.TagExistsException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NewTagCommandTest {
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
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOutputStream);
    }
    @Test
    void testAddNewTagSuccessfully() throws ZettelException {
        String newTag = "urgent";
        NewTagCommand cmd = new NewTagCommand(newTag);

        cmd.execute(notes, tags, ui, storage);

        assertEquals(1, tags.size(), "Tag list should contain one tag");
        assertEquals(newTag, tags.get(0), "Tag should match the one added");

        String output = outputStream.toString();
        assertTrue(output.contains(newTag), "UI output should mention the added tag");
    }

    @Test
    void testAddingDuplicateTagThrowsException() throws ZettelException {
        String newTag = "important";
        tags.add(newTag);

        NewTagCommand cmd = new NewTagCommand(newTag);

        ZettelException e = assertThrows(TagExistsException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals(newTag + " already exists.", e.getMessage(),
                "Exception message should indicate tag already exists");
    }

    @Test
    void testAddingEmptyTagThrowsException() {
        String newTag = "   ";
        NewTagCommand cmd = new NewTagCommand(newTag);

        ZettelException e = assertThrows(InvalidFormatException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals("No tag provided to add.", e.getMessage(),
                "Exception message should indicate no tag provided");
    }
}
