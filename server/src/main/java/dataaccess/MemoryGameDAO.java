package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    private final int GAME_ID_MIN = 1;
    private final String BLACK_TEAM = "BLACK";
    private final String WHITE_TEAM = "WHITE";

    private HashMap<Integer, GameData> gamesTable = new HashMap<>();

    // A game ID generator that automatically finds available ID numbers.
    private int generateID() {
        int id = GAME_ID_MIN;    // Starting ID value
        while (gamesTable.containsKey(id)) { // Increment if the id value is already taken
            id++;
        }
        return id;
    }

    // Clear all GameData entries from database
    @Override
    public void clearGameTable() {
        gamesTable.clear();
    }

    // Create and add a new chess game to the database
    @Override
    public int createGame(String gameName) {
        int id = generateID(); // Make new game ID

        if (id >= 0) { // Skip game creation if ID rolls over to negative. This means the games are full.
            GameData game = new GameData(id, null, null, gameName, new ChessGame());
            gamesTable.put(game.gameID(), game);
        }
        return id;
    }

    // A getter for retrieving the games database
    @Override
    public ArrayList<GameData> getGamesList() {
        return new ArrayList<>(gamesTable.values());
    }

    // Use to update the white team with a new user
    @Override
    public void updateWhiteUsername(int gameID, String whiteUsername) {
        String blackUsername = gamesTable.get(gameID).blackUsername();  // Use for updated game record
        String gameName = gamesTable.get(gameID).gameName();            // Use for updated game record
        GameData game = new GameData(gameID, whiteUsername, blackUsername, gameName, gamesTable.get(gameID).game());
        gamesTable.put(gameID, game);   // Overwrite old game data with new data
    }

    // Use to update a new user for the black team
    @Override
    public void updateBlackUsername(int gameID, String blackUsername) {
        String whiteUsername = gamesTable.get(gameID).whiteUsername();  // Use for updated game record
        String gameName = gamesTable.get(gameID).gameName();            // Use for updated game record
        GameData game = new GameData(gameID, whiteUsername, blackUsername, gameName, gamesTable.get(gameID).game());
        gamesTable.put(gameID, game);   // Overwrite old data with new data
    }

    @Override
    public boolean hasGame(int gameID) {
        return gamesTable.containsKey(gameID);
    }
    @Override
    public boolean isEmpty() {
        return gamesTable.isEmpty();
    }


    public boolean hasAvailableWhiteTeam(int gameID) {
        return gamesTable.get(gameID).whiteUsername() == null;
    }

    public boolean hasAvailableBlackTeam(int gameID) {
        return gamesTable.get(gameID).blackUsername() == null;
    }
}