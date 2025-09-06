import java.util.List;

import Storage.Storage;
import Tasks.Task;
import Tasks.Todo;
import parser.EveException;
import parser.parser;
import parser.parser.Command;
import parser.parser.DeadlineParts;
import parser.parser.EventParts;
import ui.ui;
import Tasks.Deadline;
import Tasks.Event;

public class Eve {
    private final ui ui = new ui();
    private final Storage storage = new Storage("data/eve.txt");
    private TaskList tasks;

    public Eve() {
        List<Task> loaded = storage.load();
        tasks = new TaskList(loaded);
    }

    public void run() {
        ui.showWelcome();
        boolean exit = false;
        while (!exit) {
            String full = ui.readCommand();
            if (full == null) { ui.showError("Goodbye (EOF)."); break; } // EOF
            full = full.trim();
            if (full.isEmpty()) continue;

            Command cmd = parser.parseCommand(full);
            if (cmd == null) { ui.showUnknown(); continue; }

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
                        Task t = tasks.add(new Deadline(p.desc, p.when)); // Deadline parses dates internally
                        storage.save(tasks.asList());
                        ui.showAdded(t, tasks.size());
                        break;
                    }
                    case EVENT: {
                        EventParts p = parser.parseEvent(args);          // validates range if parsable
                        Task t = tasks.add(new Event(p.desc, p.from, p.to)); // Event parses dates internally
                        storage.save(tasks.asList());
                        ui.showAdded(t, tasks.size());
                        break;
                    }
                    case MARK: {
                        int n = parser.parseIndex(args, true);
                        if (n < 1 || n > tasks.size()) { ui.showError("Please provide a valid task number (1-" + tasks.size() + ")."); break; }
                        Task t = tasks.setDone(n - 1, true);
                        storage.save(tasks.asList());
                        ui.showMarked(t, true);
                        break;
                    }
                    case UNMARK: {
                        int n = parser.parseIndex(args, false);
                        if (n < 1 || n > tasks.size()) { ui.showError("Please provide a valid task number (1-" + tasks.size() + ")."); break; }
                        Task t = tasks.setDone(n - 1, false);
                        storage.save(tasks.asList());
                        ui.showMarked(t, false);
                        break;
                    }
                    case DELETE: {
                        int n = parser.parseDeleteIndex(args);
                        if (n < 1 || n > tasks.size()) { ui.showError("Please provide a valid task number (1-" + tasks.size() + ")."); break; }
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

    public static void main(String[] args) {
        new Eve().run();
    }
}
