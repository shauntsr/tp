package seedu.zettel;

import seedu.zettel.commands.ArchiveNoteCommand;
import seedu.zettel.commands.EditNoteCommand;
import seedu.zettel.commands.NewTagCommand;
import java.util.Arrays;

import seedu.zettel.commands.Command;
import seedu.zettel.commands.DeleteNoteCommand;
import seedu.zettel.commands.DeleteTagFromNoteCommand;
import seedu.zettel.commands.EditNoteCommand;
import seedu.zettel.commands.ExitCommand;
import seedu.zettel.commands.FindNoteCommand;
import seedu.zettel.commands.InitCommand;
import seedu.zettel.commands.LinkBothNotesCommand;
import seedu.zettel.commands.LinkNotesCommand;
import seedu.zettel.commands.ListLinkedNotesCommand;
import seedu.zettel.commands.ListNoteCommand;
import seedu.zettel.commands.ListTagsGlobalCommand;
import seedu.zettel.commands.ListTagsSingleNoteCommand;
import seedu.zettel.commands.NewNoteCommand;
import seedu.zettel.commands.NewTagCommand;
import seedu.zettel.commands.PinNoteCommand;
import seedu.zettel.commands.TagNoteCommand;
import seedu.zettel.commands.UnlinkBothNotesCommand;
import seedu.zettel.commands.UnlinkNotesCommand;
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
    private static final String TAG_NOTE_FORMAT = "Tag note command format should be: tag new <NOTE_ID> <TAG>";
    private static final String NEW_TAG_FORMAT = "Tag add command format should be: tag add <TAG>";
    private static final String LIST_TAGS_GLOBAL_FORMAT = "List all tags globally command format should be: "
            + "list-tags-all";
    private static final String LINK_NOTES_FORMAT = "Link notes command format should be: link"
            + " <SOURCE_NOTE_ID> <TARGET_NOTE_ID>";
    private static final String UNLINK_NOTES_FORMAT = "Unlink notes command format should be: unlink" 
            + " <SOURCE_NOTE_ID> <TARGET_NOTE_ID>";
    private static final String LINK_BOTH_NOTES_FORMAT = "Linking both notes command format should be: link-both" 
            + " <NOTE_ID_1> <NOTE_ID_2>";
    private static final String UNLINK_BOTH_NOTES_FORMAT = "Unlinking both notes command format should be: unlink-both" 
            + " <NOTE_ID_1> <NOTE_ID_2>";
    private static final String LIST_TAGS_SINGLE_NOTE_FORMAT = "List tags for single note command format "
            + "should be: list-tags <NOTE_ID>";
    private static final String DELETE_TAG_FORMAT = "Delete tag command format should be: delete-tag <NOTE_ID> <TAG>";
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
    private static final String LIST_LINKED_TYPE_FORMAT = "Link type must be either" + 
                    " 'incoming-links' or 'outgoing-links'.";
    private static final String LIST_LINKED_FORMAT = "List linked notes format should be: list "
                    + "<incoming-links/outgoing-links> <NOTE_ID>";
    private static final String ARCHIVE_NOTES_FORMAT = "Invalid format. Usage: " +
                    "archive <NOTE_ID>";
    private static final String UNARCHIVE_NOTES_FORMAT = "Invalid format. Usage: " +
                    "unarchive <NOTE_ID>";

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
        case "edit" -> parseEditNoteCommand(inputs);
        case "delete" -> parseDeleteNoteCommand(inputs);
        case "pin" -> parsePinNoteCommand(inputs, true);
        case "unpin" -> parsePinNoteCommand(inputs, false);
        case "init" -> parseInitCommand(inputs);
        case "find" -> parseFindNoteCommand(inputs);
        case "tag" -> parseTagCommand(inputs);
        case "link" -> parseLinkNotesCommand(inputs);
        case "unlink" -> parseUnlinkNotesCommand(inputs);
        case "link-both" -> parseLinkBothNotesCommand(inputs);
        case "unlink-both" -> parseUnlinkBothNotesCommand(inputs);
        case "archive" -> parseArchiveNoteCommand(inputs, true);
        case "unarchive" -> parseArchiveNoteCommand(inputs, false);
        case "list-tags-all" -> parseListTagsGlobalCommand(inputs);
        case "list-tags" -> parseListTagsSingleNoteCommand(inputs);
        case "delete-tag" -> parseDeleteTagFromNoteCommand(inputs);
        default -> throw new InvalidInputException(command);
        };
    }

    /**
     * Extracts and validates a note ID from the input array.
     * The note ID must be exactly 8 alphanumeric characters.
     * Helper function used to validate NoteId during parsing
     *
     * @param noteId ID of note
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
     * Accepts optional flags:
     *   -p  show only pinned notes
     *   -a  show only archived notes
     * Flags can be combined in any order (e.g. "list -a -p" or "list -p -a").
     * Routes to parseListLinkedNotesCommand only when the second token is NOT a flag.
     *
     * @param inputs The tokenized user input split by spaces
     * @return A ListNoteCommand or ListLinkedNotesCommand object with the appropriate parameters.
     * @throws ZettelException If the command format is invalid.
     */
    private static Command parseListNoteCommand(String[] inputs) throws ZettelException {
        if (inputs.length >= 3 && !inputs[1].startsWith("-")) {
            return parseListLinkedNotesCommand(inputs);
        }

        if (inputs.length < 1 || inputs.length > 3) {
            throw new InvalidFormatException(LIST_FORMAT);
        }

        boolean showPinned = false;
        boolean showArchived = false;

        // parse flags if present (flags must start with '-' and be known)
        for (int i = 1; i < inputs.length; i++) {
            String tok = inputs[i].trim();
            if (!tok.startsWith("-")) {
                throw new InvalidFormatException(LIST_FORMAT);
            }
            switch (tok) {
            case "-p":
                if (showPinned) {
                    throw new InvalidFormatException(LIST_FORMAT);
                }
                showPinned = true;
                break;
            case "-a":
                if (showArchived) {
                    throw new InvalidFormatException(LIST_FORMAT);
                }
                showArchived = true;
                break;
            default:
                throw new InvalidFormatException(LIST_FORMAT);
            }
        }

        return new ListNoteCommand(showPinned, showArchived);
    }

    /**
     * Parses an init command to initialize a new repository.
     * The repository name must contain only alphanumeric characters, hyphens, and underscores.
     *
     * @param inputs The tokenized user input split by spaces
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
     * @param inputs The tokenized user input split by spaces
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
            String body = null; // newNoteCommand sees null as not provided, "" as user wants an empty body.

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
     * Parses an edit note command to edit an existing note's body.
     * Expected format: edit NOTE_ID
     *
     * @param inputs The tokenized user input split by spaces
     * @return An EditNoteCommand object with the extracted note ID
     * @throws ZettelException If the format is invalid or note ID is missing
     */
    private static Command parseEditNoteCommand(String[] inputs) throws ZettelException {
        if (inputs.length != 2) {
            throw new InvalidFormatException("Invalid format. Usage: edit <NOTE_ID>");
        }
        String noteId = parseNoteId(inputs[1], "edit");
        return new EditNoteCommand(noteId);
    }

    /**
     * Parses a find command to search for notes.
     * Expected format: find SEARCH_TERM
     *
     * @param inputs The tokenized user input split by spaces
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

    /**
     * Parses a tag command and delegates to the appropriate subcommand parser.
     * Supported formats:
     * <ul>
     *     <li>tag new TAG_NAME</li>
     *     <li>tag add NOTE_ID TAG_NAME</li>
     * </ul>
     *
     * @param inputs The tokenized user input split by spaces.
     * @return A TagNoteCommand or an AddTagCommand
     * @throws ZettelException If the format is invalid or parameters are missing.
     */
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
     * Expected Format: tag add NOTE_ID TAG_NAME
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
     * Expected Format: tag new TAG_NAME
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
     * Parses a command to list all tags globally across all notes.
     * Expected format: list-tags-all
     *
     * @param inputs The tokenized user input split by spaces.
     * @return A ListTagsGlobalCommand object.
     * @throws ZettelException If the format is invalid.
     */
    private static Command parseListTagsGlobalCommand(String[] inputs) throws ZettelException {
        if (inputs.length != 1) {
            throw new InvalidFormatException(LIST_TAGS_GLOBAL_FORMAT);
        }
        return new ListTagsGlobalCommand();
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

    /**
     * Parses a list linked notes command to display incoming or outgoing links for a specific note.
     * Expected format: list incoming-links NOTE_ID or list outgoing-links NOTE_ID
     *
     * @param inputs The tokenized user input split by spaces.
     * @return A ListLinkedNotesCommand object with the link type and note ID.
     * @throws ZettelException If the format is invalid, link type is invalid, or note ID is malformed.
     */
    private static Command parseListLinkedNotesCommand(String[] inputs) throws ZettelException {
        // Expected format: list <incoming-links/outgoing-links> <NOTE_ID>
        if (inputs.length != 3) {
            throw new InvalidFormatException(LIST_LINKED_FORMAT);
        }

        String linkType = inputs[1].toLowerCase();
        String noteId = parseNoteId(inputs[2], "list linked notes");
        
        // Convert "incoming-links" to "incoming" and "outgoing-links" to "outgoing"
        String listToShow;
        switch (linkType) {
        case "incoming-links" -> listToShow = "incoming";
        case "outgoing-links" -> listToShow = "outgoing";
        default -> throw new InvalidFormatException(LIST_LINKED_TYPE_FORMAT);
        }

        return new ListLinkedNotesCommand(listToShow, noteId);
    }

    /**
     * Parses an unlink notes command to remove a unidirectional link between two notes.
     * Expected format: unlink SOURCE_NOTE_ID TARGET_NOTE_ID
     *
     * @param inputs The tokenized user input split by spaces.
     * @return An UnlinkNotesCommand object with the source and target note IDs.
     * @throws ZettelException If the format is invalid or note IDs are malformed.
     */
    private static Command parseUnlinkNotesCommand(String[] inputs) throws ZettelException {
        // Expected format: unlink <SOURCE_NOTE_ID> <TARGET_NOTE_ID>
        if (inputs.length != 3) {
            throw new InvalidFormatException(UNLINK_NOTES_FORMAT);
        }

        String sourceNoteId = parseNoteId(inputs[1], "unlink to");
        String targetNoteId = parseNoteId(inputs[2], "unlink to");

        return new UnlinkNotesCommand(sourceNoteId, targetNoteId);
    }

    /**
     * Parses a link-both notes command to create a bidirectional link between two notes.
     * @param inputs The tokenized user input split by spaces.
     * @return A LinkBothNotesCommand object with the note IDs to link.
     * @throws ZettelException If the format is invalid or parameters are missing.
     */
    private static Command parseLinkBothNotesCommand(String[] inputs) throws ZettelException {
        // Expected format: link-both <note_ID_1> <note_ID_2>
        if (inputs.length != 3) {
            throw new InvalidFormatException(LINK_BOTH_NOTES_FORMAT);
        }

        String sourceNoteId = parseNoteId(inputs[1], "link in both directions");
        String targetNoteId = parseNoteId(inputs[2], "link in both directions");

        return new LinkBothNotesCommand(sourceNoteId, targetNoteId);
    }

    /**
     * Parses an unlink-both notes command to remove a bidirectional link between two notes.
     * @param inputs The tokenized user input split by spaces.
     * @return An UnlinkBothNotesCommand object with the note IDs to unlink.
     * @throws ZettelException If the format is invalid or parameters are missing.
     */    
    private static Command parseUnlinkBothNotesCommand(String[] inputs) throws ZettelException {
        // Expected format: unlink-both <note_ID_1> <note_ID_2>
        if (inputs.length != 3) {
            throw new InvalidFormatException(UNLINK_BOTH_NOTES_FORMAT);
        }

        String noteId1 = parseNoteId(inputs[1], "unlink in both directions");
        String noteId2 = parseNoteId(inputs[2], "unlink in both directions");

        return new UnlinkBothNotesCommand(noteId1, noteId2);
    }

    /**
     * Parses an archive or unarchive command.
     * Expected format: archive/unarchive NOTE_ID
     *
     * @param inputs        The tokenized user input split by spaces
     * @param shouldArchive True for archive, false for unarchive
     * @return              An ArchiveNoteCommand object with the note ID and action
     * @throws ZettelException If the format is invalid or note ID is missing/malformed
     */
    private static Command parseArchiveNoteCommand(String[] inputs, boolean shouldArchive)
            throws ZettelException {
        if (inputs.length != 2) {
            throw (shouldArchive) ? new InvalidFormatException(ARCHIVE_NOTES_FORMAT)
                    : new InvalidFormatException(UNARCHIVE_NOTES_FORMAT);
        }
        String noteId = parseNoteId(inputs[1], shouldArchive ? "archive" : "unarchive");
        return new ArchiveNoteCommand(noteId, shouldArchive);
    }

    /**
     * Parses a list-tags command to display all tags associated with a specific note.
     * @param inputs The tokenized user input split by spaces.
     * @return A ListTagsSingleNoteCommand object with the note ID.
     * @throws ZettelException If the format is invalid or note ID is malformed.
     */
    private static Command parseListTagsSingleNoteCommand(String[] inputs) throws ZettelException {
        // expected format: list-tags <NOTE_ID>
        if (inputs.length != 2) {
            throw new InvalidFormatException(LIST_TAGS_SINGLE_NOTE_FORMAT);
        }

        String noteId = parseNoteId(inputs[1], "list tags for");
 
        return new ListTagsSingleNoteCommand(noteId);
    }

    /**
     * Parses a delete-tag command to remove a tag from a specific note.
     * @param inputs The tokenized user input split by spaces.
     * @return A DeleteTagFromNoteCommand object with the note ID and tag.
     * @throws ZettelException If the format is invalid or parameters are missing.
     */
    private static Command parseDeleteTagFromNoteCommand(String[] inputs) throws ZettelException {
        // Expected format: delete-tag NOTE_ID TAG_NAME
        if (inputs.length != 3) {
            throw new InvalidFormatException(DELETE_TAG_FORMAT);
        }

        String noteId = parseNoteId(inputs[1], "delete tag from");
        String tag = inputs[2].trim();

        if (tag.isEmpty()) {
            throw new EmptyDescriptionException("Tag cannot be empty!");
        }

        return new DeleteTagFromNoteCommand(noteId, tag);
    }
}
