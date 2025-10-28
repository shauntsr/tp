package seedu.zettel;

import java.util.Arrays;

import seedu.zettel.commands.Command;
import seedu.zettel.commands.DeleteNoteCommand;
import seedu.zettel.commands.ExitCommand;
import seedu.zettel.commands.FindNoteCommand;
import seedu.zettel.commands.InitCommand;
import seedu.zettel.commands.LinkNotesCommand;
import seedu.zettel.commands.ListNoteCommand;
import seedu.zettel.commands.NewNoteCommand;
import seedu.zettel.commands.NewTagCommand;
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
    private static final String INIT_FORMAT = "Init format should be: init <REPO_NAME>";
    private static final String DELETE_FORMAT = "Delete format should be: delete [-f] <NOTE_ID>";
    private static final String FIND_FORMAT = "Find format should be: find <SEARCH_TERM>";
    private static final String NOTE_FORMAT = "New note format should be: new -t <TITLE> [-b <BODY>]";
    private static final String TAG_FORMAT = "Tag command requires a subcommand: new/add";
    private static final String TAG_NOTE_FORMAT = "Tag note command format should be: tag <NOTE_ID> <TAG>";
    private static final String NEW_TAG_FORMAT = "Tag add command format should be: tag add <TAG>";
    private static final String LINK_NOTES_FORMAT = "Link notes command format should be: link" 
            + " <SOURCE_NOTE_ID> <TARGET_NOTE_ID>";
    private static final String NOTE_EMPTY = "Note title cannot be empty!";
    private static final String TAG_EMPTY = "Tag cannot be empty!";
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
        String[] inputs = input.trim().split("\\s+"); //split input based on spaces in between
        String command = inputs[0].toLowerCase(); //first word of user input
        return switch (command) {
        case "bye" -> new ExitCommand();
        case "list" -> parseListNoteCommand(inputs);
        case "new" -> parseNewNoteCommand(inputs);
        case "delete" -> parseDeleteNoteCommand(inputs);
        case "pin" -> parsePinNoteCommand(inputs, true);
        case "unpin" -> parsePinNoteCommand(inputs, false);
        case "init" -> parseInitCommand(inputs);
        case "find" -> parseFindNoteCommand(inputs);
        case "tag" -> parseTagCommand(inputs);
        case "link" -> parseLinkNotesCommand(inputs);
        default -> throw new InvalidInputException(command);
        };
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
    private static String parseNoteId(String noteId, String actionName) throws ZettelException {
        if (noteId == null || noteId.trim().isEmpty()) {
            throw new EmptyDescriptionException(ID_EMPTY + actionName + "!");
        }
        String idString = noteId.trim();
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

    /**
     * Parses a list command to display notes.
     * Accepts an optional -p flag to show only pinned notes.
     *
     * @param input The full user input string starting with "list".
     * @return A ListNoteCommand object with the appropriate filter flag.
     * @throws ZettelException If the command format is invalid.
     */
    private static Command parseListNoteCommand(String[] inputs) throws ZettelException {
        if (inputs.length != 1 && inputs.length != 2) {
            throw new InvalidFormatException(LIST_FORMAT);
        }
        // command is for sure either "list" or "list <something>"
        if (inputs.length == 1) {
            return new ListNoteCommand(false);
        } else if (!inputs[1].equals("-p")) {
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
    private static Command parseInitCommand(String[] inputs) throws ZettelException {
        if (inputs.length < 2) {
            throw new EmptyDescriptionException(INIT_EMPTY);
        }
        
        if (inputs.length != 2) {
            throw new InvalidFormatException(INIT_FORMAT);
        }

        String content = inputs[1].trim();
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
    private static Command parseNewNoteCommand(String[] inputs) throws ZettelException {
        try {
            String content = String.join(" ", Arrays.copyOfRange(inputs, 1, inputs.length)).trim();

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
    private static Command parseFindNoteCommand(String[] inputs) throws ZettelException {
        if (inputs.length < 2) {
            throw new EmptyDescriptionException(FIND_FORMAT);
        }

        String content = inputs[1].trim();
        if (content.isEmpty()) {
            throw new EmptyDescriptionException(FIND_FORMAT);
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
        if (inputs.length < 2) {
            throw new EmptyDescriptionException(ID_EMPTY + (isPin ? "pin" : "unpin") + "!");
        }
        if (inputs.length > 2) {
            throw new InvalidFormatException(PIN_FORMAT);
        }
        String noteId = parseNoteId(inputs[1], isPin ? "pin" : "unpin");
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

        if (inputs.length < 2) {
            throw new EmptyDescriptionException(ID_EMPTY + "delete!");
        }
        if (inputs.length > 3) {
            throw new InvalidFormatException(DELETE_FORMAT);
        } else if (inputs.length == 3) {
            if (!inputs[1].equals("-f")) {
                throw new InvalidInputException(DELETE_FORMAT);
            }
            forceDelete = true;
        }
        String noteId = forceDelete ? parseNoteId(inputs[2], "delete") : 
                parseNoteId(inputs[1], "delete");


        return new DeleteNoteCommand(noteId, forceDelete);
    }

    private static Command parseTagCommand(String[] inputs) throws ZettelException {
        if (inputs.length < 2) {
            throw new InvalidFormatException(TAG_FORMAT);
        }

        String subCommand = inputs[1].toLowerCase();

        return switch (subCommand) {
        case "add" -> parseTagNoteCommand(inputs);
        case "new" -> parseNewTagCommand(inputs); 
        default -> throw new InvalidFormatException(TAG_FORMAT);
        };
    }

    /**
     * Parses a tag command to add a tag to a note.
     *
     * @param inputs The tokenized user input split by spaces.
     * @return A TagNoteCommand object with the note ID and tag.
     * @throws ZettelException If the format is invalid or parameters are missing.
     */
    private static Command parseTagNoteCommand(String[] inputs) throws ZettelException {
        if (inputs.length != 4) {
            throw new InvalidFormatException(TAG_NOTE_FORMAT);
        }

        String noteId = parseNoteId(inputs[2], "tag");
        String tag = inputs[3].trim();

        if (tag.isEmpty()) {
            throw new EmptyDescriptionException(TAG_EMPTY);
        }

        return new TagNoteCommand(noteId, tag);
    }

    /**
     * Parses a new tag command to add a new tag to the config file.
     *
     * @param inputs The tokenized user input split by spaces.
     * @return An AddTagCommand object with the tag to add.
     * @throws ZettelException If the format is invalid or tag is missing.
     */
    private static Command parseNewTagCommand(String[] inputs) throws ZettelException {
        if (inputs.length != 3) {
            throw new InvalidFormatException(NEW_TAG_FORMAT);
        }

        String tag = inputs[2];
        if (tag.isEmpty()) {
            throw new EmptyDescriptionException(TAG_EMPTY);
        }

        return new NewTagCommand(tag);
    }

    /**
     * Parses a link notes command to create a unidirectional link between two notes.
     * @param inputs The tokenized user input split by spaces.
     * @return A LinkNotesCommand object with the note IDs to link.
     * @throws ZettelException If the format is invalid or parameters are missing.
     */

    private static Command parseLinkNotesCommand(String[] inputs) throws ZettelException {
        if (inputs.length != 3) {
            throw new InvalidFormatException(LINK_NOTES_FORMAT);
        }

        String sourceNoteId = parseNoteId(inputs[1], "link");
        String targetNoteId = parseNoteId(inputs[2], "link");

        return new LinkNotesCommand(sourceNoteId, targetNoteId);
    }
}
