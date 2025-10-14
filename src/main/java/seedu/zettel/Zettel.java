package seedu.zettel;

import seedu.zettel.commands.Command;
import seedu.zettel.exceptions.ZettelException;

import java.util.ArrayList;

/**
 * Main class for the Zettel CLI application.
 * Handles initialization, storage setup, and the main program loop.
 */
public class Zettel {
    private static final String DATA_FILE_PATH = "data/notes.txt";

    private Storage storage;
    private ArrayList<Note> notes;
    private UI ui;
    private boolean isRunning;

    /**
     * Constructor for Zettel application.
     * Initializes the UI, storage, and loads existing notes.
     */
    public Zettel() {
        this.ui = new UI();
        this.storage = new Storage(DATA_FILE_PATH);
        this.notes = storage.load();
        this.isRunning = true;
    }

    /**
     * Runs the main application loop.
     * Displays greeting, processes commands until exit command is received.
     */
    public void run() {
        ui.showWelcome();

        while (isRunning) {
            try {
                String userInput = ui.readCommand();

                // Skip empty input
                if (userInput.trim().isEmpty()) {
                    continue;
                }

                // Parse the command
                Command command = Parser.parse(userInput);

                // Check if it's an exit command
                if (command.isExit()) {
                    isRunning = false;
                    ui.showBye();
                    break;
                }

                // Execute the command
                command.execute(notes, ui, storage);

                // Save notes after each command (auto-save)
                storage.save(notes);

            } catch (ZettelException e) {
                ui.showError(e.getMessage());
            } catch (Exception e) {
                ui.showError("An unexpected error occurred: " + e.getMessage());
            }
        }

        ui.close();
    }

    /**
     * Main entry-point for the Zettel application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        new Zettel().run();
    }
}
