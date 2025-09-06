package eve.tasks;

import eve.util.DateTimeUtil;
import java.time.LocalDateTime;

/**
 * Represents a task of type {@code Deadline}.
 * <p>
 * A {@code Deadline} has a description and an associated due date/time.
 * The due date/time is parsed into a {@link LocalDateTime} if possible.
 * If parsing fails, the raw user input is stored instead.
 */
public class Deadline extends Task {
    /** The parsed due date/time of the deadline, or {@code null} if parsing failed. */
    private final LocalDateTime when;
    /** The raw text provided by the user if parsing failed; {@code null} otherwise. */
    private final String raw;

    /**
     * Constructs a {@code Deadline} with the given description and due date/time string.
     *
     * @param description the description of the deadline task
     * @param byText      the due date/time text (parsed if possible, stored raw otherwise)
     */
    public Deadline(String description, String byText) {
        super(description);
        this.when = DateTimeUtil.parseDateTime(byText).orElse(null);
        this.raw = (when == null) ? byText : null;
    }

    /**
     * Returns the parsed due date/time of this deadline.
     *
     * @return the {@link LocalDateTime} due date/time, or {@code null} if parsing failed
     */
    public LocalDateTime getWhen() { return when; }

    /**
     * Returns a token representing the due date/time of this deadline.
     * <ul>
     *   <li>If parsed successfully, returns the ISO string (yyyy-MM-ddTHH:mm).</li>
     *   <li>If parsing failed, returns the raw user input.</li>
     * </ul>
     *
     * @return ISO string or raw input of the due date/time
     */
    public String getByToken() {
        return (when != null) ? DateTimeUtil.toIso(when) : raw;
    }

    /**
     * Returns the type icon identifying this as a deadline.
     *
     * @return the string {@code "D"}
     */
    @Override
    protected String getTypeIcon() { return "D"; }

    /**
     * Returns a string representation of this deadline, including type, status,
     * description, and the due date/time (either parsed pretty format or raw text).
     * <p>
     * Example: {@code [D][ ] return book (by: 2019/12/2 18:00)}
     *
     * @return the formatted string representation of this deadline
     */
    @Override
    public String toString() {
        String shown = (when != null) ? DateTimeUtil.pretty(when) : raw;
        return super.toString() + " (by: " + shown + ")";
    }
}
