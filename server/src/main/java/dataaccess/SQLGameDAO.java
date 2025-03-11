//package dataAccess;
//
//import chess.ChessGame;
//import chess.ChessMove;
//import chess.InvalidMoveException;
//import config.ConfigConsts;
//import exception.AlreadyTakenException;
//import exception.DataAccessException;
//import com.google.gson.Gson;
//import model.GameData;
//
//import java.util.ArrayList;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.Objects;
//
//
//public class SQLGameDAO implements GameDAO{
//
//    // For creating game table in chess database
//    private final String[] createGameTableStatements = {
//            """
//            CREATE TABLE IF NOT EXISTS  games
//            (
//              `gameID` int NOT NULL AUTO_INCREMENT,
//              `whiteUsername` varchar(256),
//              `blackUsername` varchar(256),
//              `gameName` varchar(256) NOT NULL,
//              `game` json NOT NULL,
//              PRIMARY KEY (`gameID`)
//            );
//            """
//    };
//
//    public SQLGameDAO() {
//        try {
//            DatabaseManager.createDatabase();
//        } catch (DataAccessException ex) {
//            System.out.println("Couldn't create database");
//        }
//        try (var conn = DatabaseManager.getConnection()) {
//            for (var statement : createGameTableStatements) {
//                try (var preparedStatement = conn.prepareStatement(statement)) {
//                    preparedStatement.executeUpdate();
//                }
//            }
//        } catch (SQLException | DataAccessException e) {
//            System.out.println("SQL Error: could not make games database.");
//        }
//    }
//    @Override
//    public void clearGamesDataBase() {
//
//        String clearStmt = "TRUNCATE TABLE games";
//        try {
//            ExecuteSQL.executeUpdate(clearStmt);
//        } catch (SQLException e) {
//            System.out.println("Error: could not clear game table from chess database.");
//        }
//
//    }
//
//    @Override
//    public int createGame(String gameName) throws SQLException {
//        String createStmt = "INSERT INTO games (whiteUsername, blackUsername, gameName, game)"
//                          + " VALUES (?, ?, ?, ?)";
//        String game = new Gson().toJson(new ChessGame());
//        int id = ExecuteSQL.executeUpdate(createStmt, null, null, gameName, game);
//        return id;
//    }
//
//    // Use to update the white team with a new user
//    @Override
//    public void updateWhiteUsername(int gameID, String whiteUsername) throws SQLException, AlreadyTakenException {
//        try {
//            if (hasAvailableTeam(gameID, ConfigConsts.WHITE_TEAM_COL)) {
//                String updateStmt = "UPDATE games SET whiteUsername=? WHERE gameID=?";
//                ExecuteSQL.executeUpdate(updateStmt, whiteUsername, gameID);
//            } else {
//                throw new AlreadyTakenException("Team already taken.");
//            }
//        } catch (DataAccessException ex) {
//            throw new SQLException("Could not update white username.");
//        }
//    }
//
//    // Use to update a new user for the black team
//    @Override
//    public void updateBlackUsername(int gameID, String blackUsername) throws SQLException, AlreadyTakenException {
//        try {
//            if (hasAvailableTeam(gameID, ConfigConsts.BLACK_TEAM_COL)) {
//                String updateStmt = "UPDATE games SET blackUsername=? WHERE gameID=?";
//                ExecuteSQL.executeUpdate(updateStmt, blackUsername, gameID);
//            } else {
//                throw new AlreadyTakenException("Team already taken.");
//            }
//        } catch (DataAccessException ex) {
//            throw new SQLException("Could not update black username.");
//        }
//    }
//
//    /**
//     * Helper method for removing user from game upon Leave request.
//     * @param gameID Which game they're exiting
//     * @param color Which team to set vacant
//     * @throws SQLException Thrown if there is a database accessing error
//     */
//    public void removePlayer(int gameID, ChessGame.TeamColor color) throws SQLException {
//        String colorColumn = color.equals(ChessGame.TeamColor.WHITE) ? "whiteUsername" : "blackUsername";
//        String removeStmt = "UPDATE games SET " + colorColumn + "=? WHERE gameID=?";
//        ExecuteSQL.executeUpdate(removeStmt, null, gameID);
//    }
//
//    /**
//     *
//     * @param gameID select requested game
//     * @param color controls the query statement to get whiteUsername or blackUsername
//     * @return
//     * @throws SQLException thrown if there's an issue with retrieving the names from the database.
//     */
//    public String getUsername(int gameID, ChessGame.TeamColor color) throws SQLException {
//        try (var conn = DatabaseManager.getConnection()) {
//            // Make query statement to get all entries
//            String colLabel = color.equals(ChessGame.TeamColor.WHITE) ? "whiteUsername" : "blackUsername";
//            String queryStmt = "SELECT " + colLabel + " FROM " + ConfigConsts.GAME_TABLE_NAME + " WHERE gameID = ?";
//            try (var ps = conn.prepareStatement(queryStmt)) {
//                ps.setInt(1, gameID);   // Plug in game ID
//                // Executing the query and retrieving the result set
//                try (var rs = ps.executeQuery()) {
//                    if (rs.next()) {
//                        // Return white or black username string based on request. If null, return empty string
//                        return Objects.requireNonNullElse(rs.getString(colLabel), "");
//                    }
//                }
//            }
//        } catch (DataAccessException | SQLException e) {
//            throw new SQLException("Could not look for game!");
//        }
//        return null;
//    }
//
//    @Override
//    public boolean hasGame(int gameID) {
//        try (var conn = DatabaseManager.getConnection()) {
//            // Make query statement to count instances of key
//            String queryStmt = "SELECT COUNT(*) FROM " + ConfigConsts.GAME_TABLE_NAME + " WHERE "
//                    + ConfigConsts.GAME_TABLE_KEY_COL + " = ?";
//            try (var ps = conn.prepareStatement(queryStmt)) {
//                ps.setInt(1, gameID);    // Replace '?' with key value
//
//                // Executing the query and retrieving the result set
//                ResultSet resultSet = ps.executeQuery();
//
//                // Checking if the result set has any rows
//                if (resultSet.next()) {
//                    int count = resultSet.getInt(1);
//                    // If count is greater than 0, key exists; otherwise, it does not exist
//                    return count > 0;
//                }
//            }
//        } catch (DataAccessException | SQLException e) {
//            System.out.println("Could not look for game id!");
//        }
//        return false;
//    }
//
//    @Override
//    public ArrayList<GameData> getGamesList() throws SQLException {
//
//        try (var conn = DatabaseManager.getConnection()) {
//            // Make query statement to get all entries
//            ArrayList<GameData> games = new ArrayList<>();
//            String queryStmt = "SELECT * FROM " + ConfigConsts.GAME_TABLE_NAME;
//            try (var ps = conn.prepareStatement(queryStmt)) {
//
//                // Executing the query and retrieving the result set
//                ResultSet resultSet = ps.executeQuery();
//
//                // Checking if the result set has any rows
//                while (resultSet.next()) {
//                    int id = resultSet.getInt(1);
//                    String whiteTeamName = resultSet.getString(2);
//                    String blackTeamName = resultSet.getString(3);
//                    String gameName = resultSet.getString(4);
//                    String gameStr = resultSet.getString(5);
//                    Gson gs = new Gson();
//                    ChessGame game = gs.fromJson(gameStr, ChessGame.class);
//                    games.add(new GameData(id, whiteTeamName, blackTeamName, gameName, game));
//                }
//                return games;
//            }
//        } catch (DataAccessException | SQLException e) {
//            throw new SQLException("Could not look for auth key!");
//        }
//    }
//
//    /**
//     *
//     * @param gameID
//     * @return
//     * @throws SQLException
//     */
//    public ChessGame getChessGameFromDatabase(int gameID) throws SQLException {
//        try (var conn = DatabaseManager.getConnection()) {
//            // Make query statement to get all entries
//            String queryStmt = "SELECT game FROM " + ConfigConsts.GAME_TABLE_NAME + " WHERE gameID = ?";
//            try (var ps = conn.prepareStatement(queryStmt)) {
//                ps.setInt(1, gameID);   // Plug in game ID
//                // Executing the query and retrieving the result set
//                try (var rs = ps.executeQuery()) {
//                    if (rs.next()) {
//                        ChessGame game = new Gson().fromJson(rs.getString("game"), ChessGame.class);
//                        return game;
//                    }
//                }
//            }
//        } catch (DataAccessException | SQLException e) {
//            throw new SQLException("Could not look for game!");
//        }
//        return null;
//    }
//
//    /**
//     * Attempts to update a chess game with a given move.
//     * @param gameID ID of game to make move in
//     * @param move Contains start and end positions of move
//     * @throws InvalidMoveException Thrown if the move was invalid
//     */
//    public void updateGameMakeMove(int gameID, ChessMove move) throws InvalidMoveException, SQLException {
//        ChessGame game = getChessGameFromDatabase(gameID);
//        game.makeMove(move);        // Make the requested chess move
//        updateGame(gameID, game);   // Update game in database
//    }
//
//    public void updateGame(int gameID, ChessGame game) throws SQLException {
//        String gameJson = new Gson().toJson(game);  // Serialize the updated game object
//        String updateStmt = "UPDATE " + ConfigConsts.GAME_TABLE_NAME + " SET game=? WHERE " + "gameID = " + gameID;
//        ExecuteSQL.executeUpdate(updateStmt, gameJson); // Execute SQL statement
//    }
//
//    @Override
//    public boolean isEmpty() throws DataAccessException {
//        try (var conn = DatabaseManager.getConnection()) {
//            // Make query statement to count entries
//            String queryStmt = "SELECT COUNT(*) FROM " + ConfigConsts.GAME_TABLE_NAME;
//            try (var ps = conn.prepareStatement(queryStmt)) {
//
//                // Executing the query and retrieving the result set
//                ResultSet resultSet = ps.executeQuery();
//
//                // Checking if the result set has any rows
//                if (resultSet.next()) {
//                    int count = resultSet.getInt(1);
//                    // If count is 0, tables is empty
//                    return (count == 0);
//                }
//            }
//        } catch (DataAccessException | SQLException e) {
//            throw new DataAccessException("Could not look for auth key!");
//        }
//        return false;
//    }
//
//    @Override
//    public boolean hasAvailableTeam(int gameID, String team) throws DataAccessException {
//        try (var conn = DatabaseManager.getConnection()) {
//            // Make query statement to verify if the requested team is available for game with given ID
//            String queryStmt = "SELECT " + team + " FROM " + ConfigConsts.GAME_TABLE_NAME + " WHERE gameID = " + gameID;
//            try (var ps = conn.prepareStatement(queryStmt)) {
//
//                // Executing the query and retrieving the result set
//                ResultSet resultSet = ps.executeQuery();
//
//                // Checking if the result set has null value in whiteUsername column
//                if (resultSet.next()) {
//                    Object value = resultSet.getObject(1);
//                    // If value is null, team is vacant
//                    return (value == null);
//                }
//            }
//        } catch (DataAccessException | SQLException e) {
//            throw new DataAccessException("Could not verify team availability!");
//        }
//        return false;
//    }
//
//}
