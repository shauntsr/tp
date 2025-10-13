package seedu.duke;

import seedu.duke.commands.Command;
import seedu.duke.exceptions.CoachException;
import seedu.duke.tasks.Task;

import java.util.ArrayList;

public class Coach {
    private static final String FILE_PATH = "./data/coach.txt";

    private final Storage storage;
    private final ArrayList<Task> tasks;
    private final UI ui;

    public Coach(String filePath) {
        ui = new UI();
        storage = new Storage(filePath);
        tasks = storage.load();
    }

    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.printLine();
                Command c = Parser.parse(fullCommand);
                c.execute(tasks, ui, storage);
                isExit = c.isExit();
            } catch (CoachException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.printLine();
            }
        }
        ui.close();
    }

    public static void main(String[] args) {
        new Coach(FILE_PATH).run();
    }
}
