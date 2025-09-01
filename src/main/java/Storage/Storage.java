package Storage;

import Tasks.Task;
import Tasks.Todo;
import Tasks.Deadline;
import Tasks.Event;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
/* no need to parse dates here; tasks will do it */

public class Storage {
    private final Path file;

    public Storage(String relativePath) { this.file = Paths.get(relativePath); }

    public List<Task> load() {
        List<Task> out = new ArrayList<>();
        try {
            if (!Files.exists(file)) {
                Path parent = file.getParent();
                if (parent != null && !Files.exists(parent)) Files.createDirectories(parent);
                return out;
            }
            try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                String line;
                while ((line = br.readLine()) != null) {
                    Task t = parseLine(line);
                    if (t != null) out.add(t);
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: failed to load tasks: " + e.getMessage());
        }
        return out;
    }

    public void save(List<Task> tasks) {
        try {
            Path parent = file.getParent();
            if (parent != null && !Files.exists(parent)) Files.createDirectories(parent);
            try (BufferedWriter bw = Files.newBufferedWriter(
                    file, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                for (Task t : tasks) {
                    bw.write(serialize(t));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: failed to save tasks: " + e.getMessage());
        }
    }

    private Task parseLine(String line) {
        if (line == null) return null;
        String[] parts = line.split("\\s*\\|\\s*");
        if (parts.length < 3) return null;

        String type = parts[0].trim();
        boolean isDone = "1".equals(parts[1].trim());

        try {
            switch (type) {
                case "T": {
                    Task t = new Todo(parts[2].trim());
                    if (isDone) t.markAsDone();
                    return t;
                }
                case "D": {
                    if (parts.length < 4) return null;
                    Task t = new Deadline(parts[2].trim(), parts[3].trim()); // parses internally
                    if (isDone) t.markAsDone();
                    return t;
                }
                case "E": {
                    // supports both 4-field (combined) and 5-field (split) forms
                    String desc = parts[2].trim();
                    String from = (parts.length >= 4 ? parts[3].trim() : "");
                    String to   = (parts.length >= 5 ? parts[4].trim() : "");
                    Task t = new Event(desc, from, to); // parses internally
                    if (isDone) t.markAsDone();
                    return t;
                }
                default: return null;
            }
        } catch (Exception ex) {
            return null; // treat as corrupted line
        }
    }

    private String serialize(Task t) {
        if (t instanceof Todo) {
            return String.format("T | %d | %s", isDone(t), t.getDescription());
        } else if (t instanceof Deadline) {
            Deadline d = (Deadline) t;
            return String.format("D | %d | %s | %s", isDone(t), d.getDescription(), d.getByToken());
        } else if (t instanceof Event) {
            Event e = (Event) t;
            return String.format("E | %d | %s | %s | %s", isDone(t), e.getDescription(), e.getFromToken(), e.getToToken());
        }
        return String.format("T | %d | %s", isDone(t), t.getDescription());
    }

    private int isDone(Task t) { return t.toString().contains("[X]") ? 1 : 0; }
}
