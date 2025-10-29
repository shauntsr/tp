package seedu.zettel.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.zettel.Note;

/**
 * Handles serialization and deserialization of Note objects.
 * Converts notes to/from file format for persistence.
 */
public class NoteSerializer {

    /** Delimiter used for separating items in lists during serialization. */
    static final String LIST_DELIM = ";;";

    /**
     * Loads notes from the index file and their corresponding body files.
     *
     * @param indexPath the path to the index file containing note metadata
     * @param notesDir the directory containing note body files (notes/)
     * @param archiveDir the directory containing archived note body files (archive/)
     * @return an ArrayList of loaded notes
     */
    public ArrayList<Note> loadNotes(Path indexPath, Path notesDir, Path archiveDir) {
        try (Stream<String> lines = Files.lines(indexPath)) {
            return lines.map(this::parseIndex)
                    .filter(Objects::nonNull)
                    .map(note -> loadNoteBody(note, notesDir, archiveDir))
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            System.out.println("Error loading file: " + indexPath);
            return new ArrayList<>();
        }
    }

    /**
     * Saves a list of notes to the index file.
     *
     * @param notes the list of notes to save
     * @param indexPath the path where the index file should be written
     * @throws IOException if there's an error writing to the file
     */
    public void saveNotes(List<Note> notes, Path indexPath) throws IOException {
        List<String> lines = notes.stream()
                .map(this::toIndexFormat)
                .collect(Collectors.toList());

        Files.write(indexPath, lines);
    }

    /**
     * Reads the index file and extracts the expected filenames of note body files.
     *
     * @param indexPath the path to the index file
     * @return a list of expected filenames
     */
    public List<String> getExpectedFilenames(Path indexPath) {
        List<String> expectedFiles = new ArrayList<>();

        try (Stream<String> lines = Files.lines(indexPath)) {
            lines.map(this::parseIndex)
                    .filter(Objects::nonNull)
                    .forEach(note -> expectedFiles.add(note.getFilename()));
        } catch (IOException e) {
            System.out.println("Warning: Could not read index file for validation: " + e.getMessage());
        }

        return expectedFiles;
    }

    /**
     * Returns a map of expected body filenames to a boolean indicating whether
     * the note is archived (true => expected in archive/, false => expected in notes/).
     *
     * @param indexPath path to the index file
     * @return Map filename -> isArchived
     */
    public Map<String, Boolean> getExpectedFilenamesWithArchiveFlag(Path indexPath) {
        Map<String, Boolean> map = new HashMap<>();
        try (Stream<String> lines = Files.lines(indexPath)) {
            lines.map(this::parseIndex)
                    .filter(Objects::nonNull)
                    .forEach(note -> map.put(note.getFilename(), note.isArchived()));
        } catch (IOException e) {
            System.out.println("Warning: Could not read index file for validation: " + e.getMessage());
        }
        return map;
    }

    /**
     * Loads the body content for a note from its corresponding body file in notes/ or archive/.
     *
     * @param note the note to load the body for
     * @param notesDir the notes directory
     * @param archiveDir the archive directory
     * @return the note with its body loaded
     */
    private Note loadNoteBody(Note note, Path notesDir, Path archiveDir) {
        Path bodyFile = (note.isArchived() ? archiveDir : notesDir).resolve(note.getFilename());
        try {
            String body = Files.readString(bodyFile);
            note.loadBody(body);
        } catch (IOException e) {
            System.out.println("Warning: cannot read body file for '" +
                    note.getTitle() + "': " + e.getMessage());
            note.loadBody("");
        }
        return note;
    }

    /**
     * Converts a note to its string representation for storage in the index file.
     *
     * @param note the note to convert
     * @return the string representation of the note in index format
     */
    private String toIndexFormat(Note note) {
        String logsStr = String.join(LIST_DELIM, note.getLogs());
        String tagsStr = String.join(LIST_DELIM, note.getTags());
        String filename = note.getFilename() != null ? note.getFilename() : "";
        String archiveName = note.getArchiveName() != null ? note.getArchiveName() : "";

        return String.format("%s | %s | %s | %s | %s | %s | %s | %s | %s | %s",
                note.getId(),
                note.getTitle(),
                filename,
                note.getCreatedAt().toString(),
                note.getModifiedAt().toString(),
                note.isPinned() ? "1" : "0",
                note.isArchived() ? "1" : "0",
                archiveName,
                logsStr,
                tagsStr
        );
    }

    /**
     * Parses a line from the index file and creates a Note object.
     *
     * @param line the line to parse from the index file
     * @return a Note object, or null if the line is corrupted or invalid
     */
    private Note parseIndex(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        String[] fields = line.split(" \\| ", -1);
        try {
            String id = fields[0];
            String title = fields[1];
            String filename = fields[2];
            String body = "";
            Instant createdAt = Instant.parse(fields[3]);
            Instant modifiedAt = Instant.parse(fields[4]);
            boolean pinned = fields[5].equals("1");
            boolean archived = fields[6].equals("1");
            String archiveName = fields[7].isEmpty() ? null : fields[7];

            List<String> logs = new ArrayList<>();
            if (fields.length > 8 && !fields[8].isEmpty()) {
                logs = Arrays.asList(fields[8].split(LIST_DELIM));
            }

            String tagsStr = fields[9];
            List<String> tags = new ArrayList<>();
            if (!tagsStr.isEmpty()) {
                tags = Arrays.asList(tagsStr.split(LIST_DELIM));
            }

            return new Note(id, title, filename, body, createdAt, modifiedAt,
                    pinned, archived, archiveName, logs, tags);
        } catch (Exception e) {
            System.out.println("Skipping corrupted line: " + line);
            return null;
        }
    }
}
