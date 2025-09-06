package eve.util;

import java.time.*;
import java.time.format.*;
import java.util.*;

/**
 * Utility class for parsing, formatting, and normalizing date/time values
 * used by Eve tasks (e.g. Deadlines and Events).
 * <p>
 * Supports a wide variety of input formats for convenience:
 * <ul>
 *   <li>{@code yyyy-MM-dd} (ISO date)</li>
 *   <li>{@code yyyy-MM-dd HH:mm} or {@code yyyy-MM-dd HHmm}</li>
 *   <li>{@code d/M/yyyy}, {@code d-M-yyyy}, and space-separated forms
 *       (with or without time)</li>
 * </ul>
 * <p>
 * Outputs are normalized to a consistent format: {@code yyyy/M/d} for
 * dates, or {@code yyyy/M/d HH:mm} for date-times.
 */
public final class DateTimeUtil {
    /** Prevent instantiation. */
    private DateTimeUtil() {}

    /**
     * Accepted input patterns for parsing date-only strings.
     * Includes ISO ({@code yyyy-MM-dd}), slash-separated, dash-separated,
     * and space-separated formats.
     */
    private static final List<DateTimeFormatter> DATE_PATTERNS = List.of(
        DateTimeFormatter.ISO_LOCAL_DATE,                                        // yyyy-MM-dd
        new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("d/M/uuuu").toFormatter(), // 2/12/2019
        new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("d-M-uuuu").toFormatter(), // 2-12-2019
        new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("d M uuuu").toFormatter(), // 2 12 2019
        new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("M d uuuu").toFormatter()  // 12 2 2019
    );

    /**
     * Accepted input patterns for parsing date + time strings.
     * Includes ISO ({@code yyyy-MM-ddTHH:mm}), and flexible formats
     * with dashes, slashes, or spaces.
     */
    private static final List<DateTimeFormatter> DATETIME_PATTERNS = List.of(
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,                                   // 2019-12-02T18:00
        new DateTimeFormatterBuilder().parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE).appendLiteral(' ').appendPattern("HHmm").toFormatter(),  // yyyy-MM-dd 1800
        new DateTimeFormatterBuilder().parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE).appendLiteral(' ').appendPattern("HH:mm").toFormatter(), // yyyy-MM-dd 18:00
        new DateTimeFormatterBuilder().parseCaseInsensitive()
            .appendPattern("d/M/uuuu HHmm").toFormatter(),                       // 2/12/2019 1800
        new DateTimeFormatterBuilder().parseCaseInsensitive()
            .appendPattern("d/M/uuuu HH:mm").toFormatter(),                      // 2/12/2019 18:00
        new DateTimeFormatterBuilder().parseCaseInsensitive()
            .appendPattern("d-M-uuuu HHmm").toFormatter(),                       // 2-12-2019 1800
        new DateTimeFormatterBuilder().parseCaseInsensitive()
            .appendPattern("d-M-uuuu HH:mm").toFormatter(),                      // 2-12-2019 18:00
        new DateTimeFormatterBuilder().parseCaseInsensitive()
            .appendPattern("M d uuuu HHmm").toFormatter(),                       // 12 2 2019 1800
        new DateTimeFormatterBuilder().parseCaseInsensitive()
            .appendPattern("M d uuuu HH:mm").toFormatter(),                      // 12 2 2019 12:00
        new DateTimeFormatterBuilder().parseCaseInsensitive()
            .appendPattern("d M uuuu HHmm").toFormatter(),                       // 2 12 2019 1800
        new DateTimeFormatterBuilder().parseCaseInsensitive()
            .appendPattern("d M uuuu HH:mm").toFormatter()                       // 2 12 2019 18:00
    );

    /** Output format for dates (no leading zeros). */
    private static final DateTimeFormatter OUT_DATE = DateTimeFormatter.ofPattern("yyyy/M/d");       
    /** Output format for date-times (no leading zeros for day/month). */
    private static final DateTimeFormatter OUT_DT   = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm"); 

    /**
     * Attempts to parse a string into a {@link LocalDateTime}.
     * <ul>
     *   <li>If the string matches a date-time format, a full {@code LocalDateTime} is returned.</li>
     *   <li>If the string matches a date-only format, a {@code LocalDateTime} at midnight is returned.</li>
     *   <li>If parsing fails for all formats, {@link Optional#empty()} is returned.</li>
     * </ul>
     *
     * @param s the input string
     * @return an {@code Optional} containing the parsed date/time, or empty if none match
     */
    public static Optional<LocalDateTime> parseDateTime(String s) {
        if (s == null) return Optional.empty();
        String x = s.trim();
        for (DateTimeFormatter f : DATETIME_PATTERNS) {
            try { return Optional.of(LocalDateTime.parse(x, f)); } catch (Exception ignored) {}
        }
        for (DateTimeFormatter f : DATE_PATTERNS) {
            try { return Optional.of(LocalDate.parse(x, f).atStartOfDay()); } catch (Exception ignored) {}
        }
        return Optional.empty();
    }

    /**
     * Formats a {@link LocalDateTime} into a user-friendly string.
     * If the time component is midnight (00:00), only the date is shown.
     *
     * @param dt the date/time to format
     * @return a pretty string (e.g. {@code 2019/12/2} or {@code 2019/12/2 18:00})
     */
    public static String pretty(LocalDateTime dt) {
        if (dt.toLocalTime().equals(LocalTime.MIDNIGHT)) return dt.toLocalDate().format(OUT_DATE);
        return dt.format(OUT_DT);
    }

    /**
     * Converts a {@link LocalDateTime} into its ISO-8601 string form.
     * Useful for persistent storage in save files.
     *
     * @param dt the date/time to convert
     * @return ISO-8601 string (e.g. {@code 2019-12-02T18:00})
     */
    public static String toIso(LocalDateTime dt) { 
        return dt.toString(); 
    }
}
