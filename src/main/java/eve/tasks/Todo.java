package eve.tasks;

/**
 * Represents a task of type {@code Todo}.
 * <p>
 * A {@code Todo} is a simple task that only has a description
 * and does not have any associated date or time.
 */
public class Todo extends Task {

    /**
     * Constructs a {@code Todo} with the given description.
     *
     * @param description the details of the todo task
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the type icon used to identify a {@code Todo} in listings.
     *
     * @return the string {@code "T"} to indicate this is a Todo task
     */
    @Override
    protected String getTypeIcon() {
        return "T";
    }
}
