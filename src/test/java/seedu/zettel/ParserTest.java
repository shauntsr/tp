package seedu.zettel;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

/**
 * Unit tests for the Parser class.
 * Tests parsing of all commands and validation of hexadecimal note IDs.
 */
class ParserTest {

    @Test
    void testParseExitCommandReturnsExitCommand() throws ZettelException {
        Command command = Parser.parse("bye");
        assertInstanceOf(ExitCommand.class, command);
    }

    @Test
    void testParseListCommandReturnsListNoteCommand() throws ZettelException {
        Command command = Parser.parse("list");
        assertInstanceOf(ListNoteCommand.class, command);
    }

    @Test
    void testParseListWithPinnedFlagReturnsListNoteCommand() throws ZettelException {
        Command command = Parser.parse("list -p");
        assertInstanceOf(ListNoteCommand.class, command);
    }

    @Test
    void testParseListWithInvalidFlagThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("list -x"));
    }

    @Test
    void testParseNewNoteWithTitleOnlyReturnsNewNoteCommand() throws ZettelException {
        Command command = Parser.parse("new -t My Title");
        assertInstanceOf(NewNoteCommand.class, command);
    }

    @Test
    void testParseNewNoteWithTitleAndBodyReturnsNewNoteCommand() throws ZettelException {
        Command command = Parser.parse("new -t My Title -b My Body");
        assertInstanceOf(NewNoteCommand.class, command);
    }

    @Test
    void testParseNewNoteWithoutTitleFlagThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("new My Title"));
    }

    @Test
    void testParseNewNoteWithEmptyTitleThrowsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parse("new -t"));
    }

    @Test
    void testParseDeleteWithValidHexIdReturnsDeleteNoteCommand() throws ZettelException {
        Command command = Parser.parse("delete abcd1234");
        assertInstanceOf(DeleteNoteCommand.class, command);
    }

    @Test
    void testParseDeleteWithUppercaseHexThrowsInvalidFormatException() {
        // IDs must be lowercase hex only
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete ABCD1234"));
    }

    @Test
    void testParseDeleteWithSpecialCharactersThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete abcd-234"));
    }

    @Test
    void testParseDeleteWithInvalidCharactersThrowsInvalidFormatException() {
        // 'g' is not a valid hex character
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete abcdefgh"));
    }

    @Test
    void testParseDeleteWithForceFlagReturnsDeleteNoteCommand() throws ZettelException {
        Command command = Parser.parse("delete -f abcd1234");
        assertInstanceOf(DeleteNoteCommand.class, command);
    }

    @Test
    void testParseDeleteWithoutIdThrowsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parse("delete"));
    }

    @Test
    void testParseDeleteWithTooShortIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete abc"));
    }

    @Test
    void testParseDeleteWithTooLongIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete abcdef123"));
    }

    @Test
    void testParsePinWithValidHexIdReturnsPinNoteCommand() throws ZettelException {
        Command command = Parser.parse("pin abcd1234");
        assertInstanceOf(PinNoteCommand.class, command);
    }

    @Test
    void testParseUnpinWithValidHexIdReturnsPinNoteCommand() throws ZettelException {
        Command command = Parser.parse("unpin def56789");
        assertInstanceOf(PinNoteCommand.class, command);
    }

    @Test
    void testParsePinWithoutIdThrowsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parse("pin"));
    }

    @Test
    void testParsePinWithSpecialCharactersThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("pin abcd-234"));
    }

    @Test
    void testParsePinWithTooShortIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("pin abc"));
    }

    @Test
    void testParsePinWithTooLongIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("pin abcdef123"));
    }

    @Test
    void testParsePinWithUppercaseHexThrowsInvalidFormatException() {
        // IDs must be lowercase hex only
        assertThrows(InvalidFormatException.class, () -> Parser.parse("pin ABCD1234"));
    }

    @Test
    void testParseInitWithRepoNameReturnsInitCommand() throws ZettelException {
        Command command = Parser.parse("init myRepo");
        assertInstanceOf(InitCommand.class,command);
    }

    @Test
    void testParseInitWithoutRepoNameThrowsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parse("init"));
    }

    @Test
    void testParseInitWithInvalidFormatThrowsInvalidInputException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("init my repo!"));
    }

    @Test
    void testParseFindWithSearchTermReturnsFindNoteCommand() throws ZettelException {
        Command command = Parser.parse("find test");
        assertInstanceOf(FindNoteCommand.class, command);
    }

    @Test
    void testParseFindWithoutSearchTermThrowsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parse("find"));
    }

    @Test
    void testParseUnknownCommandThrowsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> Parser.parse("unknown"));
    }
}
