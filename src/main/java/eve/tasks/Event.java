package eve.tasks;


import eve.util.DateTimeUtil;
import java.time.LocalDateTime;

public class Event extends Task {
    private final LocalDateTime fromDT, toDT; // parsed; may be null
    private final String fromRaw, toRaw;      // original texts if parse failed

    public Event(String description, String fromText, String toText) {
        super(description);
        this.fromDT = DateTimeUtil.parseDateTime(fromText).orElse(null);
        this.toDT   = DateTimeUtil.parseDateTime(toText).orElse(null);
        this.fromRaw = (fromDT == null) ? fromText : null;
        this.toRaw   = (toDT   == null) ? toText   : null;
    }

    public LocalDateTime getFromDT() { return fromDT; }
    public LocalDateTime getToDT()   { return toDT;   }
    public String getFromToken() { return (fromDT != null) ? DateTimeUtil.toIso(fromDT) : fromRaw; }
    public String getToToken()   { return (toDT   != null) ? DateTimeUtil.toIso(toDT)   : toRaw;   }

    @Override protected String getTypeIcon() { return "E"; }

    @Override
    public String toString() {
        String fromShown = (fromDT != null) ? DateTimeUtil.pretty(fromDT) : fromRaw;
        String toShown   = (toDT   != null) ? DateTimeUtil.pretty(toDT)   : toRaw;
        return super.toString() + " (from: " + fromShown + " to: " + toShown + ")";
    }
}
