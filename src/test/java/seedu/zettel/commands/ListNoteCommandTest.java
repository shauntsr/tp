package seedu.zettel.commands;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.Note;
import seedu.zettel.storage.Storage;
import seedu.zettel.UI;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListNoteCommandTest {
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
    void testPrintNotesNumberedAndOrdered() throws ZettelException {
        Instant now = Instant.now();
        Instant earlier = now.minusSeconds(3600);

        Note older = new Note("1", "Old Note", "old.txt", "Body A", earlier, earlier);
        Note newer = new Note("2", "New Note", "new.txt", "Body B", now, now);

        notes.add(older);
        notes.add(newer);

        ListNoteCommand cmd = new ListNoteCommand(false);
        cmd.execute(notes, ui, storage);

        String output = outputStream.toString();

        String[] lines = output.split("\n");

        // Skip lines[0] since it is the header
        // Check numbering and order
        assertTrue(lines[1].strip().startsWith("1."), "First line should start with '1.'");
        assertTrue(lines[1].strip().contains("new.txt"), "First note should be the newest");

        assertTrue(lines[2].strip().startsWith("2."), "Second line should start with '2.'");
        assertTrue(lines[2].strip().contains("old.txt"), "Second note should be the older one");
    }
    @Test
    void testPrintNoNotes() throws ZettelException {
        ListNoteCommand cmd = new ListNoteCommand(false);
        cmd.execute(notes, ui, storage);

        String output = outputStream.toString();
        assertTrue(output.contains("No notes found"),
                "Should print message for no notes found");
    }

    @Test
    void testPrintNoPinnedNotes() throws ZettelException {
        ListNoteCommand cmd = new ListNoteCommand(true);
        cmd.execute(notes, ui, storage);

        String output = outputStream.toString();
        assertTrue(output.contains("No pinned notes found. Pin a note to add to this list."),
                "Should print message for no pinned notes found");
    }

    @Test
    void testPrintsFilteredNotes() throws ZettelException {
        Instant now = Instant.now();
        Instant earlier = now.minusSeconds(3600);

        Note pinned = new Note("1", "Pinned Note", "file1.txt", "content",
                now, now);
        Note unpinned = new Note("2", "Unpinned Note", "file2.txt", "content",
                earlier, earlier);

        pinned.setPinned(true);
        unpinned.setPinned(false);

        notes.add(pinned);
        notes.add(unpinned);

        ListNoteCommand cmd = new ListNoteCommand(true);
        cmd.execute(notes, ui, storage);

        String output = outputStream.toString();
        assertTrue(output.contains("You have 1 pinned notes:"),
                "Should count only pinned notes");
        assertTrue(output.contains("file1.txt"),
                "Should show pinned note");
        assertFalse(output.contains("file2.txt"),
                "Should not show unpinned note");
    }

    @Test
    void testPrintsNotesSorted() throws ZettelException {
        Instant now = Instant.now();
        Instant earlier = now.minusSeconds(3600);

        Note older = new Note("1", "Old", "old.txt", "Body A", earlier, earlier);
        Note newer = new Note("2", "New", "new.txt", "Body B", now, now);
        notes.add(older);
        notes.add(newer);

        ListNoteCommand cmd = new ListNoteCommand(false);
        cmd.execute(notes, ui, storage);

        String output = outputStream.toString();
        assertTrue(output.contains("You have 2 notes"),
                "Should show correct number of notes");

        // Check that newer note appears before older one in output
        int idxNew = output.indexOf("new.txt");
        int idxOld = output.indexOf("old.txt");
        assertTrue(idxNew < idxOld,
                "Newer note should appear before older one (sorted descending by createdAt)");
    }
}
