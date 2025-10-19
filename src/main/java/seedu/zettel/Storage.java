package seedu.zettel;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.zettel.exceptions.InvalidRepoException;
import seedu.zettel.exceptions.ZettelException;


/**
 * Handles all file and directory operations for Zettel repositories.
 * <p>
 * The Storage class is responsible for initializing repository folders,
 * loading and saving notes, switching repositories, and maintaining
 * a configuration file that tracks the currently active repository.
 */
public class Storage {
    /** Configuration file storing the currently active repository name. */
    static final String CONFIG_FILE = ".zettelConfig";

    /** Default repository name created during initialization. */
    static final String DEFAULT_REPO = "main";

    /** Folder name for active notes. */
    static final String REPO_NOTES = "notes";

    /** Folder name for archived notes. */
    static final String REPO_ARCHIVE = "archive";

    /** Index file name for notes metadata. */
    static final String REPO_INDEX = "index.txt";

    /** Temporary storage file for note persistence (to be migrated later). */
    static final String STORAGE_FILE = "zettel.txt";

    private static final Logger logger = Logger.getLogger(Storage.class.getName());
  
    /** The root directory path under which repositories are stored; default is data/. */
    private final Path rootPath;

    /** The name of the currently active repository. */
    private String repoName = DEFAULT_REPO;

    private ArrayList<String> repoList = new ArrayList<>();

    /**
     * Creates a Storage instance with the specified root directory.
     *
     * @param rootPath The root directory for storage.
     */
    public Storage(String rootPath) {
        this.rootPath = Paths.get(rootPath);
    }

    /**
     * Initializes the storage by creating the root folder, config file,
     * default repository, and default storage file.
     */
    public void init() {
        createRootFolder();

        createConfigFile();

        // Initialise default repo if doesn't exist
        Path defaultRepoPath = rootPath.resolve(DEFAULT_REPO);
        if (Files.notExists(defaultRepoPath)) {
            createRepo(DEFAULT_REPO);
        }

        try {
            loadConfig();

            String checkedOutRepo = readCurrRepo();
            changeRepo(checkedOutRepo);

            // validate every repo in repoList
            for (String repo : repoList) {
                validateRepo(repo);
            }
        } catch (ZettelException e) {
            System.out.println("Error during init: " + e.getMessage());
        }
    }

    /**
     * Reads the currently checked-out repository from .zettelConfig.
     * Defaults to "main" if line 2 is missing or file is malformed.
     */
    private String readCurrRepo() {
        Path configFile = rootPath.resolve(CONFIG_FILE);
        try {
            if (Files.exists(configFile)) {
                List<String> lines = Files.readAllLines(configFile);
                if (lines.size() == 2 && !lines.get(1).isBlank()) {
                    return lines.get(1).trim();
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: failed to read checked-out repo: " + e.getMessage());
        }
        return DEFAULT_REPO;
    }

    /** Creates the root folder ("data") for all repositories if it does not exist. */
    private void createRootFolder() {
        try {
            if (Files.notExists(rootPath)) {
                Files.createDirectories(rootPath);
            }
        } catch (IOException e) {
            System.out.println("Error creating " + rootPath + " folder.");
        }
    }

    /** Creates a storage file in the default repository. */
    private static void createStorageFile(Path defaultRepoPath) {
        Path filePath = defaultRepoPath.resolve(REPO_NOTES).resolve(STORAGE_FILE);
        try {
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            System.out.println("Error creating " + filePath + ".");
        }
    }

    /**
     * Creates the configuration file (".zettelConfig") in the root directory.
     * The config file stores information about available repositories
     * and the currently active repository name.
     * <p>
     * If the file already exists, no action is taken.
     */
    private void createConfigFile() {
        Path configPath = rootPath.resolve(CONFIG_FILE);
        try {
            if (Files.notExists(configPath)) {
                Files.createFile(configPath);
                List<String> lines = List.of(DEFAULT_REPO, DEFAULT_REPO);
                Files.write(configPath, lines);
            }
        } catch (IOException e) {
            System.out.println("Error creating " + CONFIG_FILE + ".");
        }
    }

    public void loadConfig() {
        createConfigFile();
        Path configFile = rootPath.resolve(CONFIG_FILE);

        try {
            List<String> lines = Files.readAllLines(configFile);
            String firstLine = lines.isEmpty() ? DEFAULT_REPO : lines.get(0); // if empty (shouldn't be), use main
            repoList = Arrays.stream(firstLine.split("\\|"))
                    .map(String::trim)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            System.out.println("Error reading .zettelConfig, defaulting to main: " + e.getMessage());
            repoList = new ArrayList<>();
            repoList.add("main");
        }
    }

    public void updateConfig(String newRepo) throws ZettelException {
        createConfigFile();
        Path configFile = rootPath.resolve(CONFIG_FILE);

        try (Stream<String> stream = Files.lines(configFile)){
            List<String> lines = stream.collect(Collectors.toList());
            if (lines.isEmpty()) {
                lines.add(DEFAULT_REPO);
                lines.add(newRepo);
            } else if (lines.size() == 1) {
                lines.add(newRepo);
            } else {
                lines.set(1, newRepo);
            }
            Files.write(configFile, lines);

        } catch (IOException e) {
            throw new ZettelException("Failed to update checked-out repo in .zettelConfig: " + e.getMessage());
        }
    }

    /**
     * Loads all notes from the current repository.
     *
     * @return An ArrayList of Note objects; empty list if loading fails.
     */
    public ArrayList<Note> load() {
        Path repoPath = rootPath.resolve(repoName);
        Path notesDir = repoPath.resolve(REPO_NOTES);
        Path indexFile = repoPath.resolve(REPO_INDEX);

        ArrayList<Note> notes = new ArrayList<>();

        // Validate repo layout before attempting to read
        try {
            validateRepo(repoName);
        } catch (ZettelException e) {
            System.out.println("Error validating repo: " + e.getMessage());
            return notes;
        }

        try (Stream<String> lines = Files.lines(indexFile)){
            return lines.map(this::parseSaveFile)
                    .filter(Objects::nonNull)
                    .map(note -> {
                        Path bodyFile = notesDir.resolve(note.getFilename() + ".txt");
                        try {
                            String body = Files.readString(bodyFile);
                            note.setBody(body);
                        } catch (IOException e) {
                            System.out.println("Warning: cannot read body file for '" + note.getTitle() + "': " + e.getMessage());
                            note.setBody("");
                        }
                        return note;
                    })
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            System.out.println("Error loading file: " + indexFile);
            return new ArrayList<>();
        }
    }

    private void validateRepo(String repoName) throws ZettelException {
        Path repoPath = rootPath.resolve(repoName);
        Path notesDir = repoPath.resolve(REPO_NOTES);
        Path archiveDir = repoPath.resolve(REPO_ARCHIVE);
        Path indexFile = repoPath.resolve(REPO_INDEX);
        Path configFile = rootPath.resolve(CONFIG_FILE);

        // critical; .zettelConfig should always exist
        if (!Files.exists(configFile)) {
            throw new InvalidRepoException(".zettelConfig missing at " + configFile.toAbsolutePath());
        }

        createIfMissing(repoPath, "repository folder: " + repoName, true);
        createIfMissing(notesDir, "notes/ for repo: " + repoName, true);
        createIfMissing(archiveDir, "archive/ for repo: " + repoName, true);
        createIfMissing(indexFile, "index.txt for repo: " + repoName, false);

        // Track expected body files
        Set<String> expectedFiles = new HashSet<>();

        try (Stream<String> lines = Files.lines(indexFile)) {
            lines.map(this::parseSaveFile)
                    .filter(Objects::nonNull)
                    .forEach(note -> {
                        String fileName = note.getFilename() + ".txt";
                        expectedFiles.add(fileName);

                        Path bodyFile = notesDir.resolve(fileName);
                        try {
                            createIfMissing(bodyFile, "body file for note '" + note.getTitle() + "'", false);
                        } catch (ZettelException e) {
                            System.out.println("Warning: " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            throw new ZettelException("Failed to read index.txt: " + e.getMessage());
        }

        detectOrphans(notesDir, expectedFiles, repoName);
    }

    private void createIfMissing(Path path, String description, boolean isDirectory) throws ZettelException {
        try {
            if (Files.notExists(path)) {
                if (isDirectory) {
                    Files.createDirectories(path);
                } else {
                    Files.createFile(path);
                }
                System.out.println("Created missing " + description);
            }
        } catch (IOException e) {
            throw new ZettelException("Failed to create " + description + ": " + e.getMessage());
        }
    }

    private void detectOrphans(Path notesDir, Set<String> expectedFiles, String repoName) throws ZettelException {
        try (DirectoryStream<Path> notesStream = Files.newDirectoryStream(notesDir, "*.txt")) {
            List<String> orphans = new ArrayList<>();
            // for every .txt in notes/, if it is not referenced in index.txt, add it to orphans list
            for (Path p : notesStream) {
                if (!expectedFiles.contains(p.getFileName().toString())) {
                    orphans.add(p.getFileName().toString());
                }
            }
            if (!orphans.isEmpty()) {
                System.out.println("Notice: Found " + orphans.size() + " orphan note file(s) in repo '" + repoName + "':");
                orphans.forEach(f -> System.out.println("  - " + f));
            }
        } catch (IOException e) {
            throw new ZettelException("Failed to scan notes directory for orphans: " + e.getMessage());
        }
    }

    /**
     * Creates a new repository with notes and archive folders, and an index file.
     * Prints a message if the repository already exists.
     *
     * @param repoName The name of the repository to create.
     */
    public void createRepo(String repoName) {
        Path repoPath = rootPath.resolve(repoName);
        if (Files.exists(repoPath)) {
            System.out.println("Repository /"+ repoName + " already exists.");
            return;
        }
        try {
            Files.createDirectories(repoPath.resolve(REPO_NOTES));
            Files.createDirectories(repoPath.resolve(REPO_ARCHIVE));
            Files.createFile(repoPath.resolve(REPO_INDEX));

            logger.info("Repository " + repoName + " successfully created at " + repoPath);
        } catch (IOException e) {
            System.out.println("Error initialising repository " + repoName) ;
        }
    }

    /**
     * Switches the current repository and updates the config file.
     *
     * @param newRepo The repository to switch to.
     */
    public void changeRepo(String newRepo) {
        if (!repoList.contains(newRepo)) {
            System.out.println("Repo '" + newRepo + "' does not exist. Falling back to 'main'.");
            newRepo = "main";
        }

        this.repoName = newRepo;

        try {
            updateConfig(newRepo);
        } catch (ZettelException e) {
            System.out.println("Error switching repo: " + e.getMessage());
        }
    }

    /**
     * Saves a list of notes to the current repository.
     *
     * @param notes The notes to save.
     */
    public void save(List<Note> notes) {
        Path indexDir = rootPath.resolve(repoName);   // e.g data/<repoName>
        Path filePath = indexDir.resolve(REPO_INDEX); // data/<repoName>/index.txt

        /* TODO: validateRepo here, once implemented */
        try {
            Files.createDirectories(indexDir); // Ensure directory exists

            List<String> lines = notes.stream()
                    .map(this::toSaveFormat)
                    .collect(Collectors.toList());

            Files.write(filePath, lines);
        } catch (IOException e) {
            System.out.println("Error writing to index file: " + e.getMessage());
        }
    }


    /**
     * Converts a Note object into a string format for saving.
     *
     * @param note The note to convert
     * @return A string representation of the note for file storage
     */
    private String toSaveFormat(Note note) {
        String logsStr = String.join(";;", note.getLogs());
        String filename = note.getFilename() != null ? note.getFilename() : "";
        String archiveName = note.getArchiveName() != null ? note.getArchiveName() : "";

        return String.format("%s | %s | %s | %s | %s | %s | %s | %s | %s",
                note.getId(),
                note.getTitle(),
                filename,
                note.getCreatedAt().toString(),
                note.getModifiedAt().toString(),
                note.isPinned() ? "1" : "0",
                note.isArchived() ? "1" : "0",
                archiveName,
                logsStr);
    }

    /**
     * Parses a single line from the storage file into a Note object.
     * Returns null if the line is empty or corrupted.
     *
     * @param line The line from the storage file
     * @return A reconstructed Note object, or null if parsing fails
     */
    private Note parseSaveFile(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        String[] fields = line.split(" \\| ", -1);
        try {
            String id = fields[0];
            String title = fields[1];
            String filename = fields[2];
            String body = ""; // set temporary empty body
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
