package seedu.duke;

import seedu.duke.commands.AddCommand;
import seedu.duke.commands.Command;
import seedu.duke.commands.DeleteCommand;
import seedu.duke.commands.ExitCommand;
import seedu.duke.commands.FindCommand;
import seedu.duke.commands.ListCommand;
import seedu.duke.commands.ListDateCommand;
import seedu.duke.commands.MarkCommand;
import seedu.duke.exceptions.CoachException;
import seedu.duke.exceptions.EmptyDescriptionException;
import seedu.duke.exceptions.InvalidInputException;
import seedu.duke.exceptions.InvalidTaskFormatException;
import seedu.duke.exceptions.InvalidTaskIndexException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Parses user input commands and converts them into executable Command objects.
 * The Parser class handles the interpretation of various command formats
 * including task creation (todo, deadline, event), task management (mark, add, delete),
 * and task querying (list, find, on).
 */

public class Parser {
    private static final String TODO_EMPTY = "Task description cannot be empty!";
    private static final String DEADLINE_FORMAT = "Deadline format should be: deadline <description> /by <time>";
    private static final String DEADLINE_EMPTY = "Task description and deadline cannot be empty!";
    private static final String EVENT_FORMAT = "Event format should be: event <description> /from <time> /to <time>";
    private static final String EVENT_EMPTY = "Task description, start and end time cannot be empty!";
    private static final String INDEX_EMPTY = "Please specify a task number to ";
    private static final String INDEX_INVALID = "Task number must be a valid integer!";
    private static final String ON_EMPTY = "Please specify a date!";
    private static final String[] DATE_FORMATS = {"yyyy-MM-dd", "d/M/yyyy", "d-M-yyyy"};

    /**
     * Parses the full command string and returns the corresponding Command object.
     * Identifies the command type and uses appropriate parsing method
     *
     * @param userCommand The command string entered by the user.
     * @return The Command object corresponding to the parsed input.
     * @throws CoachException If the command is invalid or misformatted.
     */

    public static Command parse(String userCommand) throws CoachException {
        String[] inputs = userCommand.split(" ");
        String command = inputs[0].toLowerCase();

        return switch (command) {
            case "bye" -> new ExitCommand();
            case "list" -> new ListCommand();
            case "mark" -> parseMarkCommand(inputs, true);
            case "unmark" -> parseMarkCommand(inputs, false);
            case "todo" -> parseTodoCommand(userCommand);
            case "deadline" -> parseDeadlineCommand(userCommand);
            case "event" -> parseEventCommand(userCommand);
            case "delete" -> parseDeleteCommand(inputs);
            case "on" -> parseListDateCommand(userCommand);
            case "find" -> parseFindCommand(userCommand);
            default -> throw new InvalidInputException(command);
        };
    }

    /**
     * Parses a todo command and creates an AddCommand for a todo task.
     *
     * @param input The USER command string starting with "todo".
     * @return An AddCommand for creating a todo task.
     * @throws CoachException If the task description is empty.
     */
    private static Command parseTodoCommand(String input) throws CoachException {
        String content = input.substring(4).trim();
        if (content.isEmpty()) {
            throw new EmptyDescriptionException(TODO_EMPTY);
        }
        return new AddCommand(content, AddCommand.TaskType.TODO);
    }

    /**
     * Parses a deadline command and creates an AddCommand for a deadline task.
     *
     * @param input The user command string starting with "deadline", substring following /by indicates a time.
     * @return An AddCommand for creating a deadline task.
     * @throws CoachException If the format is invalid or required fields are empty.
     */
    private static Command parseDeadlineCommand(String input) throws CoachException {
        try {
            String content = input.substring(9);
            int byIndex = content.indexOf("/by ");

            if (byIndex == -1) {
                throw new InvalidTaskFormatException(DEADLINE_FORMAT);
            }

            String name = content.substring(0, byIndex).trim();
            String deadline = content.substring(byIndex + 4).trim();

            if (name.isEmpty() || deadline.isEmpty()) {
                throw new EmptyDescriptionException(DEADLINE_EMPTY);
            }

            return new AddCommand(name, deadline, AddCommand.TaskType.DEADLINE);

        } catch (StringIndexOutOfBoundsException e) {
            throw new EmptyDescriptionException(DEADLINE_EMPTY);
        }
    }

    /**
     * Parses an event command and creates an AddCommand for an event task.
     *
     * @param input The user command string starting with "event", substring following /to and /from indicates a time.
     * @return An AddCommand for creating an event task.
     * @throws CoachException If the format is invalid or required fields are empty.
     */
    private static Command parseEventCommand(String input) throws CoachException {
        try {
            String content = input.substring(6);

            int fromIndex = content.indexOf("/from");
            int toIndex = content.indexOf("/to");

            if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex) {
                throw new InvalidTaskFormatException(EVENT_FORMAT);
            }

            String taskName = content.substring(0, fromIndex).trim();
            String fromTime = content.substring(fromIndex + 6, toIndex).trim();
            String toTime = content.substring(toIndex + 4).trim();

            if (taskName.isEmpty() || fromTime.isEmpty() || toTime.isEmpty()) {
                throw new EmptyDescriptionException(EVENT_EMPTY);
            }

            return new AddCommand(taskName, fromTime, toTime, AddCommand.TaskType.EVENT);

        } catch (StringIndexOutOfBoundsException e) {
            throw new EmptyDescriptionException(EVENT_FORMAT);
        }
    }

    /**
     * Parses a mark or unmark command and creates a MarkCommand.
     *
     * @param inputs The user command string split by spaces.
     * @param isMark True to mark a task as done, false to unmark it.
     * @return A MarkCommand for marking or unmarking a task.
     * @throws CoachException If the task index is missing or invalid.
     */
    private static Command parseMarkCommand(String[] inputs, boolean isMark) throws CoachException {
        int index = parseTaskIndex(inputs, "mark/unmark");
        return new MarkCommand(index, isMark);
    }

    /**
     * Parses a delete command and creates a DeleteCommand.
     *
     * @param inputs The user command string split by spaces.
     * @return A DeleteCommand for deleting a task.
     * @throws CoachException If the task index is missing or invalid.
     */
    private static Command parseDeleteCommand(String[] inputs) throws CoachException {
        int index = parseTaskIndex(inputs, "delete");
        return new DeleteCommand(index);
    }

    /**
     * Parses a find command and creates a FindCommand.
     *
     * @param input The user command string starting with "find".
     * @return A FindCommand for searching tasks.
     * @throws CoachException If the search query is empty.
     */
    private static Command parseFindCommand(String input) throws CoachException {
        String content = input.substring(4).trim();
        if (content.isEmpty()) {
            throw new EmptyDescriptionException("Search cannot be empty!");
        }
        return new FindCommand(content);
    }

    /**
     * Parses a list date command and creates a ListDateCommand.
     * Lists all tasks occurring on the specified date.
     *
     * @param input The user command string starting with "on".
     * @return A ListDateCommand for listing tasks on a specific date.
     * @throws CoachException If the date is empty or in an invalid format.
     */
    private static Command parseListDateCommand(String input) throws CoachException {
        String content = input.substring(2).trim();
        if (content.isEmpty()) {
            throw new EmptyDescriptionException(ON_EMPTY);
        }

        LocalDate date = parseDate(content);
        return new ListDateCommand(date);
    }

    /**
     * Parses a date string in multiple supported formats.
     * Supported formats: yyyy-MM-dd, d/M/yyyy, d-M-yyyy.
     *
     * @param input The date string to parse.
     * @return The parsed LocalDate object.
     * @throws CoachException If the date format is not recognized.
     */
    private static LocalDate parseDate(String input) throws CoachException {
        for (String fmt : DATE_FORMATS) {
            try {
                return LocalDate.parse(input, DateTimeFormatter.ofPattern(fmt));
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new InvalidTaskFormatException("Invalid date format! Use yyyy-MM-dd, d/M/yyyy, or d-M-yyyy.");
    }

    /**
     * Parses and validates the task index from command inputs.
     *
     * @param inputs     The user command string split by spaces.
     * @param actionName The name of the action being performed (for error messages).
     * @return The validated task index as an integer.
     * @throws CoachException If the index is missing or not a valid integer.
     */
    private static int parseTaskIndex(String[] inputs, String actionName) throws CoachException {
        if (inputs.length < 2) {
            throw new EmptyDescriptionException(INDEX_EMPTY + actionName + "!");
        }

        try {
            return Integer.parseInt(inputs[1]) - 1;
        } catch (NumberFormatException e) {
            throw new InvalidTaskIndexException(INDEX_INVALID);
        }
    }
}
