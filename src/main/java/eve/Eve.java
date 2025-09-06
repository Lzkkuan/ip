package eve;

import java.util.List;

import eve.ui.ui;
import eve.parser.parser;
import eve.parser.EveException;
import eve.parser.parser.Command;
import eve.parser.parser.DeadlineParts;
import eve.parser.parser.EventParts;
import eve.storage.Storage;
import eve.tasks.Task;
import eve.tasks.Todo;
import eve.tasks.Deadline;
import eve.tasks.Event;

/**
 * Entry point for the Eve chatbot application.
 * <p>
 * The Eve class wires together the UI, parser, storage, and task list
 * components to provide an interactive command-line chatbot that
 * manages tasks such as Todos, Deadlines, and Events.
 * </p>
 */
public class Eve {

    /** Handles all user input and output. */
    private final ui ui = new ui();

    /** Responsible for loading and saving tasks to disk. */
    private final Storage storage = new Storage("data/eve.txt");

    /** Encapsulates the in-memory list of tasks. */
    private TaskList tasks;

    /**
     * Constructs a new {@code Eve} chatbot.
     * <p>
     * Loads previously saved tasks from storage into memory.
     * </p>
     */
    public Eve() {
        List<Task> loaded = storage.load();
        tasks = new TaskList(loaded);
    }

    /**
     * Starts the main chatbot loop.
     * <p>
     * Continuously reads user input, parses it into a {@link Command},
     * executes the command, and updates the task list and storage
     * accordingly. Terminates when the user enters the {@code bye}
     * command or when end-of-file (EOF) is reached.
     * </p>
     */
    public void run() {
        ui.showWelcome();
        boolean exit = false;
        while (!exit) {
            String full = ui.readCommand();
            if (full == null) {
                ui.showError("Goodbye (EOF).");
                break;
            } // EOF
            full = full.trim();
            if (full.isEmpty())
                continue;

            Command cmd = parser.parseCommand(full);
            if (cmd == null) {
                ui.showUnknown();
                continue;
            }

            String args = parser.args(full);
            try {
                switch (cmd) {
                    case HELP:
                        ui.showHelp();
                        break;
                    case LIST:
                        ui.showList(tasks.asList());
                        break;
                    case TODO: {
                        String desc = parser.parseTodoDesc(args);
                        Task t = tasks.add(new Todo(desc));
                        storage.save(tasks.asList());
                        ui.showAdded(t, tasks.size());
                        break;
                    }
                    case DEADLINE: {
                        DeadlineParts p = parser.parseDeadline(args);
                        Task t = tasks.add(new Deadline(p.desc, p.when));
                        storage.save(tasks.asList());
                        ui.showAdded(t, tasks.size());
                        break;
                    }
                    case EVENT: {
                        EventParts p = parser.parseEvent(args);
                        Task t = tasks.add(new Event(p.desc, p.from, p.to));
                        storage.save(tasks.asList());
                        ui.showAdded(t, tasks.size());
                        break;
                    }
                    case MARK: {
                        int n = parser.parseIndex(args, true);
                        if (n < 1 || n > tasks.size()) {
                            ui.showError("Please provide a valid task number (1-" + tasks.size() + ").");
                            break;
                        }
                        Task t = tasks.setDone(n - 1, true);
                        storage.save(tasks.asList());
                        ui.showMarked(t, true);
                        break;
                    }
                    case UNMARK: {
                        int n = parser.parseIndex(args, false);
                        if (n < 1 || n > tasks.size()) {
                            ui.showError("Please provide a valid task number (1-" + tasks.size() + ").");
                            break;
                        }
                        Task t = tasks.setDone(n - 1, false);
                        storage.save(tasks.asList());
                        ui.showMarked(t, false);
                        break;
                    }
                    case DELETE: {
                        int n = parser.parseDeleteIndex(args);
                        if (n < 1 || n > tasks.size()) {
                            ui.showError("Please provide a valid task number (1-" + tasks.size() + ").");
                            break;
                        }
                        Task removed = tasks.deleteAt(n - 1);
                        storage.save(tasks.asList());
                        ui.showDeleted(removed, tasks.size());
                        break;
                    }
                    case BYE:
                        exit = true;
                        break;
                }
            } catch (EveException e) {
                ui.showError(e.getMessage());
            }
        }
        ui.showGoodbye();
    }

    /**
     * Application entry point.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        new Eve().run();
    }
}
