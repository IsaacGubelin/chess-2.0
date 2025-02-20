package exception;

/**
 * Exception to be thrown when a request is unauthorized
 * <p>Associated with a 401 error response
 */
public class UnauthorizedException extends Exception {
    public UnauthorizedException(String message) {
        super(message);
    }
}
