package seedu.zettel.util;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.zettel.exceptions.EditorNotFoundException;
import seedu.zettel.exceptions.NoNoteFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


class EditorUtilTest {

    @TempDir
    Path tempDir;

    @Test
    void testFileDoesNotExist_throwsNoNoteFoundException() {
        Path nonExistentFile = tempDir.resolve("nonexistent.txt");

        NoNoteFoundException exception = assertThrows(
            NoNoteFoundException.class,
            () -> EditorUtil.openInEditor(nonExistentFile)
        );

        assertTrue(exception.getMessage().contains("Note does not exist at"));
        assertTrue(exception.getMessage().contains(nonExistentFile.toString()));
    }

    @Test
    void testNoConsoleAvailable_throwsEditorNotFoundException() throws IOException {
        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "test content");

        // When running in JUnit (no console), should throw EditorNotFoundException
        EditorNotFoundException exception = assertThrows(
            EditorNotFoundException.class,
            () -> EditorUtil.openInEditor(testFile)
        );

        assertTrue(exception.getMessage().contains("No interactive terminal available"));
    }

    @Test
    void testNullPath_throwsNullPointerException() {
        assertThrows(
            NullPointerException.class,
            () -> EditorUtil.openInEditor(null)
        );
    }

}
