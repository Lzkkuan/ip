package Tasks;

public class Event extends Task {
    private final String from;
    private final String to;

    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    public String getFrom() { return from; }
    public String getTo()   { return to; }

    @Override
    protected String getTypeIcon() {
        return "E";
    }

    @Override
    public String toString() {
        if (to == null || to.isEmpty()) {
            // allow legacy combined range
            return super.toString() + " (from: " + from + ")";
        }
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
