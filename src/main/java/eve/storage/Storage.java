package eve.storage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

import eve.tasks.Task;
import eve.tasks.Todo;
import eve.tasks.Deadline;
import eve.tasks.Event;

/**
 * Handles loading and saving tasks to persistent storage on disk.
 * <p>
 * Tasks are stored in a plain text file with a simple line-based format:
 * 
 * <pre>
 *   T | 1 | read book
 *   D | 0 | return book | 2019-12-02
 *   E | 0 | meeting | 2019-12-02T14:00 | 2019-12-02T16:00
 * </pre>
 * 
 * Each line represents a task of type {@code Todo}, {@code Deadline}, or
 * {@code Event}.
 * Parsing of dates/times is delegated to the individual task classes.
 */
public class Storage {
    /** Path to the data file on disk. */
    private final Path file;

    /**
     * Constructs a {@code Storage} object for the given relative file path.
     *
     * @param relativePath the relative path to the data file (e.g.
     *                     {@code "data/eve.txt"})
     */
    public Storage(String relativePath) {
        this.file = Paths.get(relativePath);
    }

    /**
     * Loads all tasks from the data file into memory.
     * <p>
     * If the file does not exist, it will be created along with its parent
     * directories.
     * If the file contains corrupted or unrecognized lines, those lines will be
     * ignored.
     *
     * @return a list of {@link Task} objects loaded from storage
     */
    public List<Task> load() {
        List<Task> out = new ArrayList<>();
        try {
            if (!Files.exists(file)) {
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
                    if (t != null) {
                        out.add(t);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: failed to load tasks: " + e.getMessage());
        }
        return out;
    }

    /**
     * Saves the given list of tasks to the data file, overwriting its contents.
     * <p>
     * If the parent directories do not exist, they will be created automatically.
     *
     * @param tasks the list of tasks to be written to storage
     */
    public void save(List<Task> tasks) {
        try {
            Path parent = file.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            try (BufferedWriter bw = Files.newBufferedWriter(
                    file, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                for (Task t : tasks) {
                    bw.write(serialize(t));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: failed to save tasks: " + e.getMessage());
        }
    }

    /**
     * Parses a single line from the data file into a {@link Task} object.
     * <p>
     * Supports the following types:
     * <ul>
     * <li>{@code T} = Todo</li>
     * <li>{@code D} = Deadline</li>
     * <li>{@code E} = Event</li>
     * </ul>
     * If parsing fails, returns {@code null}.
     *
     * @param line a line from the storage file
     * @return the corresponding {@link Task}, or {@code null} if parsing failed
     */
    private Task parseLine(String line) {
        if (line == null)
            return null;
        String[] parts = line.split("\\s*\\|\\s*");
        if (parts.length < 3)
            return null;

        String type = parts[0].trim();
        boolean isDone = "1".equals(parts[1].trim());

        try {
            switch (type) {
                case "T": {
                    Task t = new Todo(parts[2].trim());
                    if (isDone)
                        t.markAsDone();
                    return t;
                }
                case "D": {
                    if (parts.length < 4)
                        return null;
                    Task t = new Deadline(parts[2].trim(), parts[3].trim());
                    if (isDone)
                        t.markAsDone();
                    return t;
                }
                case "E": {
                    String desc = parts[2].trim();
                    String from = (parts.length >= 4 ? parts[3].trim() : "");
                    String to = (parts.length >= 5 ? parts[4].trim() : "");
                    Task t = new Event(desc, from, to);
                    if (isDone)
                        t.markAsDone();
                    return t;
                }
                default:
                    return null;
            }
        } catch (Exception ex) {
            return null; // treat as corrupted line
        }
    }

    /**
     * Serializes a {@link Task} into a line of text suitable for saving.
     *
     * @param t the task to serialize
     * @return a string representation of the task
     */
    private String serialize(Task t) {
        if (t instanceof Todo) {
            return String.format("T | %d | %s", isDone(t), t.getDescription());
        } else if (t instanceof Deadline) {
            Deadline d = (Deadline) t;
            return String.format("D | %d | %s | %s",
                    isDone(t), d.getDescription(), d.getByToken());
        } else if (t instanceof Event) {
            Event e = (Event) t;
            return String.format("E | %d | %s | %s | %s",
                    isDone(t), e.getDescription(), e.getFromToken(), e.getToToken());
        }
        return String.format("T | %d | %s", isDone(t), t.getDescription());
    }

    /**
     * Determines whether a task is marked as done.
     *
     * @param t the task to check
     * @return {@code 1} if done, {@code 0} otherwise
     */
    private int isDone(Task t) {
        return t.toString().contains("[X]") ? 1 : 0;
    }
}
