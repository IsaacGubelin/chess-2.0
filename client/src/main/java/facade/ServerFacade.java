package facade;

import model.*;
import exception.ResponseException;


public class ServerFacade {

    private final HTTPFacade httpFacade;  // HTTP facade for making requests



    public ServerFacade(String url) {
        httpFacade = new HTTPFacade(url);
    }

    public ServerFacade(int port) {
        httpFacade = new HTTPFacade("http://localhost:" + port);
    }

    public AuthData register(UserData userData) throws ResponseException {
        return httpFacade.register(userData);
    }


    public AuthData login(UserData userData) throws ResponseException {
        return httpFacade.login(userData);
    }


    public void logout(String authToken) throws ResponseException {
        httpFacade.logout(authToken);
    }


    public GameData createGame(String authToken, String gameName) throws ResponseException {
        return httpFacade.createGame(authToken, gameName);
    }

    /**
     * A function called by the chess client to join an available game.
     * @param gameReqData
     * @param authToken
     */
    public void joinGame(String authToken, JoinGameRequest gameReqData) throws ResponseException {
        httpFacade.joinGame(authToken, gameReqData);
    }

    public ListGamesResponseData getGamesList(String authToken) throws ResponseException {
        return httpFacade.getGamesList(authToken);
    }




}
