package chatbot;

import java.io.*;
import java.util.*;

import chatbot.tasks.Deadline;
import chatbot.tasks.Event;
import chatbot.tasks.Task;
import chatbot.tasks.ToDo;

public class Storage {
    private final String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

    public ArrayList<Task> load() {
        ArrayList<Task> loadedTasks = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            return loadedTasks;
        }

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                Task task = parseLine(line);
                if (task != null) {
                    loadedTasks.add(task);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
        }

        return loadedTasks;
    }

    public void save(ArrayList<Task> tasks) {
        try {
            File file = new File(filePath);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            PrintWriter pw = new PrintWriter(new FileWriter(file));
            for (Task t : tasks) {
                pw.println(t.toSaveFormat());
            }
            pw.close();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    private Task parseLine(String line) {
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
                String timings = fields[3];
                String fromTime;
                String toTime;
                String[] times = timings.split("/to", 2);
                fromTime = times[0].trim();
                toTime = times[1].trim();
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