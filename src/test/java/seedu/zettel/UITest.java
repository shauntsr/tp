package seedu.zettel;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UITest {
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private UI ui;

    @BeforeEach
    void setUp() {
        // Capture all printed output
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        ui = new UI(); // uses the no-arg constructor
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        ui.close(); // make sure scanner is closed after tests
    }

    @Test
    void testPrintLineOutputsSeparator() {
        ui.printLine();
        String output = outputStream.toString().trim();
        assertEquals("____________________________________________________________", output);
    }

    @Test
    void testShowWelcomeDisplaysGreetingAndCommands() {
        ui.showWelcome();
        String output = outputStream.toString();

        assertAll(
                () -> assertTrue(output.contains("Hello! I'm Zettel")),
                () -> assertTrue(output.contains("Available Commands:")),
                () -> assertTrue(output.contains("init <repo-name>")),
                () -> assertTrue(output.contains("bye")),
                () -> assertTrue(output.contains("____________________________________________________________"))
        );
    }

    @Test
    void testShowNoteSavedFromEditorPrintsMessage() {
        ui.showNoteSavedFromEditor();
        String output = outputStream.toString().trim();
        assertTrue(output.contains("Note body saved from editor."));
    }

    @Test
    void testShowNoteEditedDisplaysNoteInfo() {
        Instant now = Instant.now();
        Note note = new Note(
                "abcd1234",
                "Test Title",
                "testNote.txt",
                "Sample body",
                now,
                now
        );

        ui.showNoteEdited(note);
        String output = outputStream.toString().trim();
        assertTrue(output.contains("Successfully edited note: testNote.txt, id: abcd1234"));
    }

    @Test
    void testCloseDoesNotThrowException() {
        assertDoesNotThrow(() -> ui.close());
    }
}
