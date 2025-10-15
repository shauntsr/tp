package seedu.zettel;

import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParserTest {

    @Test
    void parse_exitCommand_returnsExitCommand() throws ZettelException {
        Command command = Parser.parse("bye");
        assertInstanceOf(ExitCommand.class, command);
    }

    @Test
    void parse_listCommand_returnsListNoteCommand() throws ZettelException {
        Command command = Parser.parse("list");
        assertInstanceOf(ListNoteCommand.class, command);
    }

    @Test
    void parse_listWithPinnedFlag_returnsListNoteCommand() throws ZettelException {
        Command command = Parser.parse("list -p");
        assertInstanceOf(ListNoteCommand.class, command);
    }

    @Test
    void parse_listWithInvalidFlag_throwsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("list -x"));
    }

    @Test
    void parse_newNoteWithTitleOnly_returnsNewNoteCommand() throws ZettelException {
        Command command = Parser.parse("new -t My Title");
        assertInstanceOf(NewNoteCommand.class, command);
    }

    @Test
    void parse_newNoteWithTitleAndBody_returnsNewNoteCommand() throws ZettelException {
        Command command = Parser.parse("new -t My Title -b My Body");
        assertInstanceOf(NewNoteCommand.class, command);
    }

    @Test
    void parse_newNoteWithoutTitleFlag_throwsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("new My Title"));
    }

    @Test
    void parse_newNoteWithEmptyTitle_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parse("new -t"));
    }

    @Test
    void parse_deleteWithValidId_returnsDeleteNoteCommand() throws ZettelException {
        Command command = Parser.parse("delete 123456");
        assertInstanceOf(DeleteNoteCommand.class, command);
    }

    @Test
    void parse_deleteWithForceFlag_returnsDeleteNoteCommand() throws ZettelException {
        Command command = Parser.parse("delete -f 123456");
        assertInstanceOf(DeleteNoteCommand.class, command);
    }

    @Test
    void parse_deleteWithoutId_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parse("delete"));
    }

    @Test
    void parse_pinWithValidId_returnsPinNoteCommand() throws ZettelException {
        Command command = Parser.parse("pin 123456");
        assertInstanceOf(PinNoteCommand.class, command);
    }

    @Test
    void parse_unpinWithValidId_returnsPinNoteCommand() throws ZettelException {
        Command command = Parser.parse("unpin 654321");
        assertInstanceOf(PinNoteCommand.class, command);
    }

    @Test
    void parse_pinWithoutId_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parse("pin"));
    }

    @Test
    void parse_initWithRepoName_returnsInitCommand() throws ZettelException {
        Command command = Parser.parse("init myRepo");
        assertInstanceOf(InitCommand.class, command);
    }

    @Test
    void parse_initWithoutRepoName_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parse("init"));
    }

    @Test
    void parse_findWithSearchTerm_returnsFindNoteCommand() throws ZettelException {
        Command command = Parser.parse("find test");
        assertInstanceOf(FindNoteCommand.class, command);
    }

    @Test
    void parse_findWithoutSearchTerm_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parse("find"));
    }

    @Test
    void parse_unknownCommand_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> Parser.parse("unknown"));
    }
}