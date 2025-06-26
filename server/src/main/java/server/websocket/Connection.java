package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import wsmessages.serverMessages.Notification;

import java.io.IOException;

/**
 * A collection of these will be held in a data structure for easy sending of notifications.
 * <p>
 * Auth tokens are always unique.
 */

public class Connection {
    public String authToken;
    public Session session;

    public Connection(String authToken, Session session) {
        this.authToken = authToken;     // Assign token
        this.session = session;         // Assign session
    }

    /**
     * Sends a string to the WebSocket facade on the client end.
     * @param msg
     * @throws IOException
     */
    public void send(Notification msg) throws IOException {
        session.getRemote().sendString(new Gson().toJson(msg));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection that = (Connection) o;
        return authToken.equals(that.authToken) && session.equals(that.session);
    }
}
