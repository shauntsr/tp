package seedu.zettel.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import seedu.zettel.exceptions.EditorNotFoundException;
import seedu.zettel.exceptions.NoNoteFoundException;

/**
 * CLI utility to open an existing file in the user's preferred text editor.
 * <p>
 * Priority order for editor selection:
 *  1) $VISUAL
 *  2) $EDITOR
 *  3) fallback editors: vim, nano, vi
 */
public final class EditorUtil {

    /**
     * Opens an existing file in the user's preferred text editor.
     * Attempts to use $VISUAL or $EDITOR environment variables first,
     * then falls back to common CLI editors (vim, nano, vi).
     *
     * @param filePath path to the file to open
     * @throws EditorNotFoundException if no editor could be launched
     * @throws InterruptedException if interrupted while waiting for the editor to close
     * @throws NoNoteFoundException if the target file does not exist
     */
    public static void openInEditor(Path filePath)
            throws EditorNotFoundException, InterruptedException, NoNoteFoundException {

        File file = filePath.toFile();
        if (!file.exists()) {
            throw new NoNoteFoundException("Note does not exist at " + filePath);
        }

        if (System.console() == null) {
            // No interactive console available (IDE Run, background process, etc.)
            throw new EditorNotFoundException(
                    "No interactive terminal available. " +
                    "Run the program from a real terminal/console or supply the note body via -b.");
        }

        // Try environment variables first
        String[] envVars = {"VISUAL", "EDITOR"};
        for (String var : envVars) {
            String editor = System.getenv(var);
            if (editor != null && !editor.isBlank()) {
                if (tryLaunchEditor(editor, file)) {
                    return;
                }
            }
        }

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // If on Windows, try notepad
            if (tryLaunchEditor("notepad.exe", file)) {
                return;
            }
        }

        // Try common CLI editors, as fallback
        String[] commonEditors = {"vim", "nano", "vi"};
        for (String editor : commonEditors) {
            if (tryLaunchEditor(editor, file)) {
                return;
            }
        }

        throw new EditorNotFoundException(
            "No suitable text editor found. Tried: $VISUAL, $EDITOR, vim, nano, vi"
        );
    }

    /**
     * Attempts to launch the specified editor with the given file.
     *
     * @param editor the editor command to run
     * @param file the file to open
     * @return true if the editor was successfully launched and exited, false otherwise
     * @throws InterruptedException if interrupted while waiting for the editor
     */
    private static boolean tryLaunchEditor(String editor, File file) throws InterruptedException {
        try {
            Process process = new ProcessBuilder(editor, file.getAbsolutePath())
                    .inheritIO()
                    .start();
            int exitCode = process.waitFor();
            return exitCode == 0 || exitCode == 1;
        } catch (IOException e) {
            // Return false to try next editor
            return false;
        }
    }
}
