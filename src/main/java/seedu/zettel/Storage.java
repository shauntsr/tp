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

    // Folder and file names for the repos
    static final String DEFAULT_REPO = "main";
    static final String REPO_NOTES = "notes";
    static final String REPO_ARCHIVE = "archive";
    static final String REPO_INDEX = "index.txt";

    static final String STORAGE_FILE = "zettel.txt"; // Placeholder until we migrate
    private final Path rootPath; // Root directory path
    private String repoName = DEFAULT_REPO;

    public Storage(String rootPath) {
        this.rootPath = Paths.get(rootPath);
    }

    public void init() {
        // Create root folder
        try {
            if (Files.notExists(rootPath)) {
                Files.createDirectories(rootPath);
            }
        } catch (IOException e) {
            System.out.println("Error creating " + rootPath + " folder.");
        }

        // Create config file
        Path configPath = rootPath.resolve(CONFIG_FILE);
        try {
            if (Files.notExists(configPath)) {
                Files.createFile(configPath);
                Files.writeString(configPath, DEFAULT_REPO);
            }
        } catch (IOException e) {
            System.out.println("Error creating " + CONFIG_FILE + ".");
        }

        // Initialise default repo if doesn't exist
        Path defaultRepoPath = rootPath.resolve(DEFAULT_REPO);
        if (Files.notExists(defaultRepoPath)) {
            createRepo(DEFAULT_REPO);
        }

        // Create storage file
        // Can remove this portion after we use a folder of notes
        Path filePath = defaultRepoPath.resolve(REPO_NOTES).resolve(STORAGE_FILE);
        try {
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            System.out.println("Error creating " + filePath + ".");
        }
    }

    public ArrayList<Note> load() {
        Path repoPath = rootPath.resolve(repoName);
        Path filePath = repoPath.resolve(REPO_NOTES).resolve(STORAGE_FILE);
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

    public void createRepo(String repoName) {
        Path repoPath = rootPath.resolve(repoName);
        try {
            Files.createDirectories(repoPath.resolve(REPO_NOTES));
            Files.createDirectories(repoPath.resolve(REPO_ARCHIVE));
            Files.createFile(repoPath.resolve(REPO_INDEX));
        } catch (IOException e) {
            System.out.println("Error initialising repository " + repoName) ;
        }
    }

    public void changeRepo(String repoName) {
        try {
            this.repoName = repoName;
            Path configPath = rootPath.resolve(CONFIG_FILE);
            Files.writeString(configPath,repoName);
        } catch (IOException e) {
            System.out.println("Unable to change repository to " + repoName);
        }
    }

    public void save(List<Note> notes) {
        Path repoPath = rootPath.resolve(repoName).resolve(REPO_NOTES);
        Path filePath = repoPath.resolve(STORAGE_FILE);
        try {
            Files.createDirectories(repoPath); // Ensure directory exists

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

        String[] fields = line.split(" \\| ", -1);
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
