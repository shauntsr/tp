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

        fs.validateRepoStructure("main", List.of("x.txt"));

        // After validation, the body file should exist
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

    @Test
    void testCreateConfigFileReconstructsFromSingleExistingRepo() throws IOException {
        // Setup: create a valid repo without config file
        fs.createRootFolder();
        fs.createRepoStructure("main");

        Path configPath = tempDir.resolve(".zettelConfig");
        assertFalse(Files.exists(configPath));

        // Act: create config file - should reconstruct from "main" repo
        fs.createConfigFile("default");

        // Assert: config file created with "main" (not "default")
        assertTrue(Files.exists(configPath));
        List<String> cfgLines = Files.readAllLines(configPath);
        assertEquals(2, cfgLines.size());
        assertEquals("main", cfgLines.get(0), "Current repo should be 'main'");
        assertEquals("main", cfgLines.get(1), "Default repo should be 'main'");
    }

    @Test
    void testCreateConfigFileReconstructsFromMultipleExistingRepos() throws IOException {
        // Setup: create multiple valid repos
        fs.createRootFolder();
        fs.createRepoStructure("main");
        fs.createRepoStructure("main1");
        fs.createRepoStructure("project");

        Path configPath = tempDir.resolve(".zettelConfig");
        assertFalse(Files.exists(configPath));

        // Act: create config file - should use first found repo
        fs.createConfigFile("default");

        // Assert: config file created with one of the existing repos
        assertTrue(Files.exists(configPath));
        List<String> cfgLines = Files.readAllLines(configPath);
        assertEquals(2, cfgLines.size());

        String reconstructedRepo = cfgLines.get(0);
        assertTrue(List.of("main", "main1", "project").contains(reconstructedRepo),
                "Should reconstruct from existing repos");
        assertEquals(cfgLines.get(0), cfgLines.get(1),
                "Current and default should be the same");
    }

    @Test
    void testCreateConfigFileUsesDefaultWhenNoReposExist() throws IOException {
        // Setup: empty root directory
        fs.createRootFolder();

        Path configPath = tempDir.resolve(".zettelConfig");
        assertFalse(Files.exists(configPath));

        // Act: create config file with default
        fs.createConfigFile("mydefault");

        // Assert: config file created with provided default
        assertTrue(Files.exists(configPath));
        List<String> cfgLines = Files.readAllLines(configPath);
        assertEquals(2, cfgLines.size());
        assertEquals("mydefault", cfgLines.get(0));
        assertEquals("mydefault", cfgLines.get(1));
    }

    @Test
    void testCreateConfigFileIgnoresInvalidDirectories() throws IOException {
        // Setup: create some directories that are NOT valid repos
        fs.createRootFolder();
        Files.createDirectory(tempDir.resolve("incomplete1")); // no subdirs
        Files.createDirectory(tempDir.resolve("incomplete2"));
        Files.createDirectory(tempDir.resolve("incomplete2").resolve("notes")); // missing archive and index

        // Create one valid repo
        fs.createRepoStructure("valid");

        Path configPath = tempDir.resolve(".zettelConfig");

        // Act: create config file
        fs.createConfigFile("default");

        // Assert: should reconstruct from "valid" repo, not incomplete ones
        List<String> cfgLines = Files.readAllLines(configPath);
        assertEquals("valid", cfgLines.get(0));
        assertEquals("valid", cfgLines.get(1));
    }

    @Test
    void testCreateConfigFileDoesNotOverwriteExisting() throws IOException {
        // Setup: create config file manually
        fs.createRootFolder();
        Path configPath = tempDir.resolve(".zettelConfig");
        Files.writeString(configPath, "existing\nexisting\n");

        // Act: attempt to create config file
        fs.createConfigFile("newdefault");

        // Assert: original content preserved
        List<String> cfgLines = Files.readAllLines(configPath);
        assertEquals("existing", cfgLines.get(0));
        assertEquals("existing", cfgLines.get(1));
    }

    @Test
    void testCreateConfigFileHandlesEmptyRootGracefully() throws IOException {
        // Setup: root doesn't exist yet
        Path nonExistentRoot = tempDir.resolve("nonexistent");
        FileSystemManager fsNew = new FileSystemManager(nonExistentRoot.toString());

        // Act: create config without creating root first
        fsNew.createConfigFile("default");

        // Assert: should handle gracefully (may not create file if root doesn't exist)
        // This tests error handling rather than success
        Path configPath = nonExistentRoot.resolve(".zettelConfig");
        assertFalse(Files.exists(configPath),
                "Config should not be created when root doesn't exist");
    }
}
