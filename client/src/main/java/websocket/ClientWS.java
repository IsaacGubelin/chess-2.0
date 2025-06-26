package websocket;


import exception.ResponseException;

import javax.websocket.*;
import java.io.IOException;
import java.net.*;

public class ClientWS extends Endpoint {

    public Session session;
    private ServiceMessageHandler messageHandler;


    public ClientWS(String url, ServiceMessageHandler msgHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");

            this.messageHandler = msgHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() { // DO NOT replace with lambda
                @Override
                public void onMessage(String message) {
                    try {
                        msgHandler.notify(message);
                    } catch (ResponseException ex) {
                        System.out.println("Error: Response exception thrown by web socket facade.");
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }




    //Endpoint requires this method, but you don't have to do anything
    @Override
    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("onOpen reached within client websocket.");
    }

}
