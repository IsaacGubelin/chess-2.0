package handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exception.DataAccessException;
import exception.ResponseException;
import model.CreateGameRequestData;
import model.GameData;
import model.ListGamesResponseData;
import model.MessageData;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.ArrayList;

public class GameHandler {

    private final GameService gameService;
    private final String AUTH_HEADER = "Authorization";

    /**
     * The constructor for the GameHandler class. Takes DAOs to pass to new GameService object.
     * @param authDAO the data access object for authentication data
     * @param gameDAO the data access object for game data
     */
    public GameHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.gameService = new GameService(authDAO, gameDAO);
    }

    /**
     * Handle the request to create a new game.
     * @param req HTTP request data
     * @param res HTTP response object to modify
     * @return JSON response body and status code
     */
    public Object handleCreateGame(Request req, Response res) {
        String authToken = req.headers(AUTH_HEADER);
        try {
            String requestedGameName = new Gson().fromJson(req.body(), CreateGameRequestData.class).gameName();
            int gameID = gameService.createGame(authToken, requestedGameName);
            res.status(200);
            return new Gson().toJson(new GameData(gameID, null, null, null, null));
        } catch (ResponseException e) {
            res.status(e.getStatusCode());
            return new Gson().toJson(new MessageData(e.getMessage()));
        } catch (DataAccessException e) {
            res.status(500);
            return new Gson().toJson(new MessageData(e.getMessage()));
        }
    }

    public Object handleListGames(Request req, Response res) {
        String authToken = req.headers(AUTH_HEADER);
        try {
            ListGamesResponseData gamesList = gameService.listGames(authToken);
            res.status(200);
            return new Gson().toJson(gamesList);
        } catch (ResponseException e) {
            res.status(e.getStatusCode());
            return new Gson().toJson(new MessageData(e.getMessage()));
        } catch (DataAccessException e) {
            res.status(500);
            return new Gson().toJson(new MessageData(e.getMessage()));
        }
    }

    public Object handleJoinGame() {
        return null;
    }
}
