package seedu.zettel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Simple JUnit tests for the Zettel main class.
 */
class ZettelTest {

    @Test
    void testZettelConstructor() {
        Zettel zettel = new Zettel();
        assertNotNull(zettel);
    }

    @Test
    void testMainMethodExists() {
        assertNotNull(Zettel.class);
    }
}