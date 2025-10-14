package seedu.zettel.commands;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class NewNoteCommandTest {
    private static final String FILE_PATH = "./data/zettel.txt";

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOutputStream = System.out;
    private ArrayList<Note> notes;
    private UI ui;
    private Storage storage;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
        notes = new ArrayList<>();
        ui = new UI();
        storage = new Storage(FILE_PATH);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOutputStream);
    }

    @Test
    void testAddsNewNoteAndPrintsMessage() throws ZettelException {
        String title = "Test Note";
        String body = "Test body";

        NewNoteCommand cmd = new NewNoteCommand(title, body);
        cmd.execute(notes, ui, storage); // storage ignored

        assertEquals(1, notes.size());

        Note note = notes.get(0);
        assertEquals(title, note.getTitle());
        assertEquals(body, note.getBody());
        assertEquals( "Test_Note.txt", note.getFilename());

        String output = outputStream.toString();
        assertTrue(output.contains("Note created: Test_Note.txt #1"));
    }

    @Test
    void testDuplicateFilenameThrowsException() throws ZettelException {
        String title = "Test Note";
        String body = "Body 1";

        // Add an existing note with the same filename
        Note existingNote = new Note("0", title, "Test_Note.txt",
                "Old body", Instant.now(), Instant.now());
        notes.add(existingNote);

        NewNoteCommand cmd = new NewNoteCommand(title, body);

        ZettelException e = assertThrows(ZettelException.class, () -> {
            cmd.execute(notes, ui, storage);
        });
        assertEquals("Note already exists!", e.getMessage());

        // Ensure note list unchanged
        assertEquals(1, notes.size());
    }
}
