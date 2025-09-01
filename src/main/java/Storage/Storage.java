package Storage;

import Tasks.Task;
import Tasks.Todo;
import Tasks.Deadline;
import Tasks.Event;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private final Path file;

    /** Use a relative path like "data/duke.txt" */
    public Storage(String relativePath) {
        this.file = Paths.get(relativePath);
    }

    /** Load tasks from disk; if file/folder is missing, return empty list. */
    public List<Task> load() {
        List<Task> out = new ArrayList<>();
        try {
            if (!Files.exists(file)) {
                // First run: nothing to load; ensure parent folder exists for future saves
                Path parent = file.getParent();
                if (parent != null && !Files.exists(parent)) {
                    Files.createDirectories(parent);
                }
                return out;
            }
            try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                String line;
                while ((line = br.readLine()) != null) {
                    Task t = parseLine(line);
                    if (t != null) out.add(t); // corrupted lines are skipped
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: failed to load tasks: " + e.getMessage());
        }
        return out;
    }

    /** Save tasks to disk; atomically replaces existing file. */
    public void save(List<Task> tasks) {
        try {
            Path parent = file.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            // CREATE + TRUNCATE_EXISTING = overwrite each time
            try (BufferedWriter bw = Files.newBufferedWriter(
                    file, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                for (Task t : tasks) {
                    bw.write(serialize(t));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: failed to save tasks: " + e.getMessage());
        }
    }

    // ---------- private helpers ----------

    // File format examples:
    // T | 1 | read book
    // D | 0 | return book | June 6th
    // E | 0 | project meeting | Aug 6th 2pm | 4pm
    private Task parseLine(String line) {
        if (line == null) return null;
        String[] parts = line.split("\\s*\\|\\s*");
        // minimal sanity check
        if (parts.length < 3) return null;

        String type = parts[0].trim();
        String doneStr = parts[1].trim();
        boolean isDone = "1".equals(doneStr);

        try {
            switch (type) {
                case "T": {
                    String desc = parts[2].trim();
                    if (desc.isEmpty()) return null;
                    Task t = new Todo(desc);
                    if (isDone) t.markAsDone();
                    return t;
                }
                case "D": {
                    if (parts.length < 4) return null;
                    String desc = parts[2].trim();
                    String by = parts[3].trim();
                    Task t = new Deadline(desc, by);
                    if (isDone) t.markAsDone();
                    return t;
                }
                case "E": {
                    // we support two styles:
                    //  - combined range in parts[3] (legacy) -> store whole in "from", empty "to"
                    //  - split from/to in parts[3], parts[4]
                    if (parts.length == 4) {
                        String desc = parts[2].trim();
                        String combined = parts[3].trim(); // e.g., "Aug 6th 2-4pm"
                        Task t = new Event(desc, combined, ""); // keep as-is
                        if (isDone) t.markAsDone();
                        return t;
                    } else if (parts.length >= 5) {
                        String desc = parts[2].trim();
                        String from = parts[3].trim();
                        String to = parts[4].trim();
                        Task t = new Event(desc, from, to);
                        if (isDone) t.markAsDone();
                        return t;
                    }
                    return null;
                }
                default:
                    return null;
            }
        } catch (Exception ex) {
            // any unexpected parse error -> skip line
            return null;
        }
    }

    private String serialize(Task t) {
        if (t instanceof Todo) {
            return String.format("T | %d | %s", tIsDone(t), t.getDescription());
        } else if (t instanceof Deadline) {
            Deadline d = (Deadline) t;
            return String.format("D | %d | %s | %s",
                    tIsDone(t), d.getDescription(), d.getBy());
        } else if (t instanceof Event) {
            Event e = (Event) t;
            // Prefer split from/to; if "to" is empty we still write the field
            return String.format("E | %d | %s | %s | %s",
                    tIsDone(t), e.getDescription(), e.getFrom(), e.getTo());
        }
        // Fallback to Task string (shouldn't happen with our hierarchy)
        return String.format("T | %d | %s", tIsDone(t), t.getDescription());
    }

    private int tIsDone(Task t) { return t.toString().contains("[X]") ? 1 : 0; }
}
