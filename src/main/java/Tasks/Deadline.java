package Tasks;

public class Deadline extends Task {
    private final String by;

    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    protected String getTypeIcon() {
        return "D";
    }

    @Override
    public String toString() {
        // e.g. [D][ ] return book (by: Sunday)
        return super.toString() + " (by: " + by + ")";
    }
}
