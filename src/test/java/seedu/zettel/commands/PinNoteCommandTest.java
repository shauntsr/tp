package seedu.zettel.commands;

import java.time.Instant;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidFormatException;
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.NoNotesException;

public class PinNoteCommandTest {
    
    // ==================== Exception Tests ====================
    
    @Test
    public void testInvalidFormatTooShortException() {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("12345678", "Title", "file.txt", "Body", Instant.now(), Instant.now()));
        UI ui = new UI();
        Storage storage = new Storage("build/testdata/pinnote-test.txt");
        
        // Test with ID that's too short (only 1 character)
        PinNoteCommand command = new PinNoteCommand("1", true);
        assertThrows(InvalidFormatException.class, () -> {
            command.execute(notes, ui, storage);
        });
    }
    
    @Test
    public void testInvalidFormatTooLongException() {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("12345678", "Title", "file.txt", "Body", Instant.now(), Instant.now()));
        UI ui = new UI();
        Storage storage = new Storage("build/testdata/pinnote-test.txt");
        
        // Test with ID that's too long (more than 8 characters)
        PinNoteCommand command = new PinNoteCommand("123456789", true);
        assertThrows(InvalidFormatException.class, () -> {
            command.execute(notes, ui, storage);
        });
    }
    
    @Test
    public void testInvalidFormatSpecialCharactersException() {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("12345678", "Title", "file.txt", "Body", Instant.now(), Instant.now()));
        UI ui = new UI();
        Storage storage = new Storage("build/testdata/pinnote-test.txt");
        
        // Test with ID containing special characters
        PinNoteCommand command = new PinNoteCommand("1234-678", true);
        assertThrows(InvalidFormatException.class, () -> {
            command.execute(notes, ui, storage);
        });
    }
    
    @Test
    public void testInvalidFormatNullThrowsException() {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("12345678", "Title", "file.txt", "Body", Instant.now(), Instant.now()));
        UI ui = new UI();
        Storage storage = new Storage("build/testdata/pinnote-test.txt");
        
        // Test with null ID
        PinNoteCommand command = new PinNoteCommand(null, true);
        assertThrows(InvalidFormatException.class, () -> {
            command.execute(notes, ui, storage);
        });
    }
    
    @Test
    public void testEmptyNotesListException() {
        ArrayList<Note> notes = new ArrayList<>();
        UI ui = new UI();
        Storage storage = new Storage("build/testdata/pinnote-test.txt");
        
        // Test with empty notes list
        PinNoteCommand command = new PinNoteCommand("12345678", true);
        assertThrows(NoNotesException.class, () -> {
            command.execute(notes, ui, storage);
        });
    }
    
    @Test
    public void testNoteIdNotFoundException() {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("12345678", "Title 0", "file0.txt", "Body 0", Instant.now(), Instant.now()));
        notes.add(new Note("abcdefgh", "Title 1", "file1.txt", "Body 1", Instant.now(), Instant.now()));
        UI ui = new UI();
        Storage storage = new Storage("build/testdata/pinnote-test.txt");
        
        // Test with valid format but non-existent ID
        PinNoteCommand command = new PinNoteCommand("99999999", true);
        assertThrows(InvalidNoteIdException.class, () -> {
            command.execute(notes, ui, storage);
        });
    }
    
    // ==================== Happy Path Tests ====================
    
    @Test
    public void testValidPinNoteCommandNoteIsPinned() throws Exception {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("12345678", "Title 0", "file0.txt", "Body 0", Instant.now(), Instant.now()));
        notes.add(new Note("abcdefgh", "Title 1", "file1.txt", "Body 1", Instant.now(), Instant.now()));
        Note target = notes.get(1);
        assertFalse(target.isPinned());

        Instant before = target.getModifiedAt();
        // Ensure clock advances on platforms with coarse Instant resolution
        Thread.sleep(2);
        UI ui = new UI();
        Storage storage = new Storage("build/testdata/pinnote-test.txt");
        
        new PinNoteCommand("abcdefgh", true).execute(notes, ui, storage);
        
        assertTrue(target.isPinned());
        assertTrue(target.getModifiedAt().isAfter(before), "modifiedAt should be updated on pinning");
    }

    @Test
    void testUnpinsNoteAtValidIndexUpdatesPinnedAndModifiedAt() throws Exception {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("12345678", "Title0", "file0.txt", "Body0",
                Instant.now(), Instant.now()));
        Note target = notes.get(0);

        target.setPinned(true);
        assertTrue(target.isPinned());
        Instant before = target.getModifiedAt();
        // Ensure clock advances on platforms with coarse Instant resolution
        Thread.sleep(2);
        UI ui = new UI();
        Storage storage = new Storage("build/testdata/pinnote-test.txt");
        
        new PinNoteCommand("12345678", false).execute(notes, ui, storage);
        
        assertFalse(target.isPinned(), "Note should be unpinned");
        assertTrue(target.getModifiedAt().isAfter(before), "modifiedAt should be updated on unpin");
    }
    
    @Test
    public void testPinAlreadyPinnedNoteRemainsPinned() throws Exception {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("12345678", "Title", "file.txt", "Body", Instant.now(), Instant.now()));
        Note target = notes.get(0);
        
        target.setPinned(true);
        assertTrue(target.isPinned());
        
        UI ui = new UI();
        Storage storage = new Storage("build/testdata/pinnote-test.txt");
        
        new PinNoteCommand("12345678", true).execute(notes, ui, storage);
        
        assertTrue(target.isPinned(), "Note should still be pinned");
    }
    
    @Test
    public void testUnpinAlreadyUnpinnedNoteRemainsUnpinned() throws Exception {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("12345678", "Title", "file.txt", "Body", Instant.now(), Instant.now()));
        Note target = notes.get(0);
        
        assertFalse(target.isPinned());
        
        UI ui = new UI();
        Storage storage = new Storage("build/testdata/pinnote-test.txt");
        
        new PinNoteCommand("12345678", false).execute(notes, ui, storage);
        
        assertFalse(target.isPinned(), "Note should still be unpinned");
    }
}
