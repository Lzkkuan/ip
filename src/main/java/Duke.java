import java.util.Scanner;

public class Duke {
    private static final String LINE = "____________________________________________________________";
    private static final String LOGO = " ______   __      __   ______ \n"
                                     + "| _____|  \\ \\    / /  | _____|\n"
                                     + "| |__      \\ \\  / /   | |__  \n"
                                     + "|  __|      \\ \\/ /    |  __| \n"
                                     + "| |____      \\  /     | |____ \n"
                                     + "|______|      \\/      |______|\n";
        public static void main(String[] args) {
        greet();
        runEcho();
        exit();
    }

    /** Prints the greeting message */
    private static void greet() {
        System.out.println(LINE);
        System.out.println("Hello, I am\n" + LOGO);
        System.out.println(" What can I do for you?");
        System.out.println(LINE);
    }

    /** Handles the echo loop */
    private static void runEcho() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            String input = sc.nextLine();
            if (input.equals("bye")) {
                break;
            }
            printWithLines(input);
        }
        sc.close();
    }

    /** Prints the exit message */
    private static void exit() {
        System.out.println(LINE);
        System.out.println(" Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }

    /** Helper method to wrap any text with horizontal lines */
    private static void printWithLines(String message) {
        System.out.println(LINE);
        System.out.println(" " + message);
        System.out.println(LINE);
    }
}
