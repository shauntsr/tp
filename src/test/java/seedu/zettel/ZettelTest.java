package seedu.zettel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * JUnit tests for the Zettel main class.
 */
class ZettelTest {
    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStream;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // Redirect System.out to capture output
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        // Restore original System.in and System.out
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    /**
     * Helper method to simulate user input.
     */
    private void provideInput(String input) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);
    }

    /**
     * Helper method to get the captured output.
     */
    private String getOutput() {
        return outputStream.toString();
    }

    @Test
    void testZettelConstructor() {
        Zettel zettel = new Zettel();
        assertNotNull(zettel);
    }

    @Test
    void testZettelShowsWelcomeMessage() {
        provideInput("bye\n");

        Zettel zettel = new Zettel();
        zettel.run();

        String output = getOutput();
        assertTrue(output.contains("Hello! I'm Zettel"));
        assertTrue(output.contains("What can I do for you?"));
    }

    @Test
    void testZettelShowsAvailableCommands() {
        provideInput("bye\n");

        Zettel zettel = new Zettel();
        zettel.run();

        String output = getOutput();
        assertTrue(output.contains("Available Commands:"));
        assertTrue(output.contains("init"));
        assertTrue(output.contains("new"));
        assertTrue(output.contains("list"));
        assertTrue(output.contains("delete"));
        assertTrue(output.contains("pin"));
        assertTrue(output.contains("unpin"));
        assertTrue(output.contains("find"));
        assertTrue(output.contains("bye"));
    }

    @Test
    void testZettelExitsOnByeCommand() {
        provideInput("bye\n");

        Zettel zettel = new Zettel();
        zettel.run();

        String output = getOutput();
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void testZettelHandlesEmptyInput() {
        provideInput("\n\n\nbye\n");

        Zettel zettel = new Zettel();
        zettel.run();

        String output = getOutput();
        // Should skip empty lines and still show welcome and goodbye
        assertTrue(output.contains("Hello! I'm Zettel"));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void testZettelHandlesInvalidCommand() {
        provideInput("invalidcommand\nbye\n");

        Zettel zettel = new Zettel();
        zettel.run();

        String output = getOutput();
        // Should show error but continue running
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void testZettelHandlesMultipleCommands() {
        provideInput("list\nlist -p\nbye\n");

        Zettel zettel = new Zettel();
        zettel.run();

        String output = getOutput();
        assertTrue(output.contains("Hello! I'm Zettel"));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void testZettelHandlesListCommand() {
        provideInput("list\nbye\n");

        Zettel zettel = new Zettel();
        zettel.run();

        String output = getOutput();
        // Should execute list command without errors
        assertTrue(output.contains("Hello! I'm Zettel"));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void testZettelHandlesListPinnedCommand() {
        provideInput("list -p\nbye\n");

        Zettel zettel = new Zettel();
        zettel.run();

        String output = getOutput();
        // Should execute list -p command
        assertTrue(output.contains("Hello! I'm Zettel"));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void testZettelHandlesInvalidFormatError() {
        provideInput("new\nbye\n");

        Zettel zettel = new Zettel();
        zettel.run();

        String output = getOutput();
        // Should show format error and continue
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void testZettelHandlesDeleteCommandWithoutID() {
        provideInput("delete\nbye\n");

        Zettel zettel = new Zettel();
        zettel.run();

        String output = getOutput();
        // Should show error about missing ID and continue
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void testMainMethodRuns() {
        provideInput("bye\n");

        // Test that main method can be called without exception
        Zettel.main(new String[]{});

        String output = getOutput();
        assertTrue(output.contains("Hello! I'm Zettel"));
    }

    @Test
    void testZettelClosesUIOnExit() {
        provideInput("bye\n");

        Zettel zettel = new Zettel();
        zettel.run();

        // If run completes without exception, UI was closed properly
        String output = getOutput();
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void testZettelHandlesExceptionGracefully() {
        // Test with malformed command that should trigger exception
        provideInput("pin\nbye\n");

        Zettel zettel = new Zettel();
        zettel.run();

        String output = getOutput();
        // Should recover from error and continue
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void testZettelLoadsStorageOnStartup() {
        provideInput("bye\n");

        Zettel zettel = new Zettel();
        assertNotNull(zettel);

        zettel.run();

        String output = getOutput();
        assertTrue(output.contains("Hello! I'm Zettel"));
    }

    @Test
    void testZettelHandlesSequentialCommands() {
        provideInput("list\nlist -p\nfind test\nbye\n");

        Zettel zettel = new Zettel();
        zettel.run();

        String output = getOutput();
        assertTrue(output.contains("Hello! I'm Zettel"));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void testZettelContinuesAfterError() {
        provideInput("invalid\nlist\nbye\n");

        Zettel zettel = new Zettel();
        zettel.run();

        String output = getOutput();
        // Should show error but continue to process list command
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
    }
}
