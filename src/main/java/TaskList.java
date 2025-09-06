import java.util.ArrayList;
import java.util.List;
import Tasks.Task;

public class TaskList {
    private final List<Task> tasks;

    public TaskList() { this.tasks = new ArrayList<>(); }
    public TaskList(List<Task> initial) { this.tasks = new ArrayList<>(initial); }

    public int size() { return tasks.size(); }
    public Task get(int idx0) { return tasks.get(idx0); }
    public List<Task> asList() { return tasks; }

    public Task add(Task t) { tasks.add(t); return t; }

    public Task deleteAt(int idx0) { return tasks.remove(idx0); }

    public Task setDone(int idx0, boolean done) {
        Task t = tasks.get(idx0);
        if (done) t.markAsDone(); else t.markAsNotDone();
        return t;
    }
}
