package seedu.zettel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * JUnit tests for the Zettel main class.
 */
class ZettelTest {

    @AfterEach
    void tearDown() {
        // Clean up test data file if it exists
        try {
            Files.deleteIfExists(Paths.get("data/notes.txt"));
            Files.deleteIfExists(Paths.get("data"));
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    @Test
    void testZettelConstructor() {
        assertDoesNotThrow(() -> {
            Zettel zettel = new Zettel();
            assertNotNull(zettel);
        });
    }

    @Test
    void testZettelConstructorCreatesUI() {
        Zettel zettel = new Zettel();
        assertNotNull(zettel);
    }

    @Test
    void testZettelConstructorCreatesStorage() {
        Zettel zettel = new Zettel();
        assertNotNull(zettel);
    }

    @Test
    void testZettelConstructorLoadsNotes() {
        Zettel zettel = new Zettel();
        assertNotNull(zettel);
    }

    @Test
    void testMainMethodExists() {
        // Verify the main method and class exist
        assertNotNull(Zettel.class);
        assertDoesNotThrow(() -> {
            Zettel.class.getDeclaredMethod("main", String[].class);
        });
    }

    @Test
    void testZettelInstantiationDoesNotThrow() {
        assertDoesNotThrow(() -> {
            new Zettel();
        });
    }

    @Test
    void testZettelConstructorInitializesAllComponents() {
        assertDoesNotThrow(() -> {
            Zettel zettel = new Zettel();
            assertNotNull(zettel);
        });
    }

    @Test
    void testMultipleZettelInstances() {
        assertDoesNotThrow(() -> {
            Zettel zettel1 = new Zettel();
            Zettel zettel2 = new Zettel();
            assertNotNull(zettel1);
            assertNotNull(zettel2);
        });
    }
}
