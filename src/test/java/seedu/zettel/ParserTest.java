package seedu.zettel;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import seedu.zettel.commands.Command;
import seedu.zettel.commands.DeleteNoteCommand;
import seedu.zettel.commands.ExitCommand;
import seedu.zettel.commands.FindNoteCommand;
import seedu.zettel.commands.InitCommand;
import seedu.zettel.commands.LinkBothNotesCommand;
import seedu.zettel.commands.LinkNotesCommand;
import seedu.zettel.commands.ListNoteCommand;
import seedu.zettel.commands.NewNoteCommand;
import seedu.zettel.commands.PinNoteCommand;
import seedu.zettel.commands.TagNoteCommand;
import seedu.zettel.commands.UnlinkBothNotesCommand;
import seedu.zettel.commands.UnlinkNotesCommand;
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

    // ==================== Link Command Tests ====================

    @Test
    void testParseLinkWithValidIdsReturnsLinkNotesCommand() throws ZettelException {
        Command command = Parser.parse("link abcd1234 ef567890");
        assertInstanceOf(LinkNotesCommand.class, command);
    }

    @Test
    void testParseLinkWithoutArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link"));
    }

    @Test
    void testParseLinkWithOnlyOneIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link abcd1234"));
    }

    @Test
    void testParseLinkWithTooManyArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link abcd1234 ef567890 12345678"));
    }

    @Test
    void testParseLinkWithTooShortFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link abc ef567890"));
    }

    @Test
    void testParseLinkWithTooShortSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link abcd1234 ef5"));
    }

    @Test
    void testParseLinkWithTooLongFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link abcd12345 ef567890"));
    }

    @Test
    void testParseLinkWithTooLongSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link abcd1234 ef567890123"));
    }

    @Test
    void testParseLinkWithSpecialCharactersFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link abcd-234 ef567890"));
    }

    @Test
    void testParseLinkWithSpecialCharactersSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link abcd1234 ef56@890"));
    }

    @Test
    void testParseLinkWithUppercaseFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link ABCD1234 ef567890"));
    }

    @Test
    void testParseLinkWithUppercaseSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link abcd1234 EF567890"));
    }

    @Test
    void testParseLinkWithInvalidHexCharactersFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link abcdefgh ef567890"));
    }

    @Test
    void testParseLinkWithInvalidHexCharactersSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link abcd1234 ghijk890"));
    }

    // ==================== Tag Command Tests ====================

    @Test
    void testParseTagAddWithValidIdAndTagReturnsTagNoteCommand() throws ZettelException {
        Command command = Parser.parse("tag add abcd1234 urgent");
        assertInstanceOf(TagNoteCommand.class, command);
    }

    @Test
    void testParseTagAddWithoutArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("tag add"));
    }

    @Test
    void testParseTagAddWithOnlyNoteIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("tag add abcd1234"));
    }

    @Test
    void testParseTagAddWithTooShortIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("tag add abc urgent"));
    }

    @Test
    void testParseTagAddWithTooLongIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("tag add abcd12345 urgent"));
    }

    @Test
    void testParseTagAddWithSpecialCharactersIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("tag add abcd-234 urgent"));
    }

    @Test
    void testParseTagAddWithUppercaseIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("tag add ABCD1234 urgent"));
    }

    @Test
    void testParseTagAddWithInvalidHexCharactersThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("tag add abcdefgh urgent"));
    }

    @Test
    void testParseUnknownCommandThrowsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> Parser.parse("unknown"));
    }

    // ==================== Unlink Command Tests ====================

    @Test
    void testParseUnlinkWithValidIdsReturnsUnlinkNotesCommand() throws ZettelException {
        Command command = Parser.parse("unlink abcd1234 ef567890");
        assertInstanceOf(UnlinkNotesCommand.class, command);
    }

    @Test
    void testParseUnlinkWithoutArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink"));
    }

    @Test
    void testParseUnlinkWithOnlyOneIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink abcd1234"));
    }

    @Test
    void testParseUnlinkWithTooManyArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink abcd1234 ef567890 12345678"));
    }

    @Test
    void testParseUnlinkWithTooShortFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink abc ef567890"));
    }

    @Test
    void testParseUnlinkWithTooShortSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink abcd1234 ef5"));
    }

    @Test
    void testParseUnlinkWithTooLongFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink abcd12345 ef567890"));
    }

    @Test
    void testParseUnlinkWithTooLongSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink abcd1234 ef567890123"));
    }

    @Test
    void testParseUnlinkWithSpecialCharactersFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink abcd-234 ef567890"));
    }

    @Test
    void testParseUnlinkWithSpecialCharactersSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink abcd1234 ef56@890"));
    }

    @Test
    void testParseUnlinkWithUppercaseFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink ABCD1234 ef567890"));
    }

    @Test
    void testParseUnlinkWithUppercaseSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink abcd1234 EF567890"));
    }

    @Test
    void testParseUnlinkWithInvalidHexCharactersFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink abcdefgh ef567890"));
    }

    @Test
    void testParseUnlinkWithInvalidHexCharactersSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink abcd1234 ghijk890"));
    }

    // ==================== Link-Both Command Tests ====================

    @Test
    void testParseLinkBothWithValidIdsReturnsLinkBothNotesCommand() throws ZettelException {
        Command command = Parser.parse("link-both abcd1234 ef567890");
        assertInstanceOf(LinkBothNotesCommand.class, command);
    }

    @Test
    void testParseLinkBothWithoutArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both"));
    }

    @Test
    void testParseLinkBothWithOnlyOneIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both abcd1234"));
    }

    @Test
    void testParseLinkBothWithTooManyArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both abcd1234 ef567890 12345678"));
    }

    @Test
    void testParseLinkBothWithTooShortFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both abc ef567890"));
    }

    @Test
    void testParseLinkBothWithTooShortSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both abcd1234 ef5"));
    }

    @Test
    void testParseLinkBothWithTooLongFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both abcd12345 ef567890"));
    }

    @Test
    void testParseLinkBothWithTooLongSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both abcd1234 ef567890123"));
    }

    @Test
    void testParseLinkBothWithSpecialCharactersFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both abcd-234 ef567890"));
    }

    @Test
    void testParseLinkBothWithSpecialCharactersSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both abcd1234 ef56@890"));
    }

    @Test
    void testParseLinkBothWithUppercaseFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both ABCD1234 ef567890"));
    }

    @Test
    void testParseLinkBothWithUppercaseSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both abcd1234 EF567890"));
    }

    @Test
    void testParseLinkBothWithInvalidHexCharactersFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both abcdefgh ef567890"));
    }

    @Test
    void testParseLinkBothWithInvalidHexCharactersSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both abcd1234 ghijk890"));
    }

    // ==================== Unlink-Both Command Tests ====================

    @Test
    void testParseUnlinkBothWithValidIdsReturnsUnlinkBothNotesCommand() throws ZettelException {
        Command command = Parser.parse("unlink-both abcd1234 ef567890");
        assertInstanceOf(UnlinkBothNotesCommand.class, command);
    }

    @Test
    void testParseUnlinkBothWithoutArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both"));
    }

    @Test
    void testParseUnlinkBothWithOnlyOneIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both abcd1234"));
    }

    @Test
    void testParseUnlinkBothWithTooManyArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both abcd1234 ef567890 12345678"));
    }

    @Test
    void testParseUnlinkBothWithTooShortFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both abc ef567890"));
    }

    @Test
    void testParseUnlinkBothWithTooShortSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both abcd1234 ef5"));
    }

    @Test
    void testParseUnlinkBothWithTooLongFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both abcd12345 ef567890"));
    }

    @Test
    void testParseUnlinkBothWithTooLongSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both abcd1234 ef567890123"));
    }

    @Test
    void testParseUnlinkBothWithSpecialCharactersFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both abcd-234 ef567890"));
    }

    @Test
    void testParseUnlinkBothWithSpecialCharactersSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both abcd1234 ef56@890"));
    }

    @Test
    void testParseUnlinkBothWithUppercaseFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both ABCD1234 ef567890"));
    }

    @Test
    void testParseUnlinkBothWithUppercaseSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both abcd1234 EF567890"));
    }

    @Test
    void testParseUnlinkBothWithInvalidHexCharactersFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both abcdefgh ef567890"));
    }

    @Test
    void testParseUnlinkBothWithInvalidHexCharactersSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both abcd1234 ghijk890"));
    }
}
