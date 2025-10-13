package chatbot;

import chatbot.commands.Command;
import chatbot.exceptions.CoachException;
import chatbot.tasks.Task;

import java.util.ArrayList;

/**
 * Main chatbot application that inputs user interactions to coordinate task management.
 * A Coach object handles the initialization of storage, parser and UI components,
 * and provides the main execution loop for parsing and executing user commands.
 */
public class Coach {
    private static final String FILE_PATH = "./data/coach.txt";

    private final Storage storage;
    private final ArrayList<Task> tasks;
    private final UI ui;

    /**
     * Constructs a Coach chatbot with the specified file path for data storage.
     * Initializes the UI, storage, and loads existing tasks from the file.
     *
     * @param filePath The path to the file where tasks are stored.
     */
    public Coach(String filePath) {
        ui = new UI();
        storage = new Storage(filePath);
        tasks = storage.load();
    }

    /**
     * Runs the main execution loop of the chatbot.
     * Displays the welcome message, continuously reads and processes user commands
     * until an exit command is received. Handles exceptions by displaying error messages
     * and ensures proper cleanup by closing the UI when done.
     */
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

    /**
     * The entry point of the Coach chatbot application.
     * Creates a new Coach instance with the default file path and starts the application.
     */
    public static void main(String[] args) {
        new Coach(FILE_PATH).run();
    }
}

