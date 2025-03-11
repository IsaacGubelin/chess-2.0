//package dataaccess;
//
//import exception.DataAccessException;
//import model.AuthData;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.UUID;
//
//public class SQLAuthDAO implements AuthDAO {
//
//    private final String[] createAuthTableStatements = {
//            """
//            CREATE TABLE IF NOT EXISTS  auths
//            (
//              `authToken` varchar(256) NOT NULL,
//              `username` varchar(256) NOT NULL,
//              PRIMARY KEY (`authToken`)
//            );
//            """
//    };
//
//    // Constructor for AuthDAO
//    public SQLAuthDAO() {
//
//        try {
//            DatabaseManager.createDatabase();
//        } catch (DataAccessException ex) {
//            System.out.println("Couldn't create database");
//        }
//
//        try (var conn = DatabaseManager.getConnection()) {
//            for (var statement : createAuthTableStatements) {
//                try (var preparedStatement = conn.prepareStatement(statement)) {
//                    preparedStatement.executeUpdate();
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        } catch (exception.DataAccessException e) {
//            System.out.println("Error: could not create auths database.");
//        }
//    }
//
//    // Generate a unique authToken
//    private String generateToken() {
//        String token = UUID.randomUUID().toString();    // Make an authToken
//        return token;
//    }
//
//    @Override
//    public void clearAuthDatabase() {
//        String clearStmt = "TRUNCATE TABLE auths";
//        try {
//            ExecuteSQL.executeUpdate(clearStmt);
//        } catch (Exception e) {
//            System.out.println("Error: could not clear auth table from chess database.");
//        }
//    }
//
//    @Override
//    public String createAuth(String username) throws SQLException {
//        String token = generateToken();
//        String createStmt = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
//        ExecuteSQL.executeUpdate(createStmt, token, username); // Function replaces '?' with parameters
//
//        return token;
//    }
//
//    @Override
//    public AuthData getAuth(String authToken) throws exception.DataAccessException {
//
//        try (var conn = DatabaseManager.getConnection()) {
//            var statement = "SELECT authToken, username FROM auths WHERE authToken=?";
//            try (var ps = conn.prepareStatement(statement)) {
//                ps.setString(1, authToken);
//                try (var rs = ps.executeQuery()) {
//                    if (rs.next()) {
//                        return readAuth(rs);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("Error: could not retrieve auth.");
//        }
//        return null;
//    }
//
//    @Override
//    public void deleteAuth(String authToken) {
//        String deleteStmt = "DELETE FROM auths WHERE authToken=?";
//        try {
//            ExecuteSQL.executeUpdate(deleteStmt, authToken);
//        } catch (Exception e) {
//            System.out.println("Error: could not delete authToken.");
//        }
//    }
//
//    @Override
//    public boolean hasAuth(String authToken) {
//        try (var conn = DatabaseManager.getConnection()) {
//            // Make query statement to count instances of key
//            String queryStmt = "SELECT COUNT(*) FROM " + ConfigConsts.AUTH_TABLE_NAME + " WHERE "
//                    + ConfigConsts.AUTH_TABLE_KEY_COL + " = ?";
//            try (var ps = conn.prepareStatement(queryStmt)) {
//                ps.setString(1, authToken);    // Replace '?' with key value
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
//            System.out.println("Could not look for auth key!");
//        }
//        return false;
//    }
//
//    @Override
//    public boolean isEmpty() throws DataAccessException {
//        try (var conn = DatabaseManager.getConnection()) {
//            // Make query statement to count entries
//            String authQueryStmt = "SELECT COUNT(*) FROM " + ConfigConsts.AUTH_TABLE_NAME;
//            try (var ps = conn.prepareStatement(authQueryStmt)) {
//
//                // Executing the query and retrieving the result set
//                ResultSet rSet = ps.executeQuery();
//
//                // Checking if the result set has any rows
//                if (rSet.next()) {
//                    int cnt = rSet.getInt(1);
//                    // If count is 0, tables is empty
//                    return (cnt == 0);
//                }
//            }
//        } catch (DataAccessException | SQLException e) {
//            throw new DataAccessException("Could not look for auth key!");
//        }
//        return false;
//    }
//
//
//    // Read output from SQL query for auth data
//    private AuthData readAuth(ResultSet rs) throws SQLException {
//        String name = rs.getString("username");
//        String token = rs.getString("authToken");
//        return new AuthData(token, name);
//    }
//}
