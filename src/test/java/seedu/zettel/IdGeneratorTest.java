package seedu.zettel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the IdGenerator class.
 * Tests hash-based ID generation functionality.
 */
public class IdGeneratorTest {

    @Test
    @DisplayName("Generated ID should be exactly 8 characters long")
    void testGeneratedIdLength() {
        String id = IdGenerator.generateId("test input");
        assertNotNull(id, "Generated ID should not be null");
        assertEquals(8, id.length(), "Generated ID should be 8 characters long");
    }

    @Test
    @DisplayName("Generated ID should contain only lowercase hexadecimal characters")
    void testGeneratedIdFormat() {
        String id = IdGenerator.generateId("test input");
        assertTrue(id.matches("[a-f0-9]{8}"),
                "Generated ID should only contain lowercase hex characters (0-9, a-f)");
    }

    @Test
    @DisplayName("Same input should generate same ID (deterministic)")
    void testDeterministicGeneration() {
        String input = "My Test Note2025-01-01T00:00:00Z";
        String id1 = IdGenerator.generateId(input);
        String id2 = IdGenerator.generateId(input);

        assertEquals(id1, id2, "Same input should always generate the same ID");
    }

    @Test
    @DisplayName("Different inputs should generate different IDs")
    void testDifferentInputsDifferentIds() {
        String input1 = "First Note2025-01-01T00:00:00Z";
        String input2 = "Second Note2025-01-01T00:00:01Z";

        String id1 = IdGenerator.generateId(input1);
        String id2 = IdGenerator.generateId(input2);

        assertNotEquals(id1, id2, "Different inputs should generate different IDs");
    }

    @Test
    @DisplayName("Empty string input should generate valid ID")
    void testEmptyStringInput() {
        String id = IdGenerator.generateId("");
        assertNotNull(id, "ID should not be null even for empty string");
        assertEquals(8, id.length(), "ID should be 8 characters even for empty string");
        assertTrue(id.matches("[a-f0-9]{8}"), "ID should be valid hex even for empty string");
    }

    @Test
    @DisplayName("Very long input should generate valid 8-character ID")
    void testLongInputGeneratesValidId() {
        String longInput = "This is a very long input string that contains many characters " +
                "and should still generate a valid 8-character hexadecimal ID " +
                "because the hash function will compress it down to the same length";

        String id = IdGenerator.generateId(longInput);
        assertEquals(8, id.length(), "ID should still be 8 characters for long input");
        assertTrue(id.matches("[a-f0-9]{8}"), "ID should be valid hex for long input");
    }

    @Test
    @DisplayName("Special characters in input should generate valid ID")
    void testSpecialCharactersInput() {
        String input = "Note!@#$%^&*()_+-=[]{}|;':\",./<>?`~";
        String id = IdGenerator.generateId(input);

        assertNotNull(id, "ID should not be null for special characters");
        assertEquals(8, id.length(), "ID should be 8 characters for special characters");
        assertTrue(id.matches("[a-f0-9]{8}"), "ID should be valid hex for special characters");
    }

    @Test
    @DisplayName("Unicode characters in input should generate valid ID")
    void testUnicodeInput() {
        String input = "Note with 中文字符 and émojis 🎉";
        String id = IdGenerator.generateId(input);

        assertNotNull(id, "ID should not be null for Unicode input");
        assertEquals(8, id.length(), "ID should be 8 characters for Unicode input");
        assertTrue(id.matches("[a-f0-9]{8}"), "ID should be valid hex for Unicode input");
    }

    @Test
    @DisplayName("Similar inputs should generate different IDs")
    void testSimilarInputsDifferentIds() {
        String id1 = IdGenerator.generateId("test");
        String id2 = IdGenerator.generateId("Test");
        String id3 = IdGenerator.generateId("test ");

        // These should all be different due to case/whitespace sensitivity
        assertNotEquals(id1, id2, "Case difference should produce different IDs");
        assertNotEquals(id1, id3, "Whitespace difference should produce different IDs");
        assertNotEquals(id2, id3, "Different inputs should produce different IDs");
    }
}
