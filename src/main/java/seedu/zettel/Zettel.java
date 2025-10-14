package seedu.zettel;

import seedu.duke.exceptions.ZettelException;
import seedu.zettel.commands.Command;

import java.util.ArrayList;

public class Zettel {
    private ArrayList<Note> notes;
    private UI ui;
    private boolean isRunning;

    /**
     * Constructor for Zettel application.
     * Initializes the UI and notes list.
     */
    public Zettel() {
        this.ui = new UI();
        this.notes = new ArrayList<>();
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
                if (userInput.isEmpty()) {
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

                // Execute the command (without storage for now)
                command.execute(notes, ui, null);

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