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

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the HelpCommand class.
 * Tests that the command correctly prints the help message to the UI.
 */
public class HelpCommandTest {

    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOutputStream = System.out;

    private Storage storage;
    private UI ui;
    private ArrayList<Note> notes;
    private List<String> tags;

    @BeforeEach
    void setUp() {
        // Redirect System.out to capture UI output
        System.setOut(new PrintStream(outputStream));

        storage = new Storage(tempDir.toString());
        storage.init();
        ui = new UI();
        notes = new ArrayList<>();
        tags = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        // Restore original System.out
        System.setOut(originalOutputStream);
    }

    @Test
    void execute_helpCommand_printsHelpMessage() throws ZettelException {
        // Create and execute the help command
        HelpCommand command = new HelpCommand();
        command.execute(notes, tags, ui, storage);

        // Capture the output
        String output = outputStream.toString();

        // Verify that key parts of the help message are present with the new formatting
        assertTrue(output.contains("Available Commands:"),
                "Output should contain the help title");
        assertTrue(output.contains("   init <repo-name>                  - Initialize a new repository"),
                "Output should contain the init command with correct spacing");
        assertTrue(output.contains("   help                              - Show this list of commands"),
                "Output should contain the help command itself with correct spacing");
        assertTrue(output.contains("   bye                               - Exit the application"),
                "Output should contain the bye command with correct spacing");
    }
}
