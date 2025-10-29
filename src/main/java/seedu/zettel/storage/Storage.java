package seedu.zettel.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import seedu.zettel.Note;
import seedu.zettel.exceptions.ZettelException;
import java.util.logging.Logger;

/**
 * Orchestrates storage operations for Zettel repositories.
 * Manages repository state and coordinates file system and serialization operations.
 */
public class Storage {
    /** Default repository name used when no other repository is specified. */
    static final String DEFAULT_REPO = "main";

    private static final Logger logger = Logger.getLogger(Storage.class.getName());

    private final FileSystemManager fileSystemManager;
    private final NoteSerializer noteSerializer;

    private String repoName = DEFAULT_REPO;
    private ArrayList<String> repoList = new ArrayList<>();
    /**
     * Constructs a Storage instance with the specified root path.
     *
     * @param rootPath the root directory path where repositories will be stored
     */
    public Storage(String rootPath) {
        assert rootPath != null : "Root path should not be null";
        this.fileSystemManager = new FileSystemManager(rootPath);
        this.noteSerializer = new NoteSerializer();
    }

    /**
     * Initializes the storage system by creating necessary directories and files.
     * Loads configuration and validates all repositories.
     */
    public void init() {
        fileSystemManager.createRootFolder();
        fileSystemManager.createConfigFile(DEFAULT_REPO);

        Path defaultRepoPath = fileSystemManager.getRootPath().resolve(DEFAULT_REPO);
        if (Files.notExists(defaultRepoPath)) {
            createRepo(DEFAULT_REPO);
        }

        try {
            loadConfig();
            String checkedOutRepo = readCurrRepo();
            changeRepo(checkedOutRepo);

            for (String repo : repoList) {
                validateRepo(repo);
            }
            updateTagsOnInit();
        } catch (ZettelException e) {
            System.out.println("Error during init: " + e.getMessage());
        }
    }

    /**
     * Reads the currently checked-out repository from the configuration file.
     *
     * @return the name of the current repository, or DEFAULT_REPO if not found
     */
    public String readCurrRepo() {
        Path configFile = fileSystemManager.getConfigPath();
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

    /**
     * Reads the tags line from the global tags.txt file.
     *
     * @return a list of tags, or an empty list if no tags are configured
     */
    public List<String> readTagsLine() {
        Path tagsFile = fileSystemManager.getRootPath().resolve(FileSystemManager.TAGS_FILE);
        try {
            if (Files.exists(tagsFile)) {
                return Files.readAllLines(tagsFile, StandardCharsets.UTF_8)
                        .stream()
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            }
        } catch (IOException e) {
            System.out.println("Warning: failed to read tags.txt: " + e.getMessage());
        }

        return new ArrayList<>();
    }

    /**
     * Creates a storage file for the specified note in the current repository.
     *
     * @param note the note to create a storage file for
     */
    public void createStorageFile(Note note) {
        fileSystemManager.createNoteFile(note.getFilename(), note.getBody(), repoName);
    }

    /**
     * Loads the repository configuration from the config file.
     * Populates the repoList with available repositories.
     */
    public void loadConfig() {
        fileSystemManager.createConfigFile(DEFAULT_REPO);
        Path configFile = fileSystemManager.getConfigPath();

        try {
            List<String> lines = Files.readAllLines(configFile);
            String firstLine = lines.isEmpty() ? DEFAULT_REPO : lines.get(0);
            repoList = Arrays.stream(firstLine.split("\\|"))
                    .map(String::trim)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            System.out.println("Error reading .zettelConfig, defaulting to main: " + e.getMessage());
            repoList = new ArrayList<>();
            repoList.add("main");
        }
    }

    /**
     * Updates the configuration file with the specified repository as current.
     *
     * @param newRepo the repository to set as current
     * @throws ZettelException if there's an error writing to the config file
     */
    public void updateConfig(String newRepo) throws ZettelException {
        logger.info("Updating config with current repository: " + newRepo);
        fileSystemManager.createConfigFile(DEFAULT_REPO);
        Path configFile = fileSystemManager.getConfigPath();

        try (Stream<String> stream = Files.lines(configFile)) {
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
            logger.info("Config updated successfully");
        } catch (IOException e) {
            logger.warning("Failed to update checked-out repo in .zettelConfig: " + e.getMessage());
            throw new ZettelException("Failed to update checked-out repo in .zettelConfig: " + e.getMessage());
        }
    }

    /**
     * Updates the tags file on loading.
     *
     * @param tags the list of tags to write to the config file
     * @throws ZettelException if there's an error writing to the config file
     */
    public void updateTags(List<String> tags) throws ZettelException {
        logger.info("Updating tags in tags.txt: " + tags);

        fileSystemManager.validateTagsFile(); // Ensure tags file exists
        Path rootPath= fileSystemManager.getRootPath();
        Path tagsFile = rootPath.resolve(FileSystemManager.TAGS_FILE);

        try {
            Files.write(tagsFile, tags, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ZettelException("Failed to update tags line in .tags.txt: " + e.getMessage());
        }
    }

    /**
     * Updates the global tags.txt file with tags collected from all repositories.
     * <p>
     * - Ensures tags.txt exists (creates if missing).<br>
     * - Preserves existing tags.<br>
     * - Adds any missing tags found in notes across all repositories.<br>
     * - Writes one tag per line (duplicates automatically removed).
     *
     * @throws ZettelException if tags file cannot be created or read
     */
    private void updateTagsOnInit() throws ZettelException {
        fileSystemManager.validateTagsFile(); // Ensure tags file exists

        // Get tags file path
        Path rootPath = fileSystemManager.getRootPath();
        Path tagsFile = rootPath.resolve(FileSystemManager.TAGS_FILE);

        // Read existing tags
        Set<String> tags = new HashSet<>();
        try {
            List<String> lines = Files.readAllLines(tagsFile, StandardCharsets.UTF_8);
            for (String line: lines) {
                String tag = line.trim();
                tags.add(tag);
            }
        } catch (IOException e) {
            throw new ZettelException("Failed to read existing tags: " + e.getMessage());
        }

        for (String repoName: repoList) {
            Path indexPath = fileSystemManager.getIndexPath(repoName);
            Path notesDir = fileSystemManager.getNotesPath(repoName);

            List<Note> repoNotes = noteSerializer.loadNotes(indexPath,notesDir);
            for (Note note: repoNotes) {
                tags.addAll(note.getTags());
            }
        }

        List<String> tagsList = new ArrayList<String>(tags);
        try {
            Files.write(tagsFile, tagsList, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ZettelException("Failed to update existing tags: " + e.getMessage());
        }
    }

    /**
     * Loads all notes from the current repository.
     *
     * @return an ArrayList of notes loaded from the repository
     */
    public ArrayList<Note> load() {
        Path indexPath = fileSystemManager.getIndexPath(repoName);
        Path notesDir = fileSystemManager.getNotesPath(repoName);

        try {
            validateRepo(repoName);
        } catch (ZettelException e) {
            System.out.println("Error validating repo: " + e.getMessage());
            return new ArrayList<>();
        }

        return noteSerializer.loadNotes(indexPath, notesDir);
    }

    /**
     * Validates the structure of the specified repository.
     *
     * @param repoName the name of the repository to validate
     * @throws ZettelException if the repository structure is invalid
     */
    private void validateRepo(String repoName) throws ZettelException {
        Path indexPath = fileSystemManager.getIndexPath(repoName);
        List<String> expectedFiles = noteSerializer.getExpectedFilenames(indexPath);
        fileSystemManager.validateRepoStructure(repoName, expectedFiles);
    }

    /**
     * Creates a new repository with the specified name.
     *
     * @param repoName the name of the repository to create
     */
    public void createRepo(String repoName) {
        logger.info("Creating repository: " + repoName);
        boolean created = fileSystemManager.createRepoStructure(repoName);
        if (created) {
            addToConfig(repoName);
            logger.info("Repository " + repoName + " successfully created");
        } else {
            logger.warning("Repository " + repoName + " already exists or creation failed");
        }
    }

    /**
     * Adds a repository to the configuration file.
     *
     * @param repoName the name of the repository to add
     */
    private void addToConfig(String repoName) {
        fileSystemManager.createConfigFile(DEFAULT_REPO);
        Path configFile = fileSystemManager.getConfigPath();

        try {
            List<String> lines = Files.readAllLines(configFile);

            String firstLine = lines.isEmpty() ? DEFAULT_REPO : lines.get(0);
            String secondLine = (lines.size() < 2) ? DEFAULT_REPO : lines.get(1);

            if (!firstLine.contains(repoName)) {
                firstLine = firstLine.concat(" | " + repoName);
                Files.write(configFile, Arrays.asList(firstLine, secondLine));

                if (!repoList.contains(repoName)) {
                    repoList.add(repoName);
                }
            }
        } catch (IOException e) {
            System.out.println("Error updating .zettelConfig: " + e.getMessage());
        }
    }


    /**
     * Changes the current repository to the specified repository.
     *
     * @param newRepo the name of the repository to switch to
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
     * Saves the list of notes to the current repository.
     *
     * @param notes the list of notes to save
     */
    public void save(List<Note> notes) {
        Path indexPath = fileSystemManager.getIndexPath(repoName);

        try {
            Files.createDirectories(indexPath.getParent());
            noteSerializer.saveNotes(notes, indexPath);
            validateRepo(repoName);
        } catch (IOException e) {
            System.out.println("Error writing to index file: " + e.getMessage());
        } catch (ZettelException e) {
            System.out.println("Error while validating repo: " + e.getMessage());
        }
    }

    public Path getNotePath(String filename) {
        return fileSystemManager.getNotesPath(repoName).resolve(filename);
    }

    /**
     * Deletes note's body text from the current repository.
     *
     * @param filename the name of the note of body text to delete
     * @throws ZettelException if there's an error deleting the file
     */
    public void deleteStorageFile(String filename) throws ZettelException {
        Path notesDir = fileSystemManager.getNotesPath(repoName);
        Path noteFile = notesDir.resolve(filename);
        try {
            if (Files.exists(noteFile)) {
                Files.delete(noteFile);
            }

        } catch (IOException e) {
            throw new ZettelException("Error while deleting body file '" + filename + "': " + e.getMessage());
        }
    }
}
