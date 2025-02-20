package exception;

/**
 * Exception to be thrown when a request is missing required information
 * <p>Associated with a 400 error response
 */

public class BadRequestException extends Exception{
    public BadRequestException(String message) {
        super(message);
    }
}
