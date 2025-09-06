package eve.parser;

import java.time.LocalDateTime;
import java.util.Optional;

import eve.util.DateTimeUtil;

/**
 * Utility class responsible for parsing user input strings into commands,
 * arguments, and structured values such as task descriptions or date/time.
 * <p>
 * The {@code Parser} translates raw user input into more structured
 * representations (enums, indices, or parts objects) that the main
 * application logic can handle.
 */
public final class parser {
    private parser() {
    }

    /**
     * Enumerates the set of supported commands that the chatbot recognizes.
     */
    public enum Command {
        HELP, LIST, TODO, DEADLINE, EVENT, MARK, UNMARK, DELETE, BYE
    }

    /**
     * Parses the first word of the given input into a {@link Command}.
     *
     * @param full the full input string entered by the user
     * @return the parsed {@link Command}, or {@code null} if unknown
     */
    public static Command parseCommand(String full) {
        if (full == null)
            return null;
        String trimmed = full.trim();
        if (trimmed.isEmpty())
            return null;
        String head = trimmed.split("\\s+", 2)[0].toLowerCase();
        switch (head) {
            case "help":
                return Command.HELP;
            case "list":
                return Command.LIST;
            case "todo":
                return Command.TODO;
            case "deadline":
                return Command.DEADLINE;
            case "event":
                return Command.EVENT;
            case "mark":
                return Command.MARK;
            case "unmark":
                return Command.UNMARK;
            case "delete":
                return Command.DELETE;
            case "bye":
                return Command.BYE;
            default:
                return null;
        }
    }

    /**
     * Returns the argument portion of the input (everything after the first token).
     *
     * @param full the full input string
     * @return the substring after the first token, trimmed; empty if none
     */
    public static String args(String full) {
        String[] parts = full.trim().split("\\s+", 2);
        return parts.length > 1 ? parts[1].trim() : "";
    }

    /**
     * Parses the description for a {@code Todo} task.
     *
     * @param args the argument string after the "todo" keyword
     * @return the description text
     * @throws EveException if the description is empty or missing
     */
    public static String parseTodoDesc(String args) throws EveException {
        if (args == null || args.trim().isEmpty())
            throw new EveException("Oops, I need more info. Usage: todo <description>");
        return args.trim();
    }

    /**
     * Parses a numeric index for mark/unmark commands.
     *
     * @param args   the argument string, expected to be a number
     * @param toDone whether this is for mark (true) or unmark (false),
     *               used to customize the error message
     * @return the parsed integer index
     * @throws EveException if the argument is missing or not a valid number
     */
    public static int parseIndex(String args, boolean toDone) throws EveException {
        if (args == null || args.trim().isEmpty())
            throw new EveException(
                    "Please provide a task number (e.g., \"" + (toDone ? "mark 2" : "unmark 2") + "\").");
        if (!args.matches("\\d+"))
            throw new EveException("Use a number only, e.g., \"" + (toDone ? "mark 2" : "unmark 2") + "\".");
        return Integer.parseInt(args);
    }

    /**
     * Parses a numeric index for the delete command.
     *
     * @param args the argument string, expected to be a number
     * @return the parsed integer index
     * @throws EveException if the argument is missing or not a valid number
     */
    public static int parseDeleteIndex(String args) throws EveException {
        if (args == null || args.trim().isEmpty())
            throw new EveException("Use a number only, e.g., \"delete 3\".");
        if (!args.matches("\\d+"))
            throw new EveException("Use a number only, e.g., \"delete 3\".");
        return Integer.parseInt(args);
    }

    /**
     * Parses arguments for a {@code Deadline} command.
     * <p>
     * Expected format: {@code <description> /by <when>}
     *
     * @param args the raw argument string after the "deadline" keyword
     * @return a {@link DeadlineParts} containing description and time string
     * @throws EveException if the format is invalid or missing parts
     */
    public static DeadlineParts parseDeadline(String args) throws EveException {
        String[] parts = args.trim().split("(?i)\\s*/by\\s+", 2);
        if (parts.length < 2)
            throw new EveException("Oops, I need more info. Usage: deadline <description> /by <when>");
        String desc = parts[0].trim();
        String when = parts[1].trim();
        if (desc.isEmpty() || when.isEmpty())
            throw new EveException("Oops, I need more info. Usage: deadline <description> /by <when>");
        return new DeadlineParts(desc, when);
    }

    /**
     * Parses arguments for an {@code Event} command.
     * <p>
     * Expected format: {@code <description> /from <start> /to <end>}
     *
     * @param args the raw argument string after the "event" keyword
     * @return an {@link EventParts} containing description, start, and end strings
     * @throws EveException if the format is invalid or the start is after end
     */
    public static EventParts parseEvent(String args) throws EveException {
        String[] first = args.trim().split("(?i)\\s*/from\\s+", 2);
        if (first.length < 2)
            throw new EveException("Oops, I need more info. Usage: event <description> /from <start> /to <end>");
        String desc = first[0].trim();
        String[] second = first[1].split("(?i)\\s*/to\\s+", 2);
        if (second.length < 2)
            throw new EveException("Oops, I need more info. Usage: event <description> /from <start> /to <end>");
        String from = second[0].trim();
        String to = second[1].trim();
        if (desc.isEmpty() || from.isEmpty() || to.isEmpty())
            throw new EveException("Oops, I need more info. Usage: event <description> /from <start> /to <end>");

        // If both parse, validate range
        Optional<LocalDateTime> f = DateTimeUtil.parseDateTime(from);
        Optional<LocalDateTime> t = DateTimeUtil.parseDateTime(to);
        if (f.isPresent() && t.isPresent() && f.get().isAfter(t.get()))
            throw new EveException("Sorry, that time range looks invalid: start is after end.");

        return new EventParts(desc, from, to);
    }

    /**
     * Simple value object holding parts of a parsed deadline command.
     */
    public static final class DeadlineParts {
        /** The description of the task. */
        public final String desc;
        /** The deadline string provided by the user. */
        public final String when;

        public DeadlineParts(String d, String w) {
            this.desc = d;
            this.when = w;
        }
    }

    /**
     * Simple value object holding parts of a parsed event command.
     */
    public static final class EventParts {
        /** The description of the event. */
        public final String desc;
        /** The start time string provided by the user. */
        public final String from;
        /** The end time string provided by the user. */
        public final String to;

        public EventParts(String d, String f, String t) {
            this.desc = d;
            this.from = f;
            this.to = t;
        }
    }
}
