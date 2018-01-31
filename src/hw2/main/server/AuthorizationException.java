package hw2.main.server;

public class AuthorizationException extends Exception {
    public AuthorizationException() {
    }

    public AuthorizationException(String message) {
        super(message);
    }
}
