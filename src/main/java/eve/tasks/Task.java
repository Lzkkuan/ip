package eve.tasks;

/**
 * Represents a generic task with a description and completion status.
 * <p>
 * All concrete task types (e.g., {@link Todo}, {@link Deadline}, {@link Event})
 * should extend this class and implement their own type icon via
 * {@link #getTypeIcon()}.
 */
public abstract class Task {
    /** The description of the task, provided by the user. */
    protected String description;

    /** Whether the task has been marked as done. */
    protected boolean isDone;

    /**
     * Constructs a {@code Task} with the specified description.
     * Tasks are initialized as not done by default.
     *
     * @param description the details of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon representing whether this task is done.
     *
     * @return {@code "X"} if done, {@code " "} (a single space) if not done
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the type icon used to distinguish different task types.
     * For example, {@code "T"} for Todo, {@code "D"} for Deadline,
     * and {@code "E"} for Event.
     *
     * @return the type icon string for this task
     */
    protected abstract String getTypeIcon();

    /**
     * Returns the description of this task.
     *
     * @return the description string
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns a string representation of this task, including
     * its type, status, and description.
     * <p>
     * Example: {@code [T][X] read book}
     *
     * @return the formatted string representation of the task
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
