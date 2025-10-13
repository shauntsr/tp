package seedu.duke;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import seedu.duke.tasks.Deadline;
import seedu.duke.tasks.Event;
import seedu.duke.tasks.Task;
import seedu.duke.tasks.ToDo;

/**
 * Manages the loading and saving of tasks to storage on device.
 * The Storage class handles file I/O operations for reading tasks
 * from a file at startup and writing tasks back to the file after modifications.
 */
public class Storage {
    private final Path filePath;

    /**
     * Constructs a Storage object with the specified file path.
     *
     * @param filePath The path to the file where tasks are stored.
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Loads tasks from the storage file.
     * Creates the parent directory and returns an empty list if the file does not exist.
     * Skips any corrupted or invalid lines in the file.
     *
     * @return An ArrayList of tasks loaded from the file, or an empty list if the file doesn't exist or an error occurs.
     */
    public ArrayList<Task> load() {
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

    /**
     * Saves the list of tasks to the storage file.
     * Creates the parent directory if it does not exist.
     * Each task is converted to its save format before writing.
     *
     * @param tasks The list of tasks to save.
     */
    public void save(List<Task> tasks) {
        try {
            Files.createDirectories(filePath.getParent()); // Ensure directory exists

            List<String> lines = tasks.stream()
                    .map(Task::toSaveFormat)
                    .collect(Collectors.toList());

            Files.write(filePath, lines);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    /**
     * Parses a single line from the storage file and converts it to a Task object.
     * The expected format is: TYPE | MARK | DESCRIPTION | TIME (if applicable)
     * where TYPE is T (ToDo), D (Deadline), or E (Event), MARK is 1 or 0 for mark status. DESCRIPTION AND TIME are strings.
     *
     * @param line The line to parse from the file.
     * @return The parsed Task object, or null if the line is blank, invalid, or corrupted.
     */
    private Task parseSaveFile(String line) {
        if (line.isBlank()) return null;

        String[] fields = line.split(" \\| ");
        try {
            String type = fields[0];
            boolean isDone = fields[1].equals("1");
            String content = fields[2].trim();

            switch (type) {
                case "T":
                    return new ToDo(content, isDone);
                case "D":
                    return new Deadline(content, isDone, fields[3]);
                case "E":
                    String[] times = fields[3].split("/to", 2);
                    String fromTime = times[0].trim();
                    String toTime = times[1].trim();
                    return new Event(content, fromTime, toTime);
                default:
                    return null;
            }
        } catch (Exception e) {
            System.out.println("Skipping corrupted line: " + line);
            return null;
        }
    }
}