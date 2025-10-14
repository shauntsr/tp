package seedu.zettel;

import seedu.zettel.commands.AddCommand;
import seedu.zettel.commands.Command;
import seedu.zettel.commands.DeleteCommand;
import seedu.zettel.commands.ExitCommand;
import seedu.zettel.commands.FindCommand;
import seedu.zettel.commands.ListCommand;
import seedu.zettel.commands.ListDateCommand;
import seedu.zettel.commands.MarkCommand;
import seedu.zettel.exceptions.CoachException;
import seedu.zettel.exceptions.EmptyDescriptionException;
import seedu.zettel.exceptions.InvalidInputException;
import seedu.zettel.exceptions.InvalidTaskFormatException;
import seedu.zettel.exceptions.InvalidTaskIndexException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    private static Command parseTodoCommand(String input) throws CoachException {
        String content = input.substring(4).trim();
        if (content.isEmpty()) {
            throw new EmptyDescriptionException(TODO_EMPTY);
        }
        return new AddCommand(content, AddCommand.TaskType.TODO);
    }

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

    private static Command parseMarkCommand(String[] inputs, boolean isMark) throws CoachException {
        int index = parseTaskIndex(inputs, "mark/unmark");
        return new MarkCommand(index, isMark);
    }

    private static Command parseDeleteCommand(String[] inputs) throws CoachException {
        int index = parseTaskIndex(inputs, "delete");
        return new DeleteCommand(index);
    }

    private static Command parseFindCommand(String input) throws CoachException {
        String content = input.substring(4).trim();
        if (content.isEmpty()) {
            throw new EmptyDescriptionException("Search cannot be empty!");
        }
        return new FindCommand(content);
    }

    private static Command parseListDateCommand(String input) throws CoachException {
        String content = input.substring(2).trim();
        if (content.isEmpty()) {
            throw new EmptyDescriptionException(ON_EMPTY);
        }

        LocalDate date = parseDate(content);
        return new ListDateCommand(date);
    }

    private static LocalDate parseDate(String input) throws CoachException {
        for (String fmt : DATE_FORMATS) {
            return LocalDate.parse(input, DateTimeFormatter.ofPattern(fmt));
        }
        throw new InvalidTaskFormatException("Invalid date format! Use yyyy-MM-dd, d/M/yyyy, or d-M-yyyy.");
    }

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
