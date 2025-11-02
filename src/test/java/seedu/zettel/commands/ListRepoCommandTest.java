package seedu.zettel.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class ListRepoCommandTest {

    private Path tempDir;
    private Storage storage;
    private UI ui;
    private ArrayList<Note> notes;
    private List<String> tags;

    @BeforeEach
    void setUp() throws Exception {
        tempDir = Files.createTempDirectory("zettel-list-repo-test");
        storage = new Storage(tempDir.toString());
        ui = new UI();
        notes = new ArrayList<>();
        tags = new ArrayList<>();

        storage.createRepo("main");
        storage.createRepo("projectX");
        storage.createRepo("archive");
        storage.loadConfig();
    }

    @Test
    void execute_multipleRepos_listsAllRepos() throws ZettelException {
        ListRepoCommand cmd = new ListRepoCommand();

        cmd.execute(notes, tags, ui, storage);

        ArrayList<String> repos = storage.getRepoList();
        assertTrue(repos.contains("main"), "Should contain main repo");
        assertTrue(repos.contains("projectX"), "Should contain projectX repo");
        assertTrue(repos.contains("archive"), "Should contain archive repo");
    }

    @Test
    void execute_singleRepo_listsRepo() throws ZettelException, IOException {
        Path singleDir = Files.createTempDirectory("zettel-single-test");
        Storage singleStorage = new Storage(singleDir.toString());
        singleStorage.createRepo("main");
        singleStorage.loadConfig();

        ListRepoCommand cmd = new ListRepoCommand();

        cmd.execute(notes, tags, ui, singleStorage);

        ArrayList<String> repos = singleStorage.getRepoList();
        assertTrue(repos.contains("main"), "Should contain main repo");
    }
}
