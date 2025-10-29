package seedu.zettel.commands;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListTagsGlobalCommandTest {
    @TempDir
    Path tempDir;

    private ByteArrayOutputStream outputStream;
    private PrintStream originalOutputStream;

    private ArrayList<Note> notes;
    private List<String> tags;
    private UI ui;
    private Storage storage;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        originalOutputStream = System.out;
        System.setOut(new PrintStream(outputStream));

        notes = new ArrayList<>();
        tags = new ArrayList<>();
        ui = new UI();
        storage = new Storage(tempDir.resolve("zettel.txt").toString());
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOutputStream);
    }

    @Test
    void testNoTagsThrowsException() {
        ListTagsGlobalCommand cmd = new ListTagsGlobalCommand();

        ZettelException ex = assertThrows(ZettelException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals("There are no tags to list", ex.getMessage(),
                "Should throw correct exception message when no tags exist");
    }

    @Test
    void testDisplaysAllTagsWithNumbering() throws ZettelException {
        tags.add("work");
        tags.add("urgent");
        tags.add("ideas");

        ListTagsGlobalCommand cmd = new ListTagsGlobalCommand();
        cmd.execute(notes, tags, ui, storage);

        String output = outputStream.toString();
        String[] lines = output.strip().split("\\r?\\n");

        // Header line
        assertTrue(lines[0].contains("You have 3 tags:"), "Header should show total tag count");

        // Check numbering and tag names
        assertEquals("1. work", lines[1].strip(), "First tag should be numbered 1 and be 'work'");
        assertEquals("2. urgent", lines[2].strip(), "Second tag should be numbered 2 and be 'urgent'");
        assertEquals("3. ideas", lines[3].strip(), "Third tag should be numbered 3 and be 'ideas'");
    }

    @Test
    void testSingleTagOutputFormat() throws ZettelException {
        tags.add("todo");

        ListTagsGlobalCommand cmd = new ListTagsGlobalCommand();
        cmd.execute(notes, tags, ui, storage);

        String output = outputStream.toString().strip();
        String[] lines = output.split("\\r?\\n");

        assertEquals("You have 1 tags:", lines[0].strip(), "Header should show count 1");
        assertEquals("1. todo", lines[1].strip(), "Should display numbered tag correctly");
    }

    @Test
    void testOutputNotEmptyWhenTagsExist() throws ZettelException {
        tags.add("alpha");

        ListTagsGlobalCommand cmd = new ListTagsGlobalCommand();
        cmd.execute(notes, tags, ui, storage);

        String output = outputStream.toString().strip();
        assertFalse(output.isEmpty(), "Output should not be empty when tags exist");
    }
}
