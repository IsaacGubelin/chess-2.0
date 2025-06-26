package facade;
import chess.ChessMove;
import com.google.gson.Gson;

import model.*;
import exception.ResponseException;
import websocket.ServiceMessageHandler;
import websocket.ClientWS;
import wsmessages.userCommands.*;
import java.io.*;


public class WSFacade {
    public final String serverUrl;                                  // Connect to server
    private final ClientWS clientSocket;                     // Access the methods in the web socket facade

    public WSFacade(String url, ServiceMessageHandler msgHandler) throws ResponseException {
        clientSocket = new ClientWS(url, msgHandler);
        serverUrl = url;
    }

    public WSFacade(int port, ServiceMessageHandler msgHandler) throws ResponseException {
        String url = "http://localhost:" + port;   // Create url with port number
        clientSocket = new ClientWS(url, msgHandler);
        serverUrl = url;
    }



    /**
     * A function called by the chess client to join an available game.
     * @param gameReqData Contains needed data about which game and team color to join
     * @param authToken Used for user verification
     */
    public void joinGame(String authToken, JoinGameRequest gameReqData) throws ResponseException {
        try {
            JoinPlayer cmd = new JoinPlayer(authToken, gameReqData.gameID(), gameReqData.playerColor());
            clientSocket.session.getBasicRemote().sendText(new Gson().toJson(cmd));

        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void joinObserve(String authToken, int id) throws ResponseException {
        try {
            JoinObserver observeCmd = new JoinObserver(authToken, id);
            clientSocket.session.getBasicRemote().sendText(new Gson().toJson(observeCmd));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    /**
     * This sends a MakeMove command out to the server websocket handler. If successful, client will receive a
     * LoadGame message with an updated chessboard.
     * @param authToken Verification
     * @param gameID Game being played
     * @param move Proposed chess move
     * @throws ResponseException If an IO exception is caught, this exception is thrown afterward
     */
    public void playerMakeMove(String authToken, int gameID, ChessMove move) throws ResponseException {
        try {
            MakeMove moveCmd = new MakeMove(authToken, gameID, move);   // Create a new make move command
            clientSocket.session.getBasicRemote().sendText(new Gson().toJson(moveCmd));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leaveGame(String authToken, int gameID) throws ResponseException {
        Leave leaveCmd = new Leave(authToken, gameID);
        try {
            clientSocket.session.getBasicRemote().sendText(new Gson().toJson(leaveCmd));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void playerResign(String authToken, int gameID) throws ResponseException {
        Resign resCmd = new Resign(authToken, gameID);
        try {
            clientSocket.session.getBasicRemote().sendText(new Gson().toJson(resCmd));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
}
