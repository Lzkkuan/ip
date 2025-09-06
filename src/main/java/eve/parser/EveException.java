package eve.parser;

/**
 * Represents an application-specific exception used within the Eve chatbot.
 * <p>
 * This exception is thrown when the parser or other components encounter
 * invalid user input, unexpected formats, or any other recoverable error
 * that should be reported back to the user rather than crashing the program.
 */
public class EveException extends Exception {

    /**
     * Constructs a new {@code EveException} with the specified detail message.
     *
     * @param msg the detail message to describe the cause of the exception
     */
    public EveException(String msg) {
        super(msg);
    }
}
