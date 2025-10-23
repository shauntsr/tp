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

    static final String CONFIG_FILE = ".zettelConfig";
    static final String REPO_NOTES = "notes";
    static final String REPO_ARCHIVE = "archive";
    static final String REPO_INDEX = "index.txt";

    private final Path rootPath;

    public FileSystemManager(String rootPath) {
        this.rootPath = Paths.get(rootPath);
    }

    public Path getRootPath() {
        return rootPath;
    }

    public void createRootFolder() {
        try {
            if (Files.notExists(rootPath)) {
                Files.createDirectories(rootPath);
            }
        } catch (IOException e) {
            System.out.println("Error creating " + rootPath + " folder.");
        }
    }

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

    public void createNoteFile(String noteFilename, String noteBody, String repoName) {
        Path repoPath = rootPath.resolve(repoName);
        Path notesDir = repoPath.resolve(REPO_NOTES);

        try {
            Path noteFile = notesDir.resolve(noteFilename);
            if (Files.notExists(noteFile)) {
                Files.createFile(noteFile);
                System.out.println("Created note file: " + noteFilename);
            } else {
                System.out.println("Note file already exists. Overwriting... " + noteFile);
            }

            Files.writeString(noteFile, noteBody != null ? noteBody : "");

        } catch (IOException e) {
            System.out.println("Error writing note file: " + e.getMessage());
        }
    }

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
            throw new ZettelException("Failed to scan notes directory for orphans: " + e.getMessage());
        }
    }

    public Path getIndexPath(String repoName) {
        return rootPath.resolve(repoName).resolve(REPO_INDEX);
    }

    public Path getNotesPath(String repoName) {
        return rootPath.resolve(repoName).resolve(REPO_NOTES);
    }

    public Path getConfigPath() {
        return rootPath.resolve(CONFIG_FILE);
    }

}
