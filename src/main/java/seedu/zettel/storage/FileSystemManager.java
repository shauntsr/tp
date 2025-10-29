package seedu.zettel.storage;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import seedu.zettel.exceptions.InvalidRepoException;
import seedu.zettel.exceptions.ZettelException;

/**
 * Manages file system operations for Zettel repositories.
 * Handles directory creation, validation, and file management.
 */
public class FileSystemManager {

    /** Configuration file name for storing repository settings. */
    static final String CONFIG_FILE = ".zettelConfig";

    /** File name for storing all tags globally. */
    static final String TAGS_FILE = "tags.txt";

    /** Directory name for storing note body files within a repository. */
    static final String REPO_NOTES = "notes";

    /** Directory name for storing archived notes within a repository. */
    static final String REPO_ARCHIVE = "archive";

    /** File name for the repository index that stores note metadata. */
    static final String REPO_INDEX = "index.txt";

    private final Path rootPath;

    /**
     * Constructs a FileSystemManager with the specified root path.
     *
     * @param rootPath the root directory path for all repositories
     */
    public FileSystemManager(String rootPath) {
        this.rootPath = Paths.get(rootPath);
    }

    /**
     * Gets the root path of the file system manager.
     *
     * @return the root directory path
     */
    public Path getRootPath() {
        return rootPath;
    }

    /**
     * Creates the root folder if it doesn't exist.
     */
    public void createRootFolder() {
        try {
            if (Files.notExists(rootPath)) {
                Files.createDirectories(rootPath);
            }
        } catch (IOException e) {
            System.out.println("Error creating " + rootPath + " folder.");
        }
    }

    /**
     * Creates the configuration file with default repository settings if it doesn't exist.
     *
     * @param defaultRepo the default repository name to use in the config file
     */
    public void createConfigFile(String defaultRepo) {
        Path configPath = rootPath.resolve(CONFIG_FILE);
        try {
            if (Files.notExists(configPath)) {
                Files.createFile(configPath);
                List<String> lines = List.of(defaultRepo, defaultRepo);
                Files.write(configPath, lines);
            }
        } catch (IOException e) {
            System.out.println("Error creating " + CONFIG_FILE + ".");
        }
    }


    /**
     * Creates the directory structure for a new repository.
     *
     * @param repoName the name of the repository to create
     * @return true if the repository was created successfully, false if it already exists
     */
    public boolean createRepoStructure(String repoName) {
        Path repoPath = rootPath.resolve(repoName);
        if (Files.exists(repoPath)) {
            System.out.println("Repository /" + repoName + " already exists.");
            return false;
        }
        try {
            Files.createDirectories(repoPath.resolve(REPO_NOTES));
            Files.createDirectories(repoPath.resolve(REPO_ARCHIVE));
            Files.createFile(repoPath.resolve(REPO_INDEX));
            return true;
        } catch (IOException e) {
            System.out.println("Error initialising repository " + repoName);
            return false;
        }
    }

    /**
     * Creates a note file with the specified content in the given repository.
     *
     * @param noteFilename the name of the note file to create
     * @param noteBody the content to write to the note file
     * @param repoName the repository where the note file should be created
     */
    public void createNoteFile(String noteFilename, String noteBody, String repoName) {
        Path repoPath = rootPath.resolve(repoName);
        Path notesDir = repoPath.resolve(REPO_NOTES);

        try {
            Path noteFile = notesDir.resolve(noteFilename);
            if (Files.notExists(noteFile)) {
                Files.createFile(noteFile);
            } else {
                System.out.println("Note file already exists. Overwriting... " + noteFile);
            }

            Files.writeString(noteFile, noteBody != null ? noteBody : "");

        } catch (IOException e) {
            System.out.println("Error writing note file: " + e.getMessage());
        }
    }

    /**
     * Validates the structure of a repository and creates missing components.
     *
     * @param repoName the name of the repository to validate
     * @param expectedFiles the list of expected note body files
     * @throws ZettelException if the repository structure is invalid
     */
    public void validateRepoStructure(String repoName, List<String> expectedFiles) throws ZettelException {
        Path repoPath = rootPath.resolve(repoName);
        Path notesDir = repoPath.resolve(REPO_NOTES);
        Path archiveDir = repoPath.resolve(REPO_ARCHIVE);
        Path indexFile = repoPath.resolve(REPO_INDEX);
        Path configFile = rootPath.resolve(CONFIG_FILE);

        if (!Files.exists(configFile)) {
            throw new InvalidRepoException(".zettelConfig missing at " + configFile.toAbsolutePath());
        }

        createIfMissing(repoPath, "repository folder: " + repoName, true);
        createIfMissing(notesDir, "notes/ for repo: " + repoName, true);
        createIfMissing(archiveDir, "archive/ for repo: " + repoName, true);
        createIfMissing(indexFile, "index.txt for repo: " + repoName, false);

        for (String fileName : expectedFiles) {
            Path bodyFile = notesDir.resolve(fileName);
            try {
                createIfMissing(bodyFile, "body file: " + fileName, false);
            } catch (ZettelException e) {
                System.out.println("Warning: " + e.getMessage());
            }
        }

        detectOrphans(notesDir, expectedFiles, repoName);
    }

    /**
     * Ensures that the global tags file exists in the root directory.
     * <p>
     * If the file already exists, the method returns {@code true}.
     * If the file does not exist, it will be created and the method returns {@code false}.
     * </p>
     *
     * @throws ZettelException if creating the tags file fails
     */
    public void validateTagsFile() throws ZettelException{
        Path tagsFile = rootPath.resolve(TAGS_FILE);
        if (Files.exists(tagsFile)) {
        }
        createIfMissing(tagsFile, "tags.txt for storing tags", false);
    }

    /**
     * Creates a file or directory if it doesn't exist.
     *
     * @param path the path to create
     * @param description a description of what is being created
     * @param isDirectory true if creating a directory, false for a file
     * @throws ZettelException if the creation fails
     */
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
            System.out.println("Failed to create " + description + ": " + e.getMessage());
        }
    }

    /**
     * Detects orphan files (files not referenced in the index) in the notes directory.
     *
     * @param notesDir the notes directory to scan
     * @param expectedFiles the list of expected files from the index
     * @param repoName the name of the repository being scanned
     * @throws ZettelException if scanning the directory fails
     */
    private void detectOrphans(Path notesDir, List<String> expectedFiles, String repoName) throws ZettelException {
        try (DirectoryStream<Path> notesStream = Files.newDirectoryStream(notesDir, "*.txt")) {
            List<String> orphans = new ArrayList<>();
            for (Path p : notesStream) {
                if (!expectedFiles.contains(p.getFileName().toString())) {
                    orphans.add(p.getFileName().toString());
                }
            }
            if (!orphans.isEmpty()) {
                System.out.println("Notice: Found " + orphans.size() +
                        " orphan note file(s) in repo '" + repoName + "':");
                orphans.forEach(f -> System.out.println("  - " + f));
            }
        } catch (IOException e) {
            System.out.println("Failed to scan notes directory for orphans: " + e.getMessage());
        }
    }

    /**
     * Gets the path to the index file for the specified repository.
     *
     * @param repoName the repository name
     * @return the path to the repository's index file
     */
    public Path getIndexPath(String repoName) {
        return rootPath.resolve(repoName).resolve(REPO_INDEX);
    }

    /**
     * Gets the path to the notes directory for the specified repository.
     *
     * @param repoName the repository name
     * @return the path to the repository's notes directory
     */
    public Path getNotesPath(String repoName) {
        return rootPath.resolve(repoName).resolve(REPO_NOTES);
    }

    /**
     * Gets the path to the configuration file.
     *
     * @return the path to the configuration file
     */
    public Path getConfigPath() {
        return rootPath.resolve(CONFIG_FILE);
    }
}
