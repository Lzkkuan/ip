package eve.ui;

import java.util.Scanner;
import java.util.List;

import eve.tasks.Task;

public class ui {
    private static final String LINE = "____________________________________________________________";
    private static final String LOGO = " ______   __      __   ______ \n"
            + "| _____|  \\ \\    / /  | _____|\n"
            + "| |__      \\ \\  / /   | |__  \n"
            + "|  __|      \\ \\/ /    |  __| \n"
            + "| |____      \\  /     | |____ \n"
            + "|______|      \\/      |______|\n";
    private final Scanner sc = new Scanner(System.in);

    public void showWelcome() {
        System.out.println(LINE);
        System.out.println("Hello, I am Eve!\n" + LOGO);
        System.out.println(" What can I do for you?");
        System.out.println(LINE);
    }

    public String readCommand() {
        if (!sc.hasNextLine())
            return null; // EOF
        return sc.nextLine();
    }

    public void showLine() {
        System.out.println(LINE);
    }

    public void showGoodbye() {
        System.out.println(LINE);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }

    public void showUnknown() {
        printWithLines("Sorry, I don't understand that. Type 'help' to see available commands.");
    }

    public void showError(String msg) {
        printWithLines(msg);
    }

    public void showAdded(Task t, int count) {
        System.out.println(LINE);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + t);
        System.out.println(" Now you have " + count + (count == 1 ? " task" : " tasks") + " in the list.");
        System.out.println(LINE);
    }

    public void showMarked(Task t, boolean toDone) {
        System.out.println(LINE);
        if (toDone)
            System.out.println(" Nice! I've marked this task as done:");
        else
            System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + t);
        System.out.println(LINE);
    }

    public void showDeleted(Task removed, int newCount) {
        System.out.println(LINE);
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + removed);
        System.out.println(" Now you have " + newCount + (newCount == 1 ? " task" : " tasks") + " in the list.");
        System.out.println(LINE);
    }

    public void showList(List<Task> tasks) {
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

    /** Print search results in the required format. */
    public void showFindResults(java.util.List<eve.tasks.Task> matches) {
        System.out.println(LINE);
        if (matches.isEmpty()) {
            System.out.println(" No matching tasks found.");
        } else {
            System.out.println(" Here are the matching tasks in your list:");
            for (int i = 0; i < matches.size(); i++) {
                System.out.println(" " + (i + 1) + "." + matches.get(i));
            }
        }
        System.out.println(LINE);
    }

    public void showHelp() {
        System.out.println(LINE);
        System.out.println(" Available commands:");
        System.out.println("   help                             - Show this help message.");
        System.out.println("   list                             - Show all tasks and status.");
        System.out.println("   find <keyword>                   - Search tasks by keyword.");
        System.out.println("   todo <desc>                      - Add a ToDo task.");
        System.out.println("   deadline <desc> /by <time>       - Add a Deadline.");
        System.out.println("   event <desc> /from <start> /to <end> - Add an Event.");
        System.out.println("   mark N                           - Mark task N as done.");
        System.out.println("   unmark N                         - Mark task N as not done.");
        System.out.println("   delete N                         - Delete task N.");
        System.out.println("   bye                              - Exit the program.");
        System.out.println(LINE);
    }

    public void printWithLines(String message) {
        System.out.println(LINE);
        System.out.println(" " + message);
        System.out.println(LINE);
    }

    public String renderHelp() {
        return String.join("\n",
                LINE,
                " Commands:",
                "  help                  - Show this help",
                "  list                  - List tasks",
                "  todo <desc>           - Add todo",
                "  deadline <d> /by <t>  - Add deadline",
                "  event <d> /from <s> /to <e> - Add event",
                "  mark <n> / unmark <n> - Toggle done",
                "  delete <n>            - Delete task",
                "  find <kw>             - Search tasks (if you implemented Level-9)",
                "  bye                   - Exit",
                LINE);
    }

    public String renderList(List<Task> tasks) {
        StringBuilder sb = new StringBuilder();
        sb.append(LINE).append("\n Here are the tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(" ").append(i + 1).append(".").append(tasks.get(i).toString()).append("\n");
        }
        sb.append(LINE);
        return sb.toString();
    }

    public String renderAdded(Task t, int size) {
        return String.join("\n",
                LINE,
                " Got it. I've added this task:",
                "   " + t.toString(),
                " Now you have " + size + " tasks in the list.",
                LINE);
    }

    public String renderMarked(Task t, boolean done) {
        return String.join("\n",
                LINE,
                done ? " Nice! I've marked this task as done:" : " OK, I've marked this task as not done yet:",
                "   " + t.toString(),
                LINE);
    }

    public String renderDeleted(Task t, int size) {
        return String.join("\n",
                LINE,
                " Noted. I've removed this task:",
                "   " + t.toString(),
                " Now you have " + size + " tasks in the list.",
                LINE);
    }
}
