package eve.tasks;

import eve.util.DateTimeUtil;
import java.time.LocalDateTime;

/**
 * Represents an event task that spans from a start date/time to an end
 * date/time.
 * <p>
 * The {@code Event} class attempts to parse the provided date/time strings into
 * {@link LocalDateTime} objects. If parsing fails (e.g., the user typed
 * a natural language string like "next Monday"), the raw input is stored and
 * used
 * for display and persistence.
 */
public class Event extends Task {
    /**
     * The parsed start datetime of the event, or {@code null} if parsing failed.
     */
    private final LocalDateTime fromDT;
    /** The parsed end datetime of the event, or {@code null} if parsing failed. */
    private final LocalDateTime toDT;

    /** The raw start text if parsing failed; {@code null} otherwise. */
    private final String fromRaw;
    /** The raw end text if parsing failed; {@code null} otherwise. */
    private final String toRaw;

    /**
     * Constructs an {@code Event} with the specified description and start/end
     * times.
     *
     * @param description the description of the event
     * @param fromText    the start time text (parsed if possible)
     * @param toText      the end time text (parsed if possible)
     */
    public Event(String description, String fromText, String toText) {
        super(description);
        this.fromDT = DateTimeUtil.parseDateTime(fromText).orElse(null);
        this.toDT = DateTimeUtil.parseDateTime(toText).orElse(null);
        this.fromRaw = (fromDT == null) ? fromText : null;
        this.toRaw = (toDT == null) ? toText : null;
    }

    /**
     * Returns the parsed start datetime of this event.
     *
     * @return the {@link LocalDateTime} start, or {@code null} if parsing failed
     */
    public LocalDateTime getFromDT() {
        return fromDT;
    }

    /**
     * Returns the parsed end datetime of this event.
     *
     * @return the {@link LocalDateTime} end, or {@code null} if parsing failed
     */
    public LocalDateTime getToDT() {
        return toDT;
    }

    /**
     * Returns a token representing the start of the event.
     * <ul>
     * <li>If parsed successfully, returns the ISO string (yyyy-MM-ddTHH:mm).</li>
     * <li>If parsing failed, returns the original raw text.</li>
     * </ul>
     *
     * @return ISO string or raw input of the start time
     */
    public String getFromToken() {
        return (fromDT != null) ? DateTimeUtil.toIso(fromDT) : fromRaw;
    }

    /**
     * Returns a token representing the end of the event.
     * <ul>
     * <li>If parsed successfully, returns the ISO string (yyyy-MM-ddTHH:mm).</li>
     * <li>If parsing failed, returns the original raw text.</li>
     * </ul>
     *
     * @return ISO string or raw input of the end time
     */
    public String getToToken() {
        return (toDT != null) ? DateTimeUtil.toIso(toDT) : toRaw;
    }

    /**
     * Returns the type icon identifying this as an event.
     *
     * @return the string {@code "E"}
     */
    @Override
    protected String getTypeIcon() {
        return "E";
    }

    /**
     * Returns a string representation of this event, including type, status,
     * description, and its start and end times.
     * <p>
     * Example:
     * {@code [E][ ] project meeting (from: 2019/12/2 14:00 to: 2019/12/2 16:00)}
     *
     * @return the formatted string representation of this event
     */
    @Override
    public String toString() {
        String fromShown = (fromDT != null) ? DateTimeUtil.pretty(fromDT) : fromRaw;
        String toShown = (toDT != null) ? DateTimeUtil.pretty(toDT) : toRaw;
        return super.toString() + " (from: " + fromShown + " to: " + toShown + ")";
    }
}
