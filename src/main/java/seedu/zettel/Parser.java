package seedu.zettel;

import seedu.zettel.commands.Command;
import seedu.zettel.commands.DeleteNoteCommand;
import seedu.zettel.commands.ExitCommand;
import seedu.zettel.commands.FindNoteCommand;
import seedu.zettel.commands.InitCommand;
import seedu.zettel.commands.ListNoteCommand;
import seedu.zettel.commands.NewNoteCommand;
import seedu.zettel.commands.PinNoteCommand;
import seedu.zettel.exceptions.EmptyDescriptionException;
import seedu.zettel.exceptions.InvalidFormatException;
import seedu.zettel.exceptions.InvalidInputException;
import seedu.zettel.exceptions.ZettelException;

/**
 * Parses user input and returns the corresponding Command object.
 * Validates input format and throws appropriate exceptions for invalid input.
 */
public class Parser {
    // Error message constants
    private static final String LIST_FORMAT = "List format should be: list [-p]";
    private static final String PIN_FORMAT = "Pin format should be: pin/unpin <NOTE_ID>";
    private static final String DELETE_FORMAT = "Delete format should be: delete [-f] <NOTE_ID>";
    private static final String NOTE_FORMAT = "New note format should be: new -t <TITLE> [-b <BODY>]";
    private static final String NOTE_EMPTY = "Note title cannot be empty!";
    private static final String ID_EMPTY = "Please specify a Note ID to ";
    private static final String ID_INVALID = "Note ID must be exactly 8 hexadecimal characters (0-9, a-f)";
    private static final String INIT_EMPTY = "Please specify a repo name!";
    private static final String INIT_INVALID =
            "Repo name can only contain alphanumeric characters, hyphens and underscores.";

    // Note ID validation constants
    private static final int VALID_NOTE_ID_LENGTH = 8;
    private static final String VALID_NOTE_ID_REGEX = "^[a-f0-9]{" + VALID_NOTE_ID_LENGTH + "}$";
    private static final String INVALID_ID_LENGTH_FORMAT =
            "Note ID must be exactly " + VALID_NOTE_ID_LENGTH + " characters long.";

    /**
     * Parses the user command string and returns the appropriate Command object.
     *
     * @param userCommand The raw command string from the user
     * @return The corresponding Command object
     * @throws ZettelException If the command format is invalid
     */
    public static Command parse(String userCommand) throws ZettelException {
        String[] inputs = userCommand.trim().split(" ");
        String command = inputs[0].toLowerCase();
        return switch (command) {
            case "bye" -> new ExitCommand();
            case "list" -> parseListNoteCommand(userCommand);
            case "new" -> parseNewNoteCommand(userCommand);
            case "delete" -> parseDeleteNoteCommand(inputs);
            case "pin" -> parsePinNoteCommand(inputs, true);
            case "unpin" -> parsePinNoteCommand(inputs, false);
            case "init" -> parseInitCommand(userCommand);
            case "find" -> parseFindNoteCommand(userCommand);
            default -> throw new InvalidInputException(command);
        };
    }

    /**
     * Parses the list command, which can optionally include the -p flag for pinned notes.
     *
     * @param input The full command string
     * @return A ListNoteCommand with the appropriate filter
     * @throws ZettelException If the format is invalid
     */
    private static Command parseListNoteCommand(String input) throws ZettelException {
        String content = input.substring(4).trim();
        if (content.isEmpty()) {
            return new ListNoteCommand(false);
        } else if (!content.equals("-p")) {
            throw new InvalidFormatException(LIST_FORMAT);
        }
        return new ListNoteCommand(true);
    }

    /**
     * Parses the init command for creating a new repository.
     *
     * @param input The full command string
     * @return An InitCommand with the repository name
     * @throws ZettelException If the repo name is empty or invalid
     */
    private static Command parseInitCommand(String input) throws ZettelException {
        String content = input.substring(4).trim();
        if (content.isEmpty()) {
            throw new EmptyDescriptionException(INIT_EMPTY);
        }

        // Validate input - only alphanumeric, hyphens, and underscores
        if (!content.matches("[a-zA-Z0-9_-]+")) {
            throw new InvalidInputException(INIT_INVALID);
        }
        return new InitCommand(content);
    }

    /**
     * Parses the new note command with title and optional body.
     *
     * @param input The full command string
     * @return A NewNoteCommand with the title and body
     * @throws ZettelException If the format is invalid or title is empty
     */
    private static Command parseNewNoteCommand(String input) throws ZettelException {
        try {
            String content = input.substring(3).trim();

            int titleIndex = content.indexOf("-t");
            int bodyIndex = content.indexOf("-b");

            if (titleIndex == -1) {
                throw new InvalidFormatException(NOTE_FORMAT);
            }
            String title;
            String body = "";

            if (bodyIndex != -1) {
                title = content.substring(titleIndex + 2, bodyIndex).trim();
                body = content.substring(bodyIndex + 2).trim();
            } else {
                title = content.substring(titleIndex + 2).trim();
            }

            if (title.isEmpty()) {
                throw new EmptyDescriptionException(NOTE_EMPTY);
            }

            return new NewNoteCommand(title, body);

        } catch (StringIndexOutOfBoundsException e) {
            throw new InvalidFormatException(NOTE_FORMAT);
        }
    }

    /**
     * Parses the find command for searching notes.
     *
     * @param input The full command string
     * @return A FindNoteCommand with the search query
     * @throws ZettelException If the search query is empty
     */
    private static Command parseFindNoteCommand(String input) throws ZettelException {
        String content = input.substring(4).trim();
        if (content.isEmpty()) {
            throw new EmptyDescriptionException("Search cannot be empty!");
        }
        return new FindNoteCommand(content);
    }

    /**
     * Parses the pin/unpin command.
     *
     * @param inputs The command split into tokens
     * @param isPin true for pin command, false for unpin
     * @return A PinNoteCommand with the note ID and pin status
     * @throws ZettelException If the format is invalid or note ID is missing
     */
    private static Command parsePinNoteCommand(String[] inputs, boolean isPin) throws ZettelException {
        if (inputs.length > 2) {
            throw new InvalidFormatException(PIN_FORMAT);
        }
        String noteId = parseNoteID(inputs, "pin/unpin");
        return new PinNoteCommand(noteId, isPin);
    }

    /**
     * Parses the delete command, which can optionally include the -f flag for force delete.
     *
     * @param inputs The command split into tokens
     * @return A DeleteNoteCommand with the note ID and force flag
     * @throws ZettelException If the format is invalid or note ID is missing
     */
    private static Command parseDeleteNoteCommand(String[] inputs) throws ZettelException {
        boolean forceDelete = false;

        if (inputs.length > 3) {
            throw new InvalidFormatException(DELETE_FORMAT);
        } else if (inputs.length == 3) {
            if (!inputs[1].equals("-f")) {
                throw new InvalidInputException(DELETE_FORMAT);
            }
            forceDelete = true;
        }
        String noteId = parseNoteID(inputs, "delete");

        return new DeleteNoteCommand(noteId, forceDelete);
    }

    /**
     * Extracts and validates a note ID from command inputs.
     * Note IDs must be exactly 8 lowercase hexadecimal characters (0-9, a-f).
     *
     * @param inputs The command split into tokens
     * @param actionName The name of the action for error messages
     * @return The validated note ID
     * @throws ZettelException If the note ID is missing or invalid
     */
    private static String parseNoteID(String[] inputs, String actionName) throws ZettelException {
        if (inputs.length < 2) {
            throw new EmptyDescriptionException(ID_EMPTY + actionName + "!");
        }
        String idString = inputs[inputs.length - 1].trim();
        assert !idString.isEmpty() : "ID string should not be empty after trim";

        // Validate noteId format - must be exactly 8 lowercase hex characters
        if (!idString.matches(VALID_NOTE_ID_REGEX)) {
            throw new InvalidFormatException(ID_INVALID);
        }
        if (idString.length() != VALID_NOTE_ID_LENGTH) {
            throw new InvalidFormatException(INVALID_ID_LENGTH_FORMAT);
        }

        return idString;
    }
}
