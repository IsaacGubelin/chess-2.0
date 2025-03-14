package dataaccess;

import java.util.ArrayList;

import exception.DataAccessException;
import model.GameData;

public interface GameDAO {

    void clearGameTable();
    int createGame(String gameName) throws DataAccessException;

    void updateWhiteUsername(int gameID, String whiteUsername) throws DataAccessException;

    void updateBlackUsername(int gameID, String whiteUsername) throws DataAccessException;

    public ArrayList<GameData> getGamesList() throws DataAccessException;

    boolean hasGame(int gameID);

    boolean hasAvailableWhiteTeam(int gameID) throws DataAccessException;

    boolean hasAvailableBlackTeam(int gameID) throws DataAccessException;

    boolean isEmpty();
}