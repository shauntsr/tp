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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import seedu.zettel.Note;
import seedu.zettel.exceptions.ZettelException;

public class StorageTest {

    @TempDir
    private Path tempDir;
    private Storage storage;

    @BeforeEach
    void setup() {
        storage = new Storage(tempDir.toString());
    }

    @Test
    void testInitCreatesRootFolderAndConfig() throws IOException {
        Path configPath = tempDir.resolve(".zettelConfig");
        Path defaultRepoNotes = tempDir.resolve("main").resolve("notes");
        Path defaultRepoArchive = tempDir.resolve("main").resolve("archive");
        Path defaultIndexFile = tempDir.resolve("main").resolve("index.txt");

        assertFalse(Files.exists(configPath));
        assertFalse(Files.exists(defaultRepoNotes));
        assertFalse(Files.exists(defaultRepoArchive));

        storage.init();

        assertTrue(Files.exists(configPath));
        List<String> lines = Files.readAllLines(configPath);
        assertEquals("main", lines.get(0));
        assertEquals("main", lines.get(1));

        assertTrue(Files.exists(defaultRepoNotes));
        assertTrue(Files.exists(defaultRepoArchive));
        assertTrue(Files.exists(defaultIndexFile));
    }

    @Test
    void testCreateRepo() throws IOException {
        // should create new repo structure, and update config
        String repoName = "newRepo";
        Path repoNotes = tempDir.resolve(repoName).resolve("notes");
        Path repoArchive = tempDir.resolve(repoName).resolve("archive");
        Path indexFile = tempDir.resolve(repoName).resolve("index.txt");

        storage.createRepo(repoName);

        assertTrue(Files.exists(repoNotes));
        assertTrue(Files.exists(repoArchive));
        assertTrue(Files.exists(indexFile));

        Path configFile = tempDir.resolve(".zettelConfig");
        List<String> lines = Files.readAllLines(configFile);
        assertTrue(lines.get(0).contains(repoName));
    }

    @Test
    void testCreateStorageFileWritesNoteBody() throws IOException {
        storage.init();

        Note note = new Note(
                "88888888",
                "title",
                "title.txt",
                "Hello World",
                Instant.now(),
                Instant.now(),
                false,
                false,
                null,
                new ArrayList<>()
        );

        storage.createStorageFile(note);

        Path noteFile = tempDir.resolve("main").resolve("notes").resolve("title.txt");

        assertTrue(Files.exists(noteFile));
        assertEquals("Hello World", Files.readString(noteFile));
    }

    @Test
    void testSaveAndLoadNotesWorks() {
        storage.init();

        Note note1 = new Note("88888889", "Title1", "Title1.txt", "Body1",
                Instant.now(), Instant.now(), false, false, null,  new ArrayList<>());
        Note note2 = new Note("99999999", "Title2", "Title2.txt", "Body2",
                Instant.now(), Instant.now(), true, false, null,  new ArrayList<>());

        List<Note> notes = List.of(note1, note2);

        storage.createStorageFile(note1);
        storage.createStorageFile(note2);

        storage.save(notes);

        ArrayList<Note> loadedNotes = storage.load();
        assertEquals(2, loadedNotes.size());
        assertEquals("Title1", loadedNotes.get(0).getTitle());
        assertEquals("Body1", loadedNotes.get(0).getBody());
        assertEquals("Title2", loadedNotes.get(1).getTitle());
        assertEquals("Body2", loadedNotes.get(1).getBody());
    }

    @Test
    void testChangeRepo() {
        // should switch repo and update config
        storage.init();
        storage.createRepo("testRepo");

        storage.changeRepo("testRepo");
        assertEquals("testRepo", storage.readCurrRepo());
    }

    @Test
    void testUpdateConfigAppendsNewRepoToConfig() throws IOException, ZettelException {
        storage.init();
        storage.updateConfig("anotherRepo");

        List<String> lines = Files.readAllLines(tempDir.resolve(".zettelConfig"));
        assertEquals("main", lines.get(0).trim()); // first line unchanged
        assertEquals("anotherRepo", lines.get(1).trim()); // second line updated
    }

    @Test
    void testUpdateTagsWritesToTagsFile() throws ZettelException, IOException {
        List<String> tags = List.of("urgent", "work", "personal");

        storage.updateTags(tags);

        Path tagsFile = tempDir.resolve("tags.txt");
        List<String> lines = Files.readAllLines(tagsFile);

        // Should contain exactly the tags, one per line (order doesn't matter)
        assertEquals(tags.size(), lines.size(), "tags.txt should contain the same number of tags");

        for (String tag : tags) {
            assertTrue(lines.contains(tag), "tags.txt should contain tag: " + tag);
        }
    }
}
