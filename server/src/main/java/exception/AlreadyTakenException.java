package exception;

/**
 * Exception to be thrown when data is requested to be added that is already taken
 * <p>Associated with a 403 error response
 */
public class AlreadyTakenException extends Exception{
    public AlreadyTakenException(String message) {
        super(message);
    }
}
