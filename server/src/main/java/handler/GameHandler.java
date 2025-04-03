package handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exception.DataAccessException;
import exception.ResponseException;
import model.*;
import service.GameService;
import spark.Request;
import spark.Response;

public class GameHandler {

    private final GameService gameService;
    private final String AUTH_HEADER = "Authorization";
    private final Gson gs = new Gson();

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
            String requestedGameName = gs.fromJson(req.body(), CreateGameRequestData.class).gameName();
            int gameID = gameService.createGame(authToken, requestedGameName);
            res.status(200);
            return gs.toJson(new GameData(gameID, null, null, null, null));
        } catch (ResponseException e) {
            res.status(e.getStatusCode());
            return gs.toJson(new MessageData(e.getMessage()));
        } catch (DataAccessException e) {
            res.status(500);
            return gs.toJson(new MessageData(e.getMessage()));
        }
    }

    /**
     * Handle the request to list all games.
     * @param req HTTP request data
     * @param res HTTP response object to modify
     * @return JSON response body and status code
     */
    public Object handleListGames(Request req, Response res) {
        String authToken = req.headers(AUTH_HEADER);
        try {
            ListGamesResponseData gamesList = gameService.listGames(authToken);
            res.status(200);
            return gs.toJson(gamesList);
        } catch (ResponseException e) {
            res.status(e.getStatusCode());
            return gs.toJson(new MessageData(e.getMessage()));
        } catch (DataAccessException e) {
            res.status(500);
            return gs.toJson(new MessageData(e.getMessage()));
        }
    }

    public Object handleJoinGame(Request req, Response res) {

        String authToken = req.headers(AUTH_HEADER);
        JoinGameRequest joinGameData = gs.fromJson(req.body(), JoinGameRequest.class);

        try {
            gameService.joinGame(authToken, joinGameData);
            res.status(200);
            return "{}";  // No json body for join response
        } catch (ResponseException e) {
            res.status(e.getStatusCode());
            return gs.toJson(new MessageData(e.getMessage()));
        } catch (DataAccessException e) {
            res.status(500);
            return gs.toJson(new MessageData(e.getMessage()));
        }
    }
}
