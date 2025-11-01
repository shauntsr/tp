package seedu.zettel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import seedu.zettel.commands.Command;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

/**
 * Main class for the Zettel CLI application.
 * Handles initialization, storage setup, and the main program loop.
 * Zettel is a note-taking system that uses hash-based IDs for notes.
 */
public class Zettel {

    private static final String DATA_FILE_PATH = "data/";
    private static final int READ_TIMEOUT_SECONDS = 240;

    private Storage storage;
    private ArrayList<Note> notes;
    private List<String> tags;
    private UI ui;
    private boolean isRunning;

    /**
     * Constructor for Zettel application.
     * Initializes the UI, storage, and loads existing notes from disk.
     */
    public Zettel() {
        this.ui = new UI();
        this.storage = new Storage(DATA_FILE_PATH);
        storage.init();
        this.notes = storage.load();
        this.tags = storage.readTagsLine();
        this.isRunning = true;
    }

    /**
     * Runs the main application loop.
     * Displays greeting, processes commands until exit command is received.
     * Implements timeout handling to prevent hanging in CI environments.
     */
    public void run() {

        ui.showWelcome();
        ExecutorService executor = Executors.newSingleThreadExecutor();

        while (isRunning) {
            try {
                // Read command with timeout
                Future<String> future = executor.submit(() -> ui.readCommand());
                String userInput;

                try {
                    userInput = future.get(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    future.cancel(true);
                    ui.showError("Input timeout. Exiting...");
                    break;
                }

                // Skip empty input
                if (userInput.trim().isEmpty()) {
                    continue;
                }

                // Parse the command
                Command command = Parser.parse(userInput);

                // Check if it's an exit command
                if (command.isExit()) {
                    isRunning = false;
                    break;
                }

                // Execute the command
                command.execute(notes, tags, ui, storage);

                // Save notes after each command (auto-save)
                storage.save(notes);

            } catch (ZettelException e) {
                ui.showError(e.getMessage());
            } catch (Exception e) {
                ui.showError("An unexpected error occurred: " + e.getMessage());
                break;
            }
        }

        executor.shutdownNow();
        ui.close();
    }

    /**
     * Main entry-point for the Zettel application.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        new Zettel().run();
    }
}
