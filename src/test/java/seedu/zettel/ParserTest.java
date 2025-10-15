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
import seedu.zettel.exceptions.InvalidIndexException;
import seedu.zettel.exceptions.InvalidInputException;
import seedu.zettel.exceptions.ZettelException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParserTest {

    @Test
    void parse_exitCommand_returnsExitCommand() throws ZettelException {
        Command command = Parser.parse("bye");
        assertInstanceOf(ExitCommand.class, command);
    }

    @Test
    void parse_byeWithExtraSpaces_returnsExitCommand() throws ZettelException {
        Command command = Parser.parse("bye   ");
        assertInstanceOf(ExitCommand.class, command);
    }

    // ==================== ListNoteCommand Tests ====================
    @Test
    void parse_listCommand_returnsListNoteCommand() throws ZettelException {
        Command command = Parser.parse("list");
        assertInstanceOf(ListNoteCommand.class, command);
    }

    @Test
    void parse_listWithPinnedFlag_returnsListNoteCommandWithPinned() throws ZettelException {
        Command command = Parser.parse("list -p");
        assertInstanceOf(ListNoteCommand.class, command);
    }

    @Test
    void parse_listWithInvalidFlag_throwsInvalidFormatException() {
        InvalidFormatException exception = assertThrows(
                InvalidFormatException.class,
                () -> Parser.parse("list -x")
        );
        assertTrue(exception.getMessage().contains("List format should be"));
    }

    @Test
    void parse_listWithExtraText_throwsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("list extra"));
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
        InvalidFormatException exception = assertThrows(
                InvalidFormatException.class,
                () -> Parser.parse("new My Title")
        );
        assertTrue(exception.getMessage().contains("New note format should be"));
    }

    @Test
    void parse_newNoteWithEmptyTitle_throwsEmptyDescriptionException() {
        EmptyDescriptionException exception = assertThrows(
                EmptyDescriptionException.class,
                () -> Parser.parse("new -t")
        );
        assertTrue(exception.getMessage().contains("Note title cannot be empty"));
    }

    @Test
    void parse_newNoteWithEmptyTitleAndBody_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parse("new -t  -b Body"));
    }

    @Test
    void parse_newNoteWithOnlyWhitespaceTitle_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parse("new -t    "));
    }

    @Test
    void parse_newNoteInvalidFormat_throwsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("new"));
    }

    // ==================== DeleteNoteCommand Tests ====================
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
        EmptyDescriptionException exception = assertThrows(
                EmptyDescriptionException.class,
                () -> Parser.parse("delete")
        );
        assertTrue(exception.getMessage().contains("Please specify a Note ID"));
    }

    @Test
    void parse_deleteWithInvalidIdLength_throwsInvalidIndexException() {
        InvalidIndexException exception = assertThrows(
                InvalidIndexException.class,
                () -> Parser.parse("delete 12345")
        );
        assertTrue(exception.getMessage().contains("Note ID must be exactly 6 Digits"));
    }

    @Test
    void parse_deleteWithNonNumericId_throwsInvalidIndexException() {
        assertThrows(InvalidIndexException.class, () -> Parser.parse("delete abc123"));
    }

    @Test
    void parse_deleteWithTooManyArguments_throwsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete -f 123456 extra"));
    }

    @Test
    void parse_deleteWithInvalidFlag_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> Parser.parse("delete -x 123456"));
    }

    // ==================== PinNoteCommand Tests ====================
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
    void parse_pinWithInvalidIdLength_throwsInvalidIndexException() {
        assertThrows(InvalidIndexException.class, () -> Parser.parse("pin 1234567"));
    }

    @Test
    void parse_pinWithNonNumericId_throwsInvalidIndexException() {
        assertThrows(InvalidIndexException.class, () -> Parser.parse("pin abcdef"));
    }

    @Test
    void parse_pinWithTooManyArguments_throwsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("pin 123456 extra"));
    }

    @Test
    void parse_unpinWithoutId_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parse("unpin"));
    }

    // ==================== InitCommand Tests ====================
    @Test
    void parse_initWithRepoName_returnsInitCommand() throws ZettelException {
        Command command = Parser.parse("init myRepo");
        assertInstanceOf(InitCommand.class, command);
    }

    @Test
    void parse_initWithMultipleWords_returnsInitCommand() throws ZettelException {
        Command command = Parser.parse("init my repo name");
        assertInstanceOf(InitCommand.class, command);
    }

    @Test
    void parse_initWithoutRepoName_throwsEmptyDescriptionException() {
        EmptyDescriptionException exception = assertThrows(
                EmptyDescriptionException.class,
                () -> Parser.parse("init")
        );
        assertTrue(exception.getMessage().contains("Please specify a repo name"));
    }

    @Test
    void parse_initWithOnlyWhitespace_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parse("init   "));
    }

    @Test
    void parse_findWithSearchTerm_returnsFindNoteCommand() throws ZettelException {
        Command command = Parser.parse("find test");
        assertInstanceOf(FindNoteCommand.class, command);
    }

    @Test
    void parse_findWithMultipleWords_returnsFindNoteCommand() throws ZettelException {
        Command command = Parser.parse("find test search query");
        assertInstanceOf(FindNoteCommand.class, command);
    }

    @Test
    void parse_findWithoutSearchTerm_throwsEmptyDescriptionException() {
        EmptyDescriptionException exception = assertThrows(
                EmptyDescriptionException.class,
                () -> Parser.parse("find")
        );
        assertTrue(exception.getMessage().contains("Search cannot be empty"));
    }

    @Test
    void parse_findWithOnlyWhitespace_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parse("find   "));
    }

    @Test
    void parse_unknownCommand_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> Parser.parse("unknown"));
    }

    @Test
    void parse_emptyString_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> Parser.parse(""));
    }

    @Test
    void parse_randomText_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> Parser.parse("random text"));
    }

    @Test
    void parse_upperCaseCommand_returnsCorrectCommand() throws ZettelException {
        Command command = Parser.parse("BYE");
        assertInstanceOf(ExitCommand.class, command);
    }

    @Test
    void parse_mixedCaseCommand_returnsCorrectCommand() throws ZettelException {
        Command command = Parser.parse("LiSt");
        assertInstanceOf(ListNoteCommand.class, command);
    }

    @Test
    void parse_noteIdWithLeadingZeros_success() throws ZettelException {
        Command command = Parser.parse("delete 000001");
        assertInstanceOf(DeleteNoteCommand.class, command);
    }

    @Test
    void parse_noteIdAllZeros_success() throws ZettelException {
        Command command = Parser.parse("pin 000000");
        assertInstanceOf(PinNoteCommand.class, command);
    }

    @Test
    void parse_noteIdAllNines_success() throws ZettelException {
        Command command = Parser.parse("unpin 999999");
        assertInstanceOf(PinNoteCommand.class, command);
    }

    @Test
    void parse_newNoteWithSpecialCharactersInTitle_success() throws ZettelException {
        Command command = Parser.parse("new -t Title@#$% -b Body");
        assertInstanceOf(NewNoteCommand.class, command);
    }

    @Test
    void parse_newNoteWithMultipleDashes_success() throws ZettelException {
        Command command = Parser.parse("new -t My-Title-With-Dashes");
        assertInstanceOf(NewNoteCommand.class, command);
    }
}
