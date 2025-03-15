package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import exception.DataAccessException;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;


public class SQLGameDAO implements GameDAO {

    private Connection conn;
    private final String GAME_TABLE_NAME = "games";
    private final String GAME_KEY_COL = "gameID";
    private static final String WHITE_TEAM_COL = "whiteUsername";
    private static final String BLACK_TEAM_COL = "blackUsername";
    private static final String GAME_NAME_COL = "gameName";
    private static final String GAME_COL = "game";

    // For creating game table in chess database
    private final String[] createGameTableStatements = {
            String.format("""
            CREATE TABLE IF NOT EXISTS %s
            (
              `%s` int NOT NULL AUTO_INCREMENT,
              `%s` varchar(256),
              `%s` varchar(256),
              `%s` varchar(256) NOT NULL,
              `%s` json NOT NULL,
              PRIMARY KEY (`%s`)
            );
            """, GAME_TABLE_NAME, GAME_KEY_COL, WHITE_TEAM_COL, BLACK_TEAM_COL, GAME_NAME_COL, GAME_COL, GAME_KEY_COL)
    };

    public SQLGameDAO() {
        try {
            DatabaseManager.createDatabase();
            conn = DatabaseManager.getConnection();
            for (String statement : createGameTableStatements) {
                PreparedStatement ps = conn.prepareStatement(statement);
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException ex) {
            System.out.println("Couldn't create database");
        }
    }
    @Override
    public void clearGameTable() {

        String clearStmt = String.format("TRUNCATE TABLE %s", GAME_TABLE_NAME);
        try {
            ExecuteSQL.executeUpdate(clearStmt);
        } catch (DataAccessException e) {
            System.out.println("Error: could not clear game table from chess database.");
        }

    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        String createStmt = String.format(
                "INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
                GAME_TABLE_NAME, WHITE_TEAM_COL, BLACK_TEAM_COL, GAME_NAME_COL, GAME_COL
        );
        String game = new Gson().toJson(new ChessGame());
        return ExecuteSQL.executeUpdate(createStmt, null, null, gameName, game);
    }

    // Use to update the white team with a new user
    @Override
    public void updateWhiteUsername(int gameID, String whiteUsername) throws DataAccessException {
        String updateStmt = String.format(
                "UPDATE %s SET %s=? WHERE %s=?", GAME_TABLE_NAME, WHITE_TEAM_COL, GAME_KEY_COL
        );
        ExecuteSQL.executeUpdate(updateStmt, whiteUsername, gameID);
    }

    // Use to update a new user for the black team
    @Override
    public void updateBlackUsername(int gameID, String blackUsername) throws DataAccessException {
        String updateStmt = String.format(
                "UPDATE %s SET %s=? WHERE %s=?", GAME_TABLE_NAME, BLACK_TEAM_COL, GAME_KEY_COL
        );
        ExecuteSQL.executeUpdate(updateStmt, blackUsername, gameID);
    }

    /**
     * Helper method for removing user from game upon Leave request.
     * @param gameID Which game they're exiting
     * @param color Which team to set vacant
     * @throws DataAccessException Thrown if there is a database accessing error
     */
    public void removePlayer(int gameID, ChessGame.TeamColor color) throws DataAccessException {
        String colorColumn = color.equals(ChessGame.TeamColor.WHITE) ? WHITE_TEAM_COL : BLACK_TEAM_COL;
        String removeStmt = String.format(
                "UPDATE %s SET %s=? WHERE %s=?", GAME_TABLE_NAME, colorColumn, GAME_KEY_COL
        );
        ExecuteSQL.executeUpdate(removeStmt, null, gameID);
    }

    /**
     * Helper method for removing user from game upon Leave request.
     * @param gameID ID of game to query
     * @param color Which team to find username from
     * @throws DataAccessException Thrown if there is a database accessing error
     */
    public String getUsername(int gameID, ChessGame.TeamColor color) throws DataAccessException {
        // Make query statement to get all entries
        String team = color.equals(ChessGame.TeamColor.WHITE) ? WHITE_TEAM_COL : BLACK_TEAM_COL;
        String queryStmt = String.format("SELECT %s FROM %s WHERE %d = ?", team, GAME_TABLE_NAME, gameID);
        try (PreparedStatement ps = conn.prepareStatement(queryStmt)) {
            ps.setInt(1, gameID);   // Plug in game ID
            // Executing the query and retrieving the result set
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Return white or black username string based on request. If null, return empty string
                    return Objects.requireNonNullElse(rs.getString(team), "");
                }
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("Error: Could not look for game!");
        }
        return null;
    }

    @Override
    public boolean hasGame(int gameID) throws DataAccessException {
        // Make query statement to count instances of key
        String queryStmt = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", GAME_TABLE_NAME, GAME_KEY_COL);
        try (PreparedStatement ps = conn.prepareStatement(queryStmt)) {
            ps.setInt(1, gameID);    // Replace '?' with key value

            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {  // Checking if the result set has any rows
                int count = resultSet.getInt(1);
                // If count is greater than 0, key exists; otherwise, it does not exist
                return count > 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Could not look for game id!");
        }
        return false;
    }

    @Override
    public ArrayList<GameData> getGamesList() throws DataAccessException {

        // Make query statement to get all entries
        ArrayList<GameData> games = new ArrayList<>();
        String queryStmt = String.format("SELECT * FROM %s", GAME_TABLE_NAME);
        try (PreparedStatement ps = conn.prepareStatement(queryStmt)) {

            // Executing the query and retrieving the result set
            ResultSet resultSet = ps.executeQuery();

            // Checking if the result set has any rows
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String whiteTeamName = resultSet.getString(2);
                String blackTeamName = resultSet.getString(3);
                String gameName = resultSet.getString(4);
                String gameStr = resultSet.getString(5);
                Gson gs = new Gson();
                ChessGame game = gs.fromJson(gameStr, ChessGame.class);
                games.add(new GameData(id, whiteTeamName, blackTeamName, gameName, game));
            }
            return games;
        } catch (SQLException e) {
            throw new DataAccessException("Error: Could not look for auth key!");
        }
    }

    /**
     *
     * @param gameID
     * @return
     * @throws SQLException
     */
    public ChessGame getChessGameFromDatabase(int gameID) throws DataAccessException {
        // Make query statement to get all entries
        String queryStmt = String.format("SELECT game FROM %s WHERE %s = ?", GAME_TABLE_NAME, GAME_KEY_COL);
        try (PreparedStatement ps = conn.prepareStatement(queryStmt)) {
            ps.setInt(1, gameID);   // Plug in game ID
            // Executing the query and retrieving the result set
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ChessGame game = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                    return game;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Could not look for game!");
        }
        return null;
    }

    /**
     * Attempts to update a chess game with a given move.
     * @param gameID ID of game to make move in
     * @param move Contains start and end positions of move
     * @throws InvalidMoveException Thrown if the move was invalid
     */
    public void updateGameMakeMove(int gameID, ChessMove move) throws InvalidMoveException, DataAccessException {
        ChessGame game = getChessGameFromDatabase(gameID);
        game.makeMove(move);        // Make the requested chess move
        updateGame(gameID, game);   // Update game in database
    }

    public void updateGame(int gameID, ChessGame game) throws DataAccessException {
        String gameJson = new Gson().toJson(game);  // Serialize the updated game object
        String updateStmt = String.format(
                "UPDATE %s SET %s=? WHERE %s = %d", GAME_TABLE_NAME, GAME_COL, GAME_KEY_COL, gameID
        );
        ExecuteSQL.executeUpdate(updateStmt, gameJson); // Execute SQL statement
    }

    @Override
    public boolean isEmpty() throws DataAccessException {
        // Make query statement to count entries
        String query = String.format("SELECT COUNT(*) FROM %s", GAME_TABLE_NAME);
        try (PreparedStatement ps = conn.prepareStatement(query)) {

            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {  // Checking if the result set has any rows
                int count = resultSet.getInt(1);
                // If count is 0, tables is empty
                return (count == 0);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Could not look for auth key!");
        }
        return false;
    }


    @Override
    public boolean hasAvailableBlackTeam(int gameID) throws DataAccessException {
        return hasAvailableTeam(gameID, BLACK_TEAM_COL);
    }

    @Override
    public boolean hasAvailableWhiteTeam(int gameID) throws DataAccessException {
        return hasAvailableTeam(gameID, WHITE_TEAM_COL);
    }

    public boolean hasAvailableTeam(int gameID, String team) throws DataAccessException {
        // Make query statement to verify if the requested team is available for game with given ID
        String queryStmt = String.format(
                "SELECT %s FROM %s WHERE %s = %d", team, GAME_TABLE_NAME, GAME_KEY_COL, gameID
        );
        try (PreparedStatement ps = conn.prepareStatement(queryStmt)) {

            ResultSet resultSet = ps.executeQuery();
            // Checking if the result set has null value in whiteUsername column
            if (resultSet.next()) {
                Object value = resultSet.getObject(1);
                // If value is null, team is vacant
                return (value == null);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Could not verify team availability!");
        }
        return false;
    }
}
