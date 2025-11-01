package seedu.zettel.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CurrentRepoCommandTest {

    private Path tempDir;
    private Storage storage;
    private UI ui;
    private ArrayList<Note> notes;
    private List<String> tags;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() throws Exception {
        tempDir = Files.createTempDirectory("zettel-current-repo-test");
        storage = new Storage(tempDir.toString());
        ui = new UI();
        notes = new ArrayList<>();
        tags = new ArrayList<>();

        // Capture UI output
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Setup repos
        storage.createRepo("main");
        storage.createRepo("secondary");
        storage.changeRepo("secondary");
    }

    @Test
    void execute_showsCorrectCurrentRepo() throws ZettelException {
        CurrentRepoCommand cmd = new CurrentRepoCommand();
        cmd.execute(notes, tags, ui, storage);

        String output = outputStream.toString();
        assertTrue(output.contains("secondary"),
                "Output should display the currently active repository name");
    }

    @Test
    void execute_noExplicitChange_defaultsToMain() throws ZettelException {
        // Reset config so main is default
        storage.changeRepo("main");

        CurrentRepoCommand cmd = new CurrentRepoCommand();
        cmd.execute(notes, tags, ui, storage);

        String output = outputStream.toString();
        assertTrue(output.contains("main"),
                "Output should show 'main' when no repo change has been made");
    }

    @Test
    void execute_doesNotThrowException() {
        CurrentRepoCommand cmd = new CurrentRepoCommand();
        assertDoesNotThrow(() -> cmd.execute(notes, tags, ui, storage),
                "Executing CurrentRepoCommand should not throw an exception");
    }
}
