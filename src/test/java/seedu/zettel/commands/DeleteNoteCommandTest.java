package seedu.zettel.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.util.ArrayList;

import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeleteNoteCommandTest {

    private static final String FILE_PATH = "./data/zettel.txt";

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach // set up environment for test
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach // reset environment after test
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(System.in);
    }

    @Test // test deleting an existing note with force
    public void testDeleteNoteWithForce() {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("1", "Note One", "note1.txt", "Body 1", Instant.now(), Instant.now()));
        notes.add(new Note("2", "Note Two", "note2.txt", "Body 2", Instant.now(), Instant.now()));

        DeleteNoteCommand cmd = new DeleteNoteCommand("1", true);
        cmd.execute(notes, new UI(), new Storage(FILE_PATH));

        String output = outContent.toString();
        assertTrue(output.contains("note at 1 deleted"));
        assertEquals(1, notes.size());
    }

    @Test // test deleting a non-existing note
    public void testDeleteNonExistingNote() {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("1", "Note One", "note1.txt", "Body 1", Instant.now(), Instant.now()));

        DeleteNoteCommand cmd = new DeleteNoteCommand("3", true);
        cmd.execute(notes, new UI(), new Storage(FILE_PATH));

        String output = outContent.toString();
        assertTrue(output.contains("No note found with id 3"));
        assertEquals(1, notes.size());
    }

    @Test // test deleting a note without force, user confirms
    public void testDeleteNoteUserConfirms() {
        String userInput = "y\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("1", "Note One", "note1.txt", "Body 1", Instant.now(), Instant.now()));
        notes.add(new Note("2", "Note Two", "note2.txt", "Body 2", Instant.now(), Instant.now()));

        DeleteNoteCommand cmd = new DeleteNoteCommand("2", false);
        cmd.execute(notes, new UI(), new Storage(FILE_PATH));

        String output = outContent.toString();
        assertTrue(output.contains("note at 2 deleted"));
        assertEquals(1, notes.size());
    }

    @Test // test deleting a note without force, user cancels
    public void testDeleteNoteUserCancels() {
        String userInput = "n\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("1", "Note One", "note1.txt", "Body 1", Instant.now(), Instant.now()));
        notes.add(new Note("2", "Note Two", "note2.txt", "Body 2", Instant.now(), Instant.now()));

        DeleteNoteCommand cmd = new DeleteNoteCommand("1", false);
        cmd.execute(notes, new UI(), new Storage(FILE_PATH));

        String output = outContent.toString();
        assertTrue(output.contains("Deletion cancelled"));
        assertEquals(2, notes.size());
    }
}
