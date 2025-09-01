package Tasks;

import util.DateTimeUtil;
import java.time.LocalDateTime;

public class Deadline extends Task {
    private final LocalDateTime when; // parsed; may be null
    private final String raw;         // original text if parse failed

    public Deadline(String description, String byText) {
        super(description);
        this.when = DateTimeUtil.parseDateTime(byText).orElse(null);
        this.raw = (when == null) ? byText : null;
    }

    public LocalDateTime getWhen() { return when; }
    public String getByToken() { return (when != null) ? DateTimeUtil.toIso(when) : raw; }

    @Override protected String getTypeIcon() { return "D"; }

    @Override
    public String toString() {
        String shown = (when != null) ? DateTimeUtil.pretty(when) : raw;
        return super.toString() + " (by: " + shown + ")";
    }
}
