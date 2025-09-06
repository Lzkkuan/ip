package eve.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static eve.parser.parser.*;

public class ParserTest {

    @Test
    void parseCommand_variousTokens_ok() {
        assertEquals(Command.TODO, parser.parseCommand("todo read"));
        assertEquals(Command.DEADLINE, parser.parseCommand("deadline x /by 2019-12-02"));
        assertEquals(Command.EVENT, parser.parseCommand("event m /from a /to b"));
        assertNull(parser.parseCommand("unknownStuff"));
    }

    @Test
    void parseDeadline_goodAndBadInputs() throws EveException {
        DeadlineParts p = parser.parseDeadline("return book /by 2019-12-02");
        assertEquals("return book", p.desc);
        assertEquals("2019-12-02", p.when);

        EveException ex1 = assertThrows(EveException.class,
                () -> parser.parseDeadline("noByHere"));
        assertTrue(ex1.getMessage().startsWith("Oops, I need more info."));

        EveException ex2 = assertThrows(EveException.class,
                () -> parser.parseDeadline("  /by   "));
        assertTrue(ex2.getMessage().startsWith("Oops, I need more info."));
    }

    @Test
    void parseEvent_checksOrderIfParsable_allowsRawOtherwise() throws EveException {
        // valid range
        EventParts ok = parser.parseEvent("mtg /from 2019-12-02 1400 /to 2019-12-02 1600");
        assertEquals("mtg", ok.desc);

        // invalid range (both parse → reject)
        EveException badRange = assertThrows(EveException.class,
                () -> parser.parseEvent("oops /from 12 2 2019 12:00 /to 12 2 2018 12:00"));
        assertTrue(badRange.getMessage().contains("start is after end"));

        // one side not parseable → allowed (we can’t compare)
        EventParts raw = parser.parseEvent("orient /from next Mon 2pm /to 4pm");
        assertEquals("next Mon 2pm", raw.from);
        assertEquals("4pm", raw.to);
    }

    @Test
    void parseEvent_missingPieces_errorsMatch() {
        EveException noFrom = assertThrows(EveException.class,
                () -> parser.parseEvent("x /to 2019-12-02 10:00"));
        assertTrue(noFrom.getMessage().startsWith("Oops, I need more info."));

        EveException noTo = assertThrows(EveException.class,
                () -> parser.parseEvent("x /from 2019-12-02 10:00"));
        assertTrue(noTo.getMessage().startsWith("Oops, I need more info."));
    }
}
