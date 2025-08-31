package Tasks;
public abstract class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    public void markAsDone() {
        this.isDone = true;
    }

    public void markAsNotDone() {
        this.isDone = false;
    }

    protected abstract String getTypeIcon();

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        // e.g. [T][X] read book
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
