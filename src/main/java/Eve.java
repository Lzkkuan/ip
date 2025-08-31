import java.util.Scanner;
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
    private static final int MAX_TASKS = 100;
    private static final Task[] tasks = new Task[MAX_TASKS];
    private static int taskCount = 0;

    public static void main(String[] args) {
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

    /** Main input loop: handles all commands */
    private static void runLoop() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            String input = sc.nextLine().trim();

            if (input.equals("bye")) {
                break;
            } else if (input.equals("list")) {
                printList();
            } else if (input.equals("help")) {
                printHelp();
            } else if (input.startsWith("mark ")) {
                handleMark(input, true);
            } else if (input.startsWith("unmark ")) {
                handleMark(input, false);
            } else if (input.startsWith("todo ")) {
                handleTodo(input);
            } else if (input.startsWith("deadline ")) {
                handleDeadline(input);
            } else if (input.startsWith("event ")) {
                handleEvent(input);
            } else if (!input.isEmpty()) {
                // default: treat as a quick "todo" add for backward-compat
                addTask(new Todo(input));
            } else {
                printWithLines("");
            }
        }
        sc.close();
    }

    /** Adds a task if capacity allows, printing the confirmation block */
    private static void addTask(Task t) {
        if (taskCount >= MAX_TASKS) {
            printWithLines("Sorry, the list is full (" + MAX_TASKS + " tasks).");
            return;
        }
        tasks[taskCount++] = t;

        System.out.println(LINE);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + t.toString());
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
        System.out.println(LINE);
    }

    /** Handle 'todo <desc>' */
    private static void handleTodo(String input) {
        String desc = input.substring("todo".length()).trim();
        if (desc.isEmpty()) {
            printWithLines("Usage: todo <description>");
            return;
        }
        addTask(new Todo(desc));
    }

    /** Handle 'deadline <desc> /by <when>' */
    private static void handleDeadline(String input) {
        String body = input.substring("deadline".length()).trim();
        String[] parts = body.split(" /by ", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            printWithLines("Usage: deadline <description> /by <when>");
            return;
        }
        addTask(new Deadline(parts[0].trim(), parts[1].trim()));
    }

    /** Handle 'event <desc> /from <start> /to <end>' */
    private static void handleEvent(String input) {
        String body = input.substring("event".length()).trim();
        String[] firstSplit = body.split(" /from ", 2);
        if (firstSplit.length < 2 || firstSplit[0].trim().isEmpty()) {
            printWithLines("Usage: event <description> /from <start> /to <end>");
            return;
        }
        String desc = firstSplit[0].trim();
        String[] secondSplit = firstSplit[1].split(" /to ", 2);
        if (secondSplit.length < 2 || secondSplit[0].trim().isEmpty() || secondSplit[1].trim().isEmpty()) {
            printWithLines("Usage: event <description> /from <start> /to <end>");
            return;
        }
        addTask(new Event(desc, secondSplit[0].trim(), secondSplit[1].trim()));
    }

    /** Print the full task list */
    private static void printList() {
        System.out.println(LINE);
        if (taskCount == 0) {
            System.out.println(" No tasks yet.");
        } else {
            System.out.println(" Here are the tasks in your list:");
            for (int i = 0; i < taskCount; i++) {
                System.out.println(" " + (i + 1) + "." + tasks[i]);
            }
        }
        System.out.println(LINE);
    }

    /** Handle 'mark n' / 'unmark n' using Task methods */
    private static void handleMark(String input, boolean toDone) {
        Integer idx = parseIndex(input);
        if (idx == null) {
            printWithLines("Please provide a valid task number (e.g., \"" + (toDone ? "mark 2" : "unmark 2") + "\").");
            return;
        }
        if (idx < 1 || idx > taskCount) {
            printWithLines("Please provide a valid task number (1-" + taskCount + ").");
            return;
        }
        Task t = tasks[idx - 1];
        if (toDone) {
            t.markAsDone();
        } else {
            t.markAsNotDone();
        }

        System.out.println(LINE);
        if (toDone) {
            System.out.println(" Nice! I've marked this task as done:");
        } else {
            System.out.println(" OK, I've marked this task as not done yet:");
        }
        System.out.println("   " + t);
        System.out.println(LINE);
    }

    /** Print the list of available commands */
    private static void printHelp() {
        System.out.println(LINE);
        System.out.println(" Available commands:");
        System.out.println("   help                      - Show this help message.");
        System.out.println("   list                      - Show all tasks and status.");
        System.out.println("   todo <desc>               - Add a ToDo task.");
        System.out.println("   deadline <desc> /by <t>   - Add a Deadline with due time.");
        System.out.println("   event <desc> /from <s> /to <e> - Add an Event with start/end.");
        System.out.println("   mark N                    - Mark task N as done.");
        System.out.println("   unmark N                  - Mark task N as not done.");
        System.out.println("   <text>                    - Quick add a ToDo with that text.");
        System.out.println("   bye                       - Exit the program.");
        System.out.println(LINE);
    }

    /** Parse the integer after a command */
    private static Integer parseIndex(String input) {
        String[] parts = input.split("\\s+");
        if (parts.length < 2) return null;
        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Exit message */
    private static void exit() {
        System.out.println(LINE);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }

    /** Print any text between horizontal lines */
    private static void printWithLines(String message) {
        System.out.println(LINE);
        System.out.println(" " + message);
        System.out.println(LINE);
    }
}
