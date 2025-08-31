package Tasks;

public class Event extends Task {
    private final String from;
    private final String to;

    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    protected String getTypeIcon() {
        return "E";
    }

    @Override
    public String toString() {
        // e.g. [E][ ] project meeting (from: Mon 2pm to: 4pm)
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
