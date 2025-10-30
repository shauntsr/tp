package seedu.zettel.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import seedu.zettel.exceptions.ZettelException;

public class FileSystemManagerTest {

    @TempDir
    private Path tempDir;

    private FileSystemManager fs;

    @BeforeEach
    void setup() {
        fs = new FileSystemManager(tempDir.toString());
    }

    @Test
    void testCreateRootAndConfigAndRepoStructure() throws IOException {
        Path configPath = tempDir.resolve(".zettelConfig");
        Path mainNotes = tempDir.resolve("main").resolve("notes");
        Path mainArchive = tempDir.resolve("main").resolve("archive");
        Path mainIndex = tempDir.resolve("main").resolve("index.txt");

        // Before: nothing exists
        assertFalse(Files.exists(configPath));
        assertFalse(Files.exists(mainNotes));

        // Act: replicate Storage.init sequence
        fs.createRootFolder();
        fs.createConfigFile("main");
        boolean created = fs.createRepoStructure("main");

        // Assert config created and repo created
        assertTrue(Files.exists(configPath));
        List<String> cfgLines = Files.readAllLines(configPath);
        // createConfigFile wrote two lines: "main", "main"
        assertEquals("main", cfgLines.get(0));
        assertEquals("main", cfgLines.get(1));

        assertTrue(created);
        assertTrue(Files.exists(mainNotes));
        assertTrue(Files.exists(mainArchive));
        assertTrue(Files.exists(mainIndex));
    }

    @Test
    void testCreateNoteFileWritesIntoRepoNotes() throws IOException {
        // Prepare repo
        fs.createRootFolder();
        fs.createConfigFile("main");
        fs.createRepoStructure("main");

        // Act: create a note file through FileSystemManager
        fs.createNoteFile("note1.txt", "Hello Body", "main");

        // Assert: note file exists under main/notes/note1.txt and content matches
        Path notePath = tempDir.resolve("main").resolve("notes").resolve("note1.txt");
        assertTrue(Files.exists(notePath));
        assertEquals("Hello Body", Files.readString(notePath));
    }

    @Test
    void testValidateRepoStructureCreatesMissingIndexAndBodyFiles() throws IOException, ZettelException {
        // Setup minimal repo (simulate index with an entry and expect body file)
        fs.createRootFolder();
        fs.createConfigFile("main");
        fs.createRepoStructure("main");

        // Write index with an expected filename
        Path index = tempDir.resolve("main").resolve("index.txt");
        Files.writeString(index, "x | X | x.txt | 2025-10-24T00:00:00Z | 2025-10-24T00:00:00Z | 0 | 0 |  | \n");

        // NEW: expectedFilesMap: filename -> isArchived (false => expected in notes/)
        Map<String, Boolean> expectedFilesMap = Map.of("x.txt", false);
        fs.validateRepoStructure("main", expectedFilesMap);

        // After validation, the body file should exist in notes/
        Path body = tempDir.resolve("main").resolve("notes").resolve("x.txt");
        assertTrue(Files.exists(body));
    }

    @Test
    void testGetPathsReturnCorrectPaths() {
        // fs was constructed with root tempDir; just assert path getters
        Path expectedIndex = tempDir.resolve(".zettelConfig"); // config path
        assertEquals(tempDir.resolve(".zettelConfig"), fs.getConfigPath());

        // getIndexPath/getNotesPath require a repoName argument in your Storage usage;
        // assert those helper methods produce expected paths for a repoName string:
        assertEquals(tempDir.resolve("myrepo").resolve("index.txt"), fs.getIndexPath("myrepo"));
        assertEquals(tempDir.resolve("myrepo").resolve("notes"), fs.getNotesPath("myrepo"));
    }
}
