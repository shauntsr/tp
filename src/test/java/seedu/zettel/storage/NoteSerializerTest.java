package seedu.zettel.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.zettel.Note;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoteSerializerTest {

    @TempDir
    Path tempDir;

    private NoteSerializer serializer;
    private Path indexPath;
    private Path notesDir;
    private Path archiveDir;

    @BeforeEach
    void setUp() throws IOException {
        serializer = new NoteSerializer();
        indexPath = tempDir.resolve("index.txt");
        notesDir = tempDir.resolve("notes");
        archiveDir = tempDir.resolve("archive");
        Files.createDirectories(notesDir);
        Files.createDirectories(archiveDir);
    }

    @Test
    void testSaveAndLoadNotes_singleNote() throws IOException {
        Instant now = Instant.parse("2024-01-15T10:30:00Z");

        Note note = new Note("12345678", "Test Title", "test.txt", "Test body", now, now,
                false, false, null, new ArrayList<>());

        Files.writeString(notesDir.resolve("test.txt"), "Test body");

        List<Note> notes = Arrays.asList(note);
        serializer.saveNotes(notes, indexPath);

        ArrayList<Note> loaded = serializer.loadNotes(indexPath, notesDir, archiveDir);

        assertEquals(1, loaded.size());
        Note loadedNote = loaded.get(0);
        assertEquals("12345678", loadedNote.getId());
        assertEquals("Test Title", loadedNote.getTitle());
        assertEquals("test.txt", loadedNote.getFilename());
        assertEquals("Test body", loadedNote.getBody());
        assertEquals(now, loadedNote.getCreatedAt());
        assertEquals(now, loadedNote.getModifiedAt());
        assertFalse(loadedNote.isPinned());
        assertFalse(loadedNote.isArchived());
        assertNull(loadedNote.getArchiveName());
        assertTrue(loadedNote.getOutgoingLinks().isEmpty());
        assertTrue(loadedNote.getIncomingLinks().isEmpty());
    }

    @Test
    void testSaveAndLoadNotes_withTags() throws IOException {
        Instant now = Instant.parse("2024-01-15T10:30:00Z");
        List<String> tags = Arrays.asList("tag1", "tag2", "tag3");

        Note note = new Note("abcd1234", "Tagged Note", "tagged.txt", "Content", now, now,
                false, false, null, tags);

        Files.writeString(notesDir.resolve("tagged.txt"), "Content");

        serializer.saveNotes(Arrays.asList(note), indexPath);
        ArrayList<Note> loaded = serializer.loadNotes(indexPath, notesDir, archiveDir);

        assertEquals(1, loaded.size());
        assertEquals(tags, loaded.get(0).getTags());
    }

    @Test
    void testSaveAndLoadNotes_withLinks() throws IOException {
        Instant now = Instant.parse("2024-01-15T10:30:00Z");

        Note note1 = new Note("note0001", "First", "first.txt", "Body1", now, now,
                false, false, null, new ArrayList<>());
        Note note2 = new Note("note0002", "Second", "second.txt", "Body2", now, now,
                false, false, null, new ArrayList<>());
        Note note3 = new Note("note0003", "Third", "third.txt", "Body3", now, now,
                false, false, null, new ArrayList<>());

        // Link note1 -> note2 and note1 -> note3
        note1.addOutgoingLink("note0002");
        note1.addOutgoingLink("note0003");
        note2.addIncomingLink("note0001");
        note3.addIncomingLink("note0001");

        Files.writeString(notesDir.resolve("first.txt"), "Body1");
        Files.writeString(notesDir.resolve("second.txt"), "Body2");
        Files.writeString(notesDir.resolve("third.txt"), "Body3");

        serializer.saveNotes(Arrays.asList(note1, note2, note3), indexPath);
        ArrayList<Note> loaded = serializer.loadNotes(indexPath, notesDir);

        assertEquals(3, loaded.size());

        Note loadedNote1 = loaded.stream()
                .filter(n -> n.getId().equals("note0001"))
                .findFirst().get();
        Note loadedNote2 = loaded.stream()
                .filter(n -> n.getId().equals("note0002"))
                .findFirst().get();
        Note loadedNote3 = loaded.stream()
                .filter(n -> n.getId().equals("note0003"))
                .findFirst().get();

        // Verify note1's links
        assertEquals(2, loadedNote1.getOutgoingLinks().size());
        assertTrue(loadedNote1.getOutgoingLinks().contains("note0002"));
        assertTrue(loadedNote1.getOutgoingLinks().contains("note0003"));
        assertTrue(loadedNote1.getIncomingLinks().isEmpty());

        // Verify note2's links
        assertTrue(loadedNote2.getOutgoingLinks().isEmpty());
        assertEquals(1, loadedNote2.getIncomingLinks().size());
        assertTrue(loadedNote2.getIncomingLinks().contains("note0001"));

        // Verify note3's links
        assertTrue(loadedNote3.getOutgoingLinks().isEmpty());
        assertEquals(1, loadedNote3.getIncomingLinks().size());
        assertTrue(loadedNote3.getIncomingLinks().contains("note0001"));
    }

    @Test
    void testSaveAndLoadNotes_pinnedAndArchived() throws IOException {
        Instant now = Instant.parse("2024-01-15T10:30:00Z");
        Note note = new Note("xyz98765", "Pinned Archive", "pinned.txt", "Body", now, now,
                true, true, "archive.zip", new ArrayList<>());

        // archived notes should have their bodies in the archive directory
        Files.writeString(archiveDir.resolve("pinned.txt"), "Body");

        serializer.saveNotes(Arrays.asList(note), indexPath);
        ArrayList<Note> loaded = serializer.loadNotes(indexPath, notesDir, archiveDir);

        assertEquals(1, loaded.size());
        assertTrue(loaded.get(0).isPinned());
        assertTrue(loaded.get(0).isArchived());
        assertEquals("archive.zip", loaded.get(0).getArchiveName());
        assertEquals("Body", loaded.get(0).getBody());
    }

    @Test
    void testSaveAndLoadNotes_multipleNotes() throws IOException {
        Instant now = Instant.parse("2024-01-15T10:30:00Z");
        Note note1 = new Note("note0001", "First", "first.txt", "Body1", now, now,
                false, false, null, new ArrayList<>());

        Note note2 = new Note("note0002", "Second", "second.txt", "Body2", now, now,
                true, false, null, Arrays.asList("tag1"));

        Files.writeString(notesDir.resolve("first.txt"), "Body1");
        Files.writeString(notesDir.resolve("second.txt"), "Body2");

        serializer.saveNotes(Arrays.asList(note1, note2), indexPath);
        ArrayList<Note> loaded = serializer.loadNotes(indexPath, notesDir, archiveDir);

        assertEquals(2, loaded.size());
        assertEquals("First", loaded.get(0).getTitle());
        assertEquals("Second", loaded.get(1).getTitle());
        assertTrue(loaded.get(1).isPinned());
        assertEquals(1, loaded.get(1).getTags().size());
    }

    @Test
    void testLoadNotes_missingBodyFile() throws IOException {
        String indexLine = "test0001 | Title | missing.txt | 2024-01-15T10:30:00Z | " +
                "2024-01-15T10:30:00Z | 0 | 0 |  |  |  | ";
        Files.writeString(indexPath, indexLine);

        ArrayList<Note> loaded = serializer.loadNotes(indexPath, notesDir, archiveDir);

        assertEquals(1, loaded.size());
        assertEquals("", loaded.get(0).getBody());
        assertEquals("Title", loaded.get(0).getTitle());
    }

    @Test
    void testLoadNotes_missingIndexFile() {
        Path nonExistent = tempDir.resolve("nonexistent.txt");
        ArrayList<Note> loaded = serializer.loadNotes(nonExistent, notesDir, archiveDir);

        assertTrue(loaded.isEmpty());
    }

    @Test
    void testLoadNotes_corruptedLine() throws IOException {
        String corruptedLine = "invalid | data | format";
        Files.writeString(indexPath, corruptedLine);

        ArrayList<Note> loaded = serializer.loadNotes(indexPath, notesDir, archiveDir);

        assertTrue(loaded.isEmpty());
    }

    @Test
    void testLoadNotes_blankLines() throws IOException {
        Instant now = Instant.parse("2024-01-15T10:30:00Z");
        String validLine = String.format("blank001 | Title | test.txt | %s | %s | 0 | 0 |  |  |  | ",
                now, now);
        Files.writeString(indexPath, "\n" + validLine + "\n\n");
        Files.writeString(notesDir.resolve("test.txt"), "Body");

        ArrayList<Note> loaded = serializer.loadNotes(indexPath, notesDir, archiveDir);

        assertEquals(1, loaded.size());
    }

    @Test
    void testGetExpectedFilenames() throws IOException {
        Instant now = Instant.parse("2024-01-15T10:30:00Z");
        String line1 = String.format("file0001 | Note1 | file1.txt | %s | %s | 0 | 0 |  |  |  | ", now, now);
        String line2 = String.format("file0002 | Note2 | file2.txt | %s | %s | 0 | 0 |  |  |  | ", now, now);
        Files.writeString(indexPath, line1 + "\n" + line2);

        List<String> filenames = serializer.getExpectedFilenames(indexPath);

        assertEquals(2, filenames.size());
        assertTrue(filenames.contains("file1.txt"));
        assertTrue(filenames.contains("file2.txt"));
    }

    @Test
    void testGetExpectedFilenames_missingIndexFile() {
        Path nonExistent = tempDir.resolve("nonexistent.txt");
        List<String> filenames = serializer.getExpectedFilenames(nonExistent);

        assertTrue(filenames.isEmpty());
    }

    @Test
    void testSaveNotes_emptyList() throws IOException {
        serializer.saveNotes(new ArrayList<>(), indexPath);

        assertTrue(Files.exists(indexPath));
        assertEquals(0, Files.readAllLines(indexPath).size());
    }

    @Test
    void testSaveAndLoad_specialCharacters() throws IOException {
        Instant now = Instant.parse("2024-01-15T10:30:00Z");

        // Use safe special characters that don't conflict with serialization format
        Note note = new Note("special1", "Title with $pecial @chars!", "special.txt",
                "Body with \n newlines \t tabs", now, now,
                false, false, null, Arrays.asList("tag-1", "tag_2"));

        Files.writeString(notesDir.resolve("special.txt"), "Body with \n newlines \t tabs");

        serializer.saveNotes(Arrays.asList(note), indexPath);
        ArrayList<Note> loaded = serializer.loadNotes(indexPath, notesDir, archiveDir);

        assertEquals(1, loaded.size());
        assertEquals("Title with $pecial @chars!", loaded.get(0).getTitle());
        assertEquals(Arrays.asList("tag-1", "tag_2"), loaded.get(0).getTags());
    }

    @Test
    void testLoadNotes_emptyFields() throws IOException {
        Instant now = Instant.parse("2024-01-15T10:30:00Z");
        String line = String.format("empty001 |  | empty.txt | %s | %s | 0 | 0 |  |  |  | ", now, now);
        Files.writeString(indexPath, line);
        Files.writeString(notesDir.resolve("empty.txt"), "");

        ArrayList<Note> loaded = serializer.loadNotes(indexPath, notesDir, archiveDir);

        assertEquals(1, loaded.size());
        assertEquals("", loaded.get(0).getTitle());
        assertTrue(loaded.get(0).getTags().isEmpty());
        assertTrue(loaded.get(0).getOutgoingLinks().isEmpty());
        assertTrue(loaded.get(0).getIncomingLinks().isEmpty());
    }

    @Test
    void testLoadNotes_withEmptyLists() throws IOException {
        Instant now = Instant.parse("2024-01-15T10:30:00Z");
        String line = String.format("empty002 | Title | test.txt | %s | %s | 0 | 0 |  |  |  | ", now, now);
        Files.writeString(indexPath, line);
        Files.writeString(notesDir.resolve("test.txt"), "Body");

        ArrayList<Note> loaded = serializer.loadNotes(indexPath, notesDir, archiveDir);

        assertEquals(1, loaded.size());
        assertTrue(loaded.get(0).getTags().isEmpty());
        assertTrue(loaded.get(0).getOutgoingLinks().isEmpty());
        assertTrue(loaded.get(0).getIncomingLinks().isEmpty());
    }

    @Test
    void testSaveAndLoadNotes_complexLinkNetwork() throws IOException {
        Instant now = Instant.parse("2024-01-15T10:30:00Z");

        // Use 8-character IDs to satisfy Note.java:89 assertion
        Note note1 = new Note("aaa00001", "A", "a.txt", "Body A",
                now, now, false, false, null, new ArrayList<>());
        Note note2 = new Note("bbb00002", "B", "b.txt", "Body B",
                now, now, false, false, null, new ArrayList<>());
        Note note3 = new Note("ccc00003", "C", "c.txt", "Body C",
                now, now, false, false, null, new ArrayList<>());

        // Create a network: A -> B, A -> C, B -> C
        note1.addOutgoingLink("bbb00002");
        note1.addOutgoingLink("ccc00003");
        note2.addIncomingLink("aaa00001");
        note2.addOutgoingLink("ccc00003");
        note3.addIncomingLink("aaa00001");
        note3.addIncomingLink("bbb00002");

        // Write note files
        Files.writeString(notesDir.resolve("a.txt"), "Body A");
        Files.writeString(notesDir.resolve("b.txt"), "Body B");
        Files.writeString(notesDir.resolve("c.txt"), "Body C");

        // Save and load
        serializer.saveNotes(Arrays.asList(note1, note2, note3), indexPath);
        ArrayList<Note> loaded = serializer.loadNotes(indexPath, notesDir);

        assertEquals(3, loaded.size());

        Note loadedA = loaded.stream().filter(n -> n.getId().equals("aaa00001")).findFirst().get();
        Note loadedB = loaded.stream().filter(n -> n.getId().equals("bbb00002")).findFirst().get();
        Note loadedC = loaded.stream().filter(n -> n.getId().equals("ccc00003")).findFirst().get();

        // Verify A: outgoing to B and C, no incoming
        assertEquals(2, loadedA.getOutgoingLinks().size());
        assertTrue(loadedA.getOutgoingLinks().contains("bbb00002"));
        assertTrue(loadedA.getOutgoingLinks().contains("ccc00003"));
        assertTrue(loadedA.getIncomingLinks().isEmpty());

        // Verify B: outgoing to C, incoming from A
        assertEquals(1, loadedB.getOutgoingLinks().size());
        assertTrue(loadedB.getOutgoingLinks().contains("ccc00003"));
        assertEquals(1, loadedB.getIncomingLinks().size());
        assertTrue(loadedB.getIncomingLinks().contains("aaa00001"));

        // Verify C: no outgoing, incoming from A and B
        assertTrue(loadedC.getOutgoingLinks().isEmpty());
        assertEquals(2, loadedC.getIncomingLinks().size());
        assertTrue(loadedC.getIncomingLinks().contains("aaa00001"));
        assertTrue(loadedC.getIncomingLinks().contains("bbb00002"));
    }
}

