package seedu.zettel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StorageTest {

    @TempDir
    private static Path tempDir;
    private Storage storage;

    @BeforeEach
    void setup() {
        storage = new Storage(tempDir.toString());
    }

    @Test
    void initCreatesRootFolderAndConfig() throws IOException {
        Path configPath = tempDir.resolve(".zettelConfig");
        Path defaultRepoNotes = tempDir.resolve("main").resolve("notes");
        Path defaultRepoArchive = tempDir.resolve("main").resolve("archive");
        Path storageFile = defaultRepoNotes.resolve("zettel.txt");

        // Before init, nothing exists
        assertFalse(Files.exists(configPath));
        assertFalse(Files.exists(defaultRepoNotes));
        assertFalse(Files.exists(defaultRepoArchive));
        assertFalse(Files.exists(storageFile));

        storage.init();

        // After init, config and default repo structure exist
        assertTrue(Files.exists(configPath));
        assertEquals("main", Files.readString(configPath).trim());

        assertTrue(Files.exists(defaultRepoNotes));
        assertTrue(Files.exists(defaultRepoArchive));
        assertTrue(Files.exists(storageFile));
    }

    @Test
    void createRepoCreatesNewRepoStructure() throws IOException {
        String repoName = "testRepo";
        Path notesPath = tempDir.resolve(repoName).resolve("notes");
        Path archivePath = tempDir.resolve(repoName).resolve("archive");
        Path indexFile = tempDir.resolve(repoName).resolve("index.txt");

        // Repo should not exist initially
        assertFalse(Files.exists(notesPath));
        assertFalse(Files.exists(archivePath));
        assertFalse(Files.exists(indexFile));

        storage.createRepo(repoName);

        // Repo directories and index file should exist
        assertTrue(Files.exists(notesPath));
        assertTrue(Files.exists(archivePath));
        assertTrue(Files.exists(indexFile));
    }
}
