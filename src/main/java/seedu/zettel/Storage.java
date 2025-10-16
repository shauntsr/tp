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
    static final String CONFIG_FILE = ".zettelConfig";
    static final String STORAGE_FILE = "zettel.txt"; // Placeholder until we migrate
    private final Path rootPath; // Root directory path

    public Storage(String rootPath) {
        this.rootPath = Paths.get(rootPath);
    }

    public void init() {
        Path configPath = rootPath.resolve(CONFIG_FILE);
        Path filePath = rootPath.resolve(STORAGE_FILE);

        // Create root folder
        try {
            if (Files.notExists(rootPath)) {
                Files.createDirectories(rootPath);
            }
        } catch (IOException e) {
            System.out.println("Error creating " + rootPath + " folder.");
        }

        // Create config file
        try {
            if (Files.notExists(configPath)) {
                Files.createFile(configPath);
            }
        } catch (IOException e) {
            System.out.println("Error creating " + CONFIG_FILE + ".");
        }

        // Create storage file
        // Can remove this portion after we use a folder of notes
        try {
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            System.out.println("Error creating " + filePath + ".");
        }
    }

    public ArrayList<Note> load() {
        Path filePath = rootPath.resolve(STORAGE_FILE);
        try {
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
        Path filePath = rootPath.resolve(STORAGE_FILE);
        try {
            Files.createDirectories(rootPath); // Ensure directory exists

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
