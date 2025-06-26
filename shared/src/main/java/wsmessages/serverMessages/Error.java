package wsmessages.serverMessages;

public class Error extends ServerMessage{

    private String errorMessage;
    public Error(ServerMessageType type) {
        super(type);
    }

    public void setMessage(String msg) {
        this.errorMessage = msg;
    }

    public String getMessage() {
        return errorMessage;
    }
}
