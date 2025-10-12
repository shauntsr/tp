package seedu.duke;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DukeTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final ByteArrayInputStream inContent = new ByteArrayInputStream("John".getBytes());
    private final String logo = " ______    _   _       _  ____ _     ___ \n" +
            "|__  / ___| |_| |_ ___| |/ ___| |   |_ _|\n" +
            "  / / / _ \\ __| __/ _ \\ | |   | |    | | \n" +
            " / /_|  __/ |_| ||  __/ | |___| |___ | | \n" +
            "/____|\\___|\\___|\\__\\___|_|\\____|_____|___|\n";

    @BeforeEach //set up environment for test
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setIn(inContent);
    }

    @AfterEach //reset environment after test
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(System.in);
    }

    @Test //test if print ascii and correct name
    public void testMainOutput() {
        Duke.main(new String[]{});
        String output = outContent.toString();

        assertTrue(output.contains("Hello from"));
        assertTrue(output.contains(logo));
        assertTrue(output.contains("What is your name?"));
        assertTrue(output.contains("Hello John"));
    }
}