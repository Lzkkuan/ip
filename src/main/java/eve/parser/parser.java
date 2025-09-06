package eve.parser;

import java.time.LocalDateTime;
import java.util.Optional;

import eve.util.DateTimeUtil;

public final class parser {
    private parser() {}

    public enum Command { HELP, LIST, TODO, DEADLINE, EVENT, MARK, UNMARK, DELETE, BYE }

    /** Parse the first word to a command; return null if unknown. */
    public static Command parseCommand(String full) {
        if (full == null) return null;
        String trimmed = full.trim();
        if (trimmed.isEmpty()) return null;
        String head = trimmed.split("\\s+", 2)[0].toLowerCase();
        switch (head) {
            case "help": return Command.HELP;
            case "list": return Command.LIST;
            case "todo": return Command.TODO;
            case "deadline": return Command.DEADLINE;
            case "event": return Command.EVENT;
            case "mark": return Command.MARK;
            case "unmark": return Command.UNMARK;
            case "delete": return Command.DELETE;
            case "bye": return Command.BYE;
            default: return null;
        }
    }

    /** Return everything after the first token (may be empty). */
    public static String args(String full) {
        String[] parts = full.trim().split("\\s+", 2);
        return parts.length > 1 ? parts[1].trim() : "";
    }

    public static String parseTodoDesc(String args) throws EveException {
        if (args == null || args.trim().isEmpty())
            throw new EveException("Oops, I need more info. Usage: todo <description>");
        return args.trim();
    }

    public static int parseIndex(String args, boolean toDone) throws EveException {
        if (args == null || args.trim().isEmpty())
            throw new EveException("Please provide a task number (e.g., \"" + (toDone ? "mark 2" : "unmark 2") + "\").");
        if (!args.matches("\\d+"))
            throw new EveException("Use a number only, e.g., \"" + (toDone ? "mark 2" : "unmark 2") + "\".");
        return Integer.parseInt(args);
    }

    public static int parseDeleteIndex(String args) throws EveException {
        if (args == null || args.trim().isEmpty())
            throw new EveException("Use a number only, e.g., \"delete 3\".");
        if (!args.matches("\\d+"))
            throw new EveException("Use a number only, e.g., \"delete 3\".");
        return Integer.parseInt(args);
    }

    /** Deadline: "<desc> /by <when>" (same error strings as before) */
    public static DeadlineParts parseDeadline(String args) throws EveException {
        String[] parts = args.trim().split("(?i)\\s*/by\\s+", 2);
        if (parts.length < 2) throw new EveException("Oops, I need more info. Usage: deadline <description> /by <when>");
        String desc = parts[0].trim();
        String when = parts[1].trim();
        if (desc.isEmpty() || when.isEmpty())
            throw new EveException("Oops, I need more info. Usage: deadline <description> /by <when>");
        return new DeadlineParts(desc, when);
    }

    /** Event: "<desc> /from <start> /to <end>" with optional range validation */
    public static EventParts parseEvent(String args) throws EveException {
        String[] first = args.trim().split("(?i)\\s*/from\\s+", 2);
        if (first.length < 2) throw new EveException("Oops, I need more info. Usage: event <description> /from <start> /to <end>");
        String desc = first[0].trim();
        String[] second = first[1].split("(?i)\\s*/to\\s+", 2);
        if (second.length < 2) throw new EveException("Oops, I need more info. Usage: event <description> /from <start> /to <end>");
        String from = second[0].trim();
        String to   = second[1].trim();
        if (desc.isEmpty() || from.isEmpty() || to.isEmpty())
            throw new EveException("Oops, I need more info. Usage: event <description> /from <start> /to <end>");

        // If both parse, check range
        Optional<LocalDateTime> f = DateTimeUtil.parseDateTime(from);
        Optional<LocalDateTime> t = DateTimeUtil.parseDateTime(to);
        if (f.isPresent() && t.isPresent() && f.get().isAfter(t.get()))
            throw new EveException("Sorry, that time range looks invalid: start is after end.");

        return new EventParts(desc, from, to);
    }

    // tiny value objects
    public static final class DeadlineParts {
        public final String desc, when;
        public DeadlineParts(String d, String w) { this.desc = d; this.when = w; }
    }
    public static final class EventParts {
        public final String desc, from, to;
        public EventParts(String d, String f, String t) { this.desc = d; this.from = f; this.to = t; }
    }
}
