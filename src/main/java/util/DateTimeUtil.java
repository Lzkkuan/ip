package util;

import java.time.*;
import java.time.format.*;
import java.util.*;

public final class DateTimeUtil {
    private DateTimeUtil() {}

    // Accepted input patterns (dates only)
    private static final List<DateTimeFormatter> DATE_PATTERNS = List.of(
        DateTimeFormatter.ISO_LOCAL_DATE,                                        // yyyy-MM-dd
        new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("d/M/uuuu").toFormatter(), // 2/12/2019
        new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("d-M-uuuu").toFormatter(), // 2-12-2019
        // NEW: space-separated variants
        new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("d M uuuu").toFormatter(), // 2 12 2019
        new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("M d uuuu").toFormatter()  // 12 2 2019
    );

    // Accepted input patterns (date + time)
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
        // NEW: space-separated month/day/year
        new DateTimeFormatterBuilder().parseCaseInsensitive()
            .appendPattern("M d uuuu HHmm").toFormatter(),                       // 12 2 2019 1800
        new DateTimeFormatterBuilder().parseCaseInsensitive()
            .appendPattern("M d uuuu HH:mm").toFormatter(),                      // 12 2 2019 12:00
        // NEW: space-separated day/month/year
        new DateTimeFormatterBuilder().parseCaseInsensitive()
            .appendPattern("d M uuuu HHmm").toFormatter(),                       // 2 12 2019 1800
        new DateTimeFormatterBuilder().parseCaseInsensitive()
            .appendPattern("d M uuuu HH:mm").toFormatter()                       // 2 12 2019 18:00
    );

    // Output formats (no leading zeros for month/day)
    private static final DateTimeFormatter OUT_DATE = DateTimeFormatter.ofPattern("yyyy/M/d");       // 2019/12/2
    private static final DateTimeFormatter OUT_DT   = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm"); // 2019/12/2 18:00

    /** Try parse as LocalDateTime; if only date parses, return date at 00:00. */
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

    /** Pretty: if time is 00:00, show just date; else date+time. */
    public static String pretty(LocalDateTime dt) {
        if (dt.toLocalTime().equals(LocalTime.MIDNIGHT)) return dt.toLocalDate().format(OUT_DATE);
        return dt.format(OUT_DT);
    }

    /** ISO tokens for storage. */
    public static String toIso(LocalDateTime dt) { return dt.toString(); } // e.g. 2019-12-02T18:00
}
