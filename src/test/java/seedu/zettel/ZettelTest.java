package seedu.zettel;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Zettel main class.
 */
class ZettelTest {

    private static final Path DATA_DIR = Path.of("data");
    private static final Path NOTES_FILE = DATA_DIR.resolve("notes.txt");

    @BeforeEach
    void setUp() throws Exception {
        // Ensure data directory exists before each test
        Files.createDirectories(DATA_DIR);
    }

    @AfterEach
    void tearDown() {
        // Clean up data files after each test
        try {
            Files.deleteIfExists(NOTES_FILE);
            Files.deleteIfExists(DATA_DIR);
        } catch (Exception e) {
            System.err.println("Cleanup failed: " + e.getMessage());
        }
    }

    @Test
    void testConstructorInitializesDependenciesSuccessfully() {
        assertDoesNotThrow(() -> {
            Zettel zettel = new Zettel();
            assertNotNull(zettel, "Zettel instance should not be null");
        });
    }

    @Test
    void testConstructorCreatesUIAndStorageAndNotesList() {
        Zettel zettel = new Zettel();

        // Use reflection to access private fields
        try {
            var uiField = Zettel.class.getDeclaredField("ui");
            var storageField = Zettel.class.getDeclaredField("storage");
            var notesField = Zettel.class.getDeclaredField("notes");

            uiField.setAccessible(true);
            storageField.setAccessible(true);
            notesField.setAccessible(true);

            assertNotNull(uiField.get(zettel), "UI should be initialized");
            assertNotNull(storageField.get(zettel), "Storage should be initialized");
            assertNotNull(notesField.get(zettel), "Notes list should be initialized");
        } catch (ReflectiveOperationException e) {
            fail("Reflection access failed: " + e.getMessage());
        }
    }

    @Test
    void testMainMethodExistsAndDoesNotThrow() {
        assertDoesNotThrow(() -> {
            Zettel.class.getDeclaredMethod("main", String[].class);
        }, "Zettel should have a main method");
    }

    @Test
    void testMultipleInstancesCanBeCreatedIndependently() {
        assertDoesNotThrow(() -> {
            Zettel z1 = new Zettel();
            Zettel z2 = new Zettel();
            assertNotSame(z1, z2, "Each Zettel instance should be unique");
        });
    }
}
