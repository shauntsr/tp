package seedu.zettel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ZettelTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final ByteArrayInputStream inContent = new ByteArrayInputStream("James Gosling".getBytes());

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
        assertTrue(output.contains("Hello from ZettelCLI"));
        assertTrue(output.contains("What is your name?"));
        assertTrue(output.contains("Hello James Gosling"));
    }
}
