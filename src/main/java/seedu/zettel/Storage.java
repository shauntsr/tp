package seedu.zettel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class Storage {
    private final Path filePath;

    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    public ArrayList<Note> load() {
        try {
            if (!Files.exists(filePath)) {
                Files.createDirectories(filePath.getParent());
                return new ArrayList<>();
            }

            return Files.lines(filePath)
                    .map(this::parseSaveFile)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            System.out.println("Error loading file: " + filePath);
            return new ArrayList<>();
        }
    }

    public void save(List<Note> notes) {
        try {
            Files.createDirectories(filePath.getParent()); // Ensure directory exists

            List<String> lines = notes.stream()
                    .map(this::toSaveFormat)
                    .collect(Collectors.toList());

            Files.write(filePath, lines);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    private String toSaveFormat(Note note) {
        String logsStr = String.join(";;", note.getLogs());
        String archiveName = note.getArchiveName() != null ? note.getArchiveName() : "";

        return String.format("%s | %s | %s | %s | %s | %s | %s | %s | %s | %s",
                note.getId(),
                note.getTitle(),
                note.getFilename(),
                note.getBody().replace("\n", "\\n"), // Escape newlines in body
                note.getCreatedAt().toString(),
                note.getModifiedAt().toString(),
                note.isPinned() ? "1" : "0",
                note.isArchived() ? "1" : "0",
                archiveName,
                logsStr);
    }

    private Note parseSaveFile(String line) {
        if (line.isBlank()) {
            return null;
        }

        String[] fields = line.split(" \\| ");
        try {
            String id = fields[0];
            String title = fields[1];
            String filename = fields[2];
            String body = fields[3].replace("\\n", "\n"); // Unescape newlines
            Instant createdAt = Instant.parse(fields[4]);
            Instant modifiedAt = Instant.parse(fields[5]);
            boolean pinned = fields[6].equals("1");
            boolean archived = fields[7].equals("1");
            String archiveName = fields[8].isEmpty() ? null : fields[8];

            List<String> logs = new ArrayList<>();
            if (fields.length > 9 && !fields[9].isEmpty()) {
                logs = Arrays.asList(fields[9].split(";;"));
            }

            return new Note(id, title, filename, body, createdAt, modifiedAt,
                    pinned, archived, archiveName, logs);
        } catch (Exception e) {
            System.out.println("Skipping corrupted line: " + line);
            return null;
        }
    }
}
