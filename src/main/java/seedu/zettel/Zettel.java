package seedu.zettel;

import seedu.zettel.commands.Command;
import seedu.duke.exceptions.ZettelException;
import java.time.Instant;

import java.util.ArrayList;

public class Zettel {
    private static final String FILE_PATH = "./data/zettel.txt";

    private final Storage storage;
    private final ArrayList<Note> notes;
    private final UI ui;

    public Zettel(String filePath) {
        ui = new UI();
        storage = new Storage(filePath);
        notes = storage.load();
    }

    public void run() {
        ui.showWelcome();
        if (notes.isEmpty()) {
            Instant timestamp = Instant.parse("2025-10-14T10:00:00Z");
            Note testNote = new Note("abc123", "Test Note", "test.txt",
                    "This is a test note body", timestamp, timestamp);
            notes.add(testNote);
            storage.save(notes);
            System.out.println(notes);
        }

        System.out.println("Total notes: " + notes.size());
        boolean isExit = false;

        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                if (fullCommand.isEmpty()) {
                    break;
                }
                ui.printLine();
                Command c = Parser.parse(fullCommand);
                c.execute(notes, ui, storage);
                isExit = c.isExit();
            } catch (ZettelException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.printLine();
            }
        }
        ui.close();
    }

    public static void main(String[] args) {
        new Zettel(FILE_PATH).run();
    }
}
