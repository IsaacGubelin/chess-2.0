package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exception.DataAccessException;
import exception.ResponseException;
import model.GameData;
import model.JoinGameRequest;
import model.ListGamesResponseData;
import java.util.ArrayList;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private final String BLACK_TEAM = "BLACK";
    private final String WHITE_TEAM = "WHITE";

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public int createGame(String authToken, String gameName) throws ResponseException, DataAccessException {

        if (authToken == null || gameName == null) {
            throw new ResponseException(400, "Error: bad request");
        } else if (!authDAO.hasAuth(authToken)) {
            throw new ResponseException(401, "Error: unauthorized");
        }

        int gameID = gameDAO.createGame(gameName);
        if (gameID < 0) {
            throw new ResponseException(500, "Error: Games are full");
        }
        return gameID;
    }

    /**
     * This function returns a list of all games in the games database.
     * The elements in the list hold all data in a GameData model except the ChessGame object.
     * @param authToken the authentication token
     * @return a list of all games in the games database held in a ListGamesResponseData model object
     * @throws ResponseException if the request is bad or unauthorized
     * @throws DataAccessException if there is an issue accessing the data
     */
    public ListGamesResponseData listGames(String authToken) throws ResponseException, DataAccessException {

        if (authToken == null) {
            throw new ResponseException(400, "Error: bad request");
        } else if (!authDAO.hasAuth(authToken)) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        ArrayList<GameData> gameList = new ArrayList<>();
        for (GameData game : gameDAO.getGamesList()) {
            gameList.add(new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), null));
        }
        return new ListGamesResponseData(gameList); // Turn list of games into model object
    }

    private void joinToTeam(JoinGameRequest joinRequest, String name) throws DataAccessException, ResponseException {
        String color = joinRequest.playerColor();
        int gameID = joinRequest.gameID();
        if (color.equals(BLACK_TEAM)) {
            if (!gameDAO.hasAvailableBlackTeam(gameID))
                throw new ResponseException(403, "Error: Black team already taken.");
            gameDAO.updateBlackUsername(gameID, name); // If good to do so, update black team name
        }
        else if (color.equals(WHITE_TEAM)) {
            if (!gameDAO.hasAvailableWhiteTeam(gameID)) // If white team isn't available
                throw new ResponseException(403, "Error: White team already taken.");
            gameDAO.updateWhiteUsername(gameID, name); // Update white team name
        } else  // If none of previous conditions were met, request is bad.
            throw new ResponseException(400, "Error: Given color does not match options.");
    }

    public void joinGame(String authToken, JoinGameRequest joinRequest) throws ResponseException, DataAccessException {
        if (authToken == null || joinRequest.playerColor() == null || !gameDAO.hasGame(joinRequest.gameID())) {
            throw new ResponseException(400, "Error: bad request");
        } else if (!authDAO.hasAuth(authToken)) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        joinToTeam(joinRequest, authDAO.getAuth(authToken).username());
    }

}
