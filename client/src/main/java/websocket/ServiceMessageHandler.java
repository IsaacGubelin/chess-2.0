package websocket;


import exception.ResponseException;


public interface ServiceMessageHandler {
    void notify(String message) throws ResponseException;
}
