package seedu.duke;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import seedu.duke.tasks.Deadline;
import seedu.duke.tasks.Event;
import seedu.duke.tasks.Task;
import seedu.duke.tasks.ToDo;

public class Storage {
    private final Path filePath;

    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

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

    private Task parseSaveFile(String line) {
        if (line.isBlank()) {
            return null;
        }

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

