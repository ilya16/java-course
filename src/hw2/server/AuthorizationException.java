package hw2.server;

/**
 * Exception used during authorization processes
 *
 * @author Ilya Borovik
 */
public class AuthorizationException extends Exception {

    /**
     * Constructor
     *
     * @param message   error message
     */
    public AuthorizationException(String message) {
        super(message);
    }
}
