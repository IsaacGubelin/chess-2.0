package dataaccess;

import java.sql.SQLException;
import java.util.ArrayList;

import exception.AlreadyTakenException;
import exception.DataAccessException;
import model.GameData;

import javax.xml.crypto.Data;

public interface GameDAO {

    void clearGameTable();
    int createGame(String gameName) throws SQLException;

    void updateWhiteUsername(int gameID, String whiteUsername) throws DataAccessException;

    void updateBlackUsername(int gameID, String whiteUsername) throws DataAccessException;

    public ArrayList<GameData> getGamesList() throws SQLException;

    boolean hasGame(int gameID);

    boolean hasAvailableTeam(int gameID, String team) throws DataAccessException;

    boolean isEmpty() throws DataAccessException;
}