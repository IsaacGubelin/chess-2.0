package exception;

/**
 * Exception to be thrown when a request is missing required information
 * <p>Associated with a 400 error response
 */

public class ResponseException extends Exception{

    private final int statusCode;
    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
