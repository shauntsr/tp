package seedu.zettel.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import seedu.zettel.Note;
import seedu.zettel.exceptions.ZettelException;


/**
 * Orchestrates storage operations for Zettel repositories.
 * Manages repository state and coordinates file system and serialization operations.
 */
public class Storage {
    static final String DEFAULT_REPO = "main";

    private static final Logger logger = Logger.getLogger(Storage.class.getName());

    private final FileSystemManager fileSystemManager;
    private final NoteSerializer noteSerializer;

    private String repoName = DEFAULT_REPO;
    private ArrayList<String> repoList = new ArrayList<>();

    public Storage(String rootPath) {
        this.fileSystemManager = new FileSystemManager(rootPath);
        this.noteSerializer = new NoteSerializer();
    }

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
        } catch (ZettelException e) {
            System.out.println("Error during init: " + e.getMessage());
        }
    }

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

    public void createStorageFile(Note note) {
        fileSystemManager.createNoteFile(note.getFilename(), note.getBody(), repoName);
    }

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

    public void updateConfig(String newRepo) throws ZettelException {
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
        } catch (IOException e) {
            throw new ZettelException("Failed to update checked-out repo in .zettelConfig: " + e.getMessage());
        }
    }

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

    private void validateRepo(String repoName) throws ZettelException {
        Path indexPath = fileSystemManager.getIndexPath(repoName);
        List<String> expectedFiles = noteSerializer.getExpectedFilenames(indexPath);
        fileSystemManager.validateRepoStructure(repoName, expectedFiles);
    }

    public void createRepo(String repoName) {
        boolean created = fileSystemManager.createRepoStructure(repoName);
        if (created) {
            addToConfig(repoName);
            logger.info("Repository " + repoName + " successfully created");
        }
    }

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
}
