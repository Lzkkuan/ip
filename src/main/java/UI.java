import java.util.Scanner;
import java.util.List;
import Tasks.Task;

public class UI {
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
        if (!sc.hasNextLine()) return null; // EOF
        return sc.nextLine();
    }

    public void showLine() { System.out.println(LINE); }

    public void showGoodbye() {
        System.out.println(LINE);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }

    public void showUnknown() {
        printWithLines("Sorry, I don't understand that. Type 'help' to see available commands.");
    }

    public void showError(String msg) { printWithLines(msg); }

    public void showAdded(Task t, int count) {
        System.out.println(LINE);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + t);
        System.out.println(" Now you have " + count + (count == 1 ? " task" : " tasks") + " in the list.");
        System.out.println(LINE);
    }

    public void showMarked(Task t, boolean toDone) {
        System.out.println(LINE);
        if (toDone) System.out.println(" Nice! I've marked this task as done:");
        else System.out.println(" OK, I've marked this task as not done yet:");
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

    public void showHelp() {
        System.out.println(LINE);
        System.out.println(" Available commands:");
        System.out.println("   help                             - Show this help message.");
        System.out.println("   list                             - Show all tasks and status.");
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
}
