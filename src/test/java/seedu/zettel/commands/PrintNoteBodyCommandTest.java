package seedu.zettel.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.NoteBodyEmptyException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

public class PrintNoteBodyCommandTest {

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
	void execute_emptyNotesList_throwsNoNotesException() {
		PrintNoteBodyCommand cmd = new PrintNoteBodyCommand("abcd1234");
		ZettelException e = assertThrows(NoNotesException.class, () -> cmd.execute(notes, tags, ui, storage));
		assertEquals("You have no notes to pin/unpin.", e.getMessage());
	}

	@Test
	void execute_noteNotFound_throwsInvalidNoteIdException() {
		// add a different note so list not empty
		notes.add(new Note("aaaaaaaa", "Title", "Title.txt", "Body", Instant.now(), Instant.now()));
		PrintNoteBodyCommand cmd = new PrintNoteBodyCommand("deadbeef");
	ZettelException e = assertThrows(InvalidNoteIdException.class, () -> cmd.execute(notes, tags, ui, storage));
	assertTrue(e.getMessage().contains("Note with ID 'deadbeef' does not exist."));
	}

	@Test
	void execute_emptyBody_throwsNoteBodyEmptyException() {
		notes.add(new Note("abcd1234", "Title", "Title.txt", "", Instant.now(), Instant.now()));
		PrintNoteBodyCommand cmd = new PrintNoteBodyCommand("abcd1234");
		ZettelException e = assertThrows(NoteBodyEmptyException.class, () -> cmd.execute(notes, tags, ui, storage));
		assertEquals("The body of note with ID 'abcd1234' is empty.", e.getMessage());
	}

	@Test
	void execute_success_printsBody() throws ZettelException {
		notes.add(new Note("abcd1234", "Title", "Title.txt", "Hello World", Instant.now(), Instant.now()));
		PrintNoteBodyCommand cmd = new PrintNoteBodyCommand("abcd1234");
		cmd.execute(notes, tags, ui, storage);

		String output = outputStream.toString();
		assertTrue(output.contains(" Body of note #abcd1234:"));
		assertTrue(output.contains("Hello World"));
	}
}
