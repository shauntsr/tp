package seedu.zettel;

import seedu.zettel.commands.Command;
import seedu.zettel.commands.DeleteNoteCommand;
import seedu.zettel.commands.ExitCommand;
import seedu.zettel.commands.FindNoteCommand;
import seedu.zettel.commands.InitCommand;
import seedu.zettel.commands.ListNoteCommand;
import seedu.zettel.commands.NewNoteCommand;
import seedu.zettel.commands.PinNoteCommand;
import seedu.zettel.commands.TagNoteCommand;
import seedu.zettel.exceptions.EmptyDescriptionException;
import seedu.zettel.exceptions.InvalidFormatException;
import seedu.zettel.exceptions.InvalidInputException;
import seedu.zettel.exceptions.ZettelException;

/**
 * Parses user input commands and converts them into executable Command objects.
 * This class handles validation and extraction of command parameters from raw user input strings.
 */
public class Parser {
    // Error messages to be printed when encountering errors
    // Categorised between empty, format and invalid
    private static final String LIST_FORMAT = "List format should be: list [-p]";
    private static final String PIN_FORMAT = "Pin format should be: pin/unpin <NOTE_ID>";
    private static final String DELETE_FORMAT = "Delete format should be: delete [-f] <NOTE_ID>";
    private static final String NOTE_FORMAT = "New note format should be: new -t <TITLE> [-b <BODY>]";
    private static final String TAG_FORMAT = "Tag command requires a subcommand: new/add";
    private static final String TAG_NOTE_FORMAT = "Tag note command format should be: tag <NOTE_ID> <TAG>";
    private static final String NOTE_EMPTY = "Note title cannot be empty!";
    private static final String ID_EMPTY = "Please specify a Note ID to ";
    private static final String ID_INVALID = "Note ID must be exactly 8 hexadecimal characters (0-9, a-f)";
    private static final String INIT_EMPTY = "Please specify a repo name!";
    private static final String INIT_INVALID =
            "Repo name can only contain alphanumeric characters, "
                    + "hyphens and underscores.";
    // Note ID validation constants
    private static final int VALID_NOTE_ID_LENGTH = 8;
    private static final String VALID_NOTE_ID_REGEX = "^[a-f0-9]{" + VALID_NOTE_ID_LENGTH + "}$";
    private static final String INVALID_ID_LENGTH_FORMAT =
            "Note ID must be exactly " + VALID_NOTE_ID_LENGTH + " characters long.";

    /**
     * Parses a user command string and returns the corresponding Command object.
     * Supported commands include: bye, list, new, delete, pin, unpin, init, and find.
     *
     * @param input The raw user input string to parse.
     * @return A Command object corresponding to the user's input.
     * @throws ZettelException If the command format is invalid or parameters are missing.
     */
    public static Command parse(String input) throws ZettelException {
        String[] inputs = input.trim().split(" "); //split input based on spaces in between
        String command = inputs[0].toLowerCase(); //first word of user input
        return switch (command) {
        case "bye" -> new ExitCommand();
        case "list" -> parseListNoteCommand(input);
        case "new" -> parseNewNoteCommand(input);
        case "delete" -> parseDeleteNoteCommand(inputs);
        case "pin" -> parsePinNoteCommand(inputs, true);
        case "unpin" -> parsePinNoteCommand(inputs, false);
        case "init" -> parseInitCommand(input);
        case "find" -> parseFindNoteCommand(input);
        case "tag" -> parseTagCommand(inputs);
        default -> throw new InvalidInputException(command);
        };
    }

    /**
     * Parses a list command to display notes.
     * Accepts an optional -p flag to show only pinned notes.
     *
     * @param input The full user input string starting with "list".
     * @return A ListNoteCommand object with the appropriate filter flag.
     * @throws ZettelException If the command format is invalid.
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
     * Parses an init command to initialize a new repository.
     * The repository name must contain only alphanumeric characters, hyphens, and underscores.
     *
     * @param input The full user input string starting with "init".
     * @return An InitCommand object with the specified repository name.
     * @throws ZettelException If the repository name is empty or contains invalid characters.
     */
    private static Command parseInitCommand(String input) throws ZettelException {
        String content = input.substring(4).trim();
        if (content.isEmpty()) {
            throw new EmptyDescriptionException(INIT_EMPTY);
        }

        // Validate input - only alphanumeric, hyphens, and underscores
        if (!content.matches("[a-zA-Z0-9_-]+")) {
            throw new InvalidFormatException(INIT_INVALID);
        }
        return new InitCommand(content);
    }

    /**
     * Parses a new note command to create a new note with a title and optional body.
     * Expected format: new -t TITLE [-b BODY]
     *
     * @param input The full user input string starting with "new".
     * @return A NewNoteCommand object with the extracted title and body.
     * @throws ZettelException If the format is invalid, title flag is missing, or title is empty.
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
     * Parses a find command to search for notes.
     * Expected format: find SEARCH_TERM
     *
     * @param input The full user input string starting with "find".
     * @return A FindNoteCommand object with the search query.
     * @throws ZettelException If the search query is empty.
     */
    private static Command parseFindNoteCommand(String input) throws ZettelException {
        String content = input.substring(4).trim();
        if (content.isEmpty()) {
            throw new EmptyDescriptionException("Search cannot be empty!");
        }
        return new FindNoteCommand(content);
    }

    /**
     * Parses a pin or unpin command to toggle a note's pinned status.
     * Expected format: pin/unpin NOTE_ID
     *
     * @param inputs The tokenized user input split by spaces.
     * @param isPin True if pinning the note, false if unpinning.
     * @return A PinNoteCommand object with the note ID and pin status.
     * @throws ZettelException If the format is invalid or note ID is missing/malformed.
     */
    private static Command parsePinNoteCommand(String[] inputs, boolean isPin) throws ZettelException {
        if (inputs.length > 2) {
            throw new InvalidFormatException(PIN_FORMAT);
        }
        String noteId = parseNoteId(inputs, "pin/unpin");
        return new PinNoteCommand(noteId, isPin);
    }

    /**
     * Parses a delete command to remove a note.
     * Accepts an optional -f flag for force deletion.
     * Expected format: delete [-f] NOTE_ID
     *
     * @param inputs The tokenized user input split by spaces.
     * @return A DeleteNoteCommand object with the note ID and force flag.
     * @throws ZettelException If the format is invalid or note ID is missing/malformed.
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
        String noteId = parseNoteId(inputs, "delete");


        return new DeleteNoteCommand(noteId, forceDelete);
    }

    /**
     * Extracts and validates a note ID from the input array.
     * The note ID must be exactly 8 alphanumeric characters.
     * Helper function used to validate NoteId during parsing
     *
     * @param inputs The tokenized user input split by spaces.
     * @param actionName The name of the action requesting the ID (for error messages).
     * @return The validated note ID string.
     * @throws ZettelException If the ID is missing, has incorrect length, or contains invalid characters.
     */
    private static String parseNoteId(String[] inputs, String actionName) throws ZettelException {
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

    private  static Command parseTagCommand(String[] inputs) throws ZettelException {
        if (inputs.length < 2) {
            throw new InvalidFormatException(TAG_FORMAT);
        }

        String subCommand = inputs[1].toLowerCase();

        return switch (subCommand) {
        case "add" -> parseTagNoteCommand(inputs);
        default -> throw new InvalidFormatException(TAG_FORMAT);
        };
    }
    /**
     * Parses a tag command to add a tag to a note.
     * Expected format: tag NOTE_ID TAG
     *
     * @param inputs The tokenized user input split by spaces.
     * @return A TagNoteCommand object with the note ID and tag.
     * @throws ZettelException If the format is invalid or parameters are missing.
     */
    private static Command parseTagNoteCommand(String[] inputs) throws ZettelException {
        if (inputs.length != 4) {
            throw new InvalidFormatException(TAG_NOTE_FORMAT);
        }

        String noteId = inputs[2].trim();
        if (!noteId.matches(VALID_NOTE_ID_REGEX)) {
            throw new InvalidFormatException(ID_INVALID);
        }

        String tag = inputs[3].trim();

        if (tag.isEmpty()) {
            throw new EmptyDescriptionException("Tag cannot be empty!");
        }

        return new TagNoteCommand(noteId, tag);
    }
}
