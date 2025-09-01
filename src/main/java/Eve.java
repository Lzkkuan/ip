import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.time.LocalDateTime;
import java.util.Optional;
import util.DateTimeUtil;


import Storage.Storage;
import Tasks.Task;
import Tasks.Todo;
import Tasks.Deadline;
import Tasks.Event;

public class Eve {
    private static final String LINE = "____________________________________________________________";
    private static final String LOGO = " ______   __      __   ______ \n"
                                     + "| _____|  \\ \\    / /  | _____|\n"
                                     + "| |__      \\ \\  / /   | |__  \n"
                                     + "|  __|      \\ \\/ /    |  __| \n"
                                     + "| |____      \\  /     | |____ \n"
                                     + "|______|      \\/      |______|\n";

    // A-Collections: dynamic list for tasks
    private static final List<Task> tasks = new ArrayList<>();

    private static final Storage storage = new Storage("data/EVEStorage.txt"); 

    /** All supported commands, parsed case-insensitively from the first token */
    private enum Command {
        HELP, LIST, TODO, DEADLINE, EVENT, MARK, UNMARK, DELETE, BYE;

        static Command from(String s) {
            if (s == null) return null;
            switch (s.toLowerCase()) {
                case "help": return HELP;
                case "list": return LIST;
                case "todo": return TODO;
                case "deadline": return DEADLINE;
                case "event": return EVENT;
                case "mark": return MARK;
                case "unmark": return UNMARK;
                case "delete": return DELETE;
                case "bye": return BYE;
                default: return null;
            }
        }
    }

    public static void main(String[] args) {
        tasks.addAll(storage.load());
        greet();
        runLoop();
        exit();
    }

    /** Prints the greeting message */
    private static void greet() {
        System.out.println(LINE);
        System.out.println("Hello, I am Eve!\n" + LOGO);
        System.out.println(" What can I do for you?");
        System.out.println(LINE);
    }

    /** Main input loop using enum-based command parsing */
    private static void runLoop() {
        try (Scanner sc = new Scanner(System.in)) { // auto-closes, no leak warning
            while (true) {
                if (!sc.hasNextLine()) {
                    printWithLines("Goodbye (EOF).");
                    return;
                }
                String input = sc.nextLine().trim();
                if (input.isEmpty()) continue;

                String[] parts = input.split("\\s+", 2);
                Command cmd = Command.from(parts[0]);
                String args = parts.length > 1 ? parts[1].trim() : "";

                if (cmd == null) {
                    printWithLines("Sorry, I don't understand that. Type 'help' to see available commands.");
                    continue;
                }

                switch (cmd) {
                    case BYE:
                        return;
                    case HELP:
                        printHelp();
                        break;
                    case LIST:
                        printList();
                        break;
                    case TODO:
                        handleTodo(args);
                        break;
                    case DEADLINE:
                        handleDeadline(args);
                        break;
                    case EVENT:
                        handleEvent(args);
                        break;
                    case MARK:
                        handleMark(args, true);
                        break;
                    case UNMARK:
                        handleMark(args, false);
                        break;
                    case DELETE:
                        handleDelete(args);
                        break;
                }
            }
        }
    }

    /** Adds a task and prints the confirmation block */
    private static void addTask(Task t) {
        tasks.add(t);
        storage.save(tasks);
        System.out.println(LINE);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + t);
        System.out.println(" Now you have " + pluralize(tasks.size(), "task") + " in the list.");
        System.out.println(LINE);
    }

    /** Handle 'todo <desc>' */
    private static void handleTodo(String args) {
        if (args.isEmpty()) {
            printWithLines("Oops, I need more info. Usage: todo <description>");
            return;
        }
        addTask(new Todo(args));
    }

    /** Handle 'deadline <desc> /by <when>' (robust split; errors unchanged) */
    private static void handleDeadline(String args) {
        // Trim and split on "/by" ignoring case, allowing flexible spaces
        String[] parts = args.trim().split("(?i)\\s*/by\\s+", 2);
        if (parts.length < 2) {
            printWithLines("Oops, I need more info. Usage: deadline <description> /by <when>");
            return;
        }
        String desc = parts[0].trim();
        String when = parts[1].trim();

        if (desc.isEmpty() || when.isEmpty()) {
            printWithLines("Oops, I need more info. Usage: deadline <description> /by <when>");
            return;
        }

        // Pass tokens through; Deadline can parse dates/times internally (Level 8)
        addTask(new Deadline(desc, when));
    }

    /** Handle 'event <desc> /from <start> /to <end>' (robust split + range check) */
    private static void handleEvent(String args) {
        String trimmed = args.trim();

        String[] first = trimmed.split("(?i)\\s*/from\\s+", 2);
        if (first.length < 2) {
            printWithLines("Oops, I need more info. Usage: event <description> /from <start> /to <end>");
            return;
        }
        String desc = first[0].trim();
        String rest = first[1].trim();

        String[] second = rest.split("(?i)\\s*/to\\s+", 2);
        if (second.length < 2) {
            printWithLines("Oops, I need more info. Usage: event <description> /from <start> /to <end>");
            return;
        }
        String from = second[0].trim();
        String to   = second[1].trim();

        if (desc.isEmpty() || from.isEmpty() || to.isEmpty()) {
            printWithLines("Oops, I need more info. Usage: event <description> /from <start> /to <end>");
            return;
        }

        // NEW: if both sides parse, enforce from <= to
        Optional<LocalDateTime> fromDT = DateTimeUtil.parseDateTime(from);
        Optional<LocalDateTime> toDT   = DateTimeUtil.parseDateTime(to);
        if (fromDT.isPresent() && toDT.isPresent() && fromDT.get().isAfter(toDT.get())) {
            printWithLines("Sorry, that time range looks invalid: start is after end.");
            return;
        }

        addTask(new Event(desc, from, to)); // Event will also parse for pretty-printing
    }


    /** Print the full task list */
    private static void printList() {
        System.out.println(LINE);
        if (tasks.isEmpty()) {
            System.out.println(" No tasks yet.");
        } else {
            System.out.println(" Here are the tasks in your list:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println(" " + (i + 1) + "." + tasks.get(i));
            }
        }
        System.out.println(LINE);
    }

    /** Handle 'mark n' / 'unmark n' */
    private static void handleMark(String args, boolean toDone) {
        if (tasks.isEmpty()) {
            printWithLines("No tasks yet—add one before marking.");
            return;
        }
        if (!args.matches("\\d+")) {
            printWithLines("Use a number only, e.g., \"" + (toDone ? "mark 2" : "unmark 2") + "\".");
            return;
        }
        int n = Integer.parseInt(args);
        if (n < 1 || n > tasks.size()) {
            printWithLines("Please provide a valid task number (1-" + tasks.size() + ").");
            return;
        }
        Task t = tasks.get(n - 1);
        if (toDone) t.markAsDone(); else t.markAsNotDone();
        storage.save(tasks);

        System.out.println(LINE);
        if (toDone) {
            System.out.println(" Nice! I've marked this task as done:");
        } else {
            System.out.println(" OK, I've marked this task as not done yet:");
        }
        System.out.println("   " + t);
        System.out.println(LINE);
    }

    /** Handle 'delete n' */
    private static void handleDelete(String args) {
        if (tasks.isEmpty()) {
            printWithLines("No tasks yet—add one before deleting.");
            return;
        }
        if (!args.matches("\\d+")) {
            printWithLines("Use a number only, e.g., \"delete 3\".");
            return;
        }
        int n = Integer.parseInt(args);
        if (n < 1 || n > tasks.size()) {
            printWithLines("Please provide a valid task number (1-" + tasks.size() + ").");
            return;
        }
        Task removed = tasks.remove(n - 1);
        storage.save(tasks);

        System.out.println(LINE);
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + removed);
        System.out.println(" Now you have " + pluralize(tasks.size(), "task") + " in the list.");
        System.out.println(LINE);
    }

    /** Help text */
    private static void printHelp() {
        System.out.println(LINE);
        System.out.println(" Available commands:");
        System.out.println("   help                             - Show this help message.");
        System.out.println("   list                             - Show all tasks and status.");
        System.out.println("   todo <desc>                      - Add a ToDo task.");
        System.out.println("   deadline <desc> /by <t>          - Add a Deadline with due time.");
        System.out.println("   event <desc> /from <s> /to <e>   - Add an Event with start/end.");
        System.out.println("   mark N                           - Mark task N as done.");
        System.out.println("   unmark N                         - Mark task N as not done.");
        System.out.println("   delete N                         - Delete task N from the list.");
        System.out.println("   bye                              - Exit the program.");
        System.out.println(LINE);
    }

    /** Exit message */
    private static void exit() {
        System.out.println(LINE);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }

    /** Helpers */
    private static String pluralize(int n, String word) {
        return n + " " + word + (n == 1 ? "" : "s");
    }
    private static void printWithLines(String message) {
        System.out.println(LINE);
        System.out.println(" " + message);
        System.out.println(LINE);
    }
}
