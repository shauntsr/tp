package seedu.zettel.commands;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.zettel.UI;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class InitCommandTest {
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOutputStream = System.out;

    private UI ui;

    @BeforeEach
    void setup() {
        System.setOut(new PrintStream(outputStream));
        ui = new UI();
    }

    @AfterEach
    void teardown() {
        System.setOut(originalOutputStream);
    }

    @Test
    void testNewRepoInitShowsCreatedMessage() {
        String repoName = "myRepo";
        ui.showRepoInit(repoName);

        String output = outputStream.toString().trim();
        assertTrue(output.contains("Repository /" + repoName + " has been created."));
    }
}
