//package dataAccess;
//
//import exception.DataAccessException;
//import model.UserData;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class SQLUserDAO implements UserDAO{
//
//    /**
//     * SQL statements for generating new table called "users"
//     */
//    private final String[] createUserTableStatements = {
//            """
//            CREATE TABLE IF NOT EXISTS  users
//            (
//              `username` varchar(256) NOT NULL,
//              `password` varchar(512) NOT NULL,
//              `email` varchar(256) NOT NULL,
//              PRIMARY KEY (`username`)
//            );
//            """
//    };
//
//    public SQLUserDAO() {
//        // If database isn't built, create one.
//        try {
//            DatabaseManager.createDatabase();
//        } catch (DataAccessException ex) {
//            System.out.println("Couldn't create database");
//        }
//
//        try (var conn = DatabaseManager.getConnection()) {
//            for (var statement : createUserTableStatements) {
//                try (var preparedStatement = conn.prepareStatement(statement)) {
//                    preparedStatement.executeUpdate();
//                }
//            }
//        } catch (SQLException | exception.DataAccessException e) {
//            System.out.println("Error: could not create users database.");
//        }
//    }
//
//    // Evaluates if a given username exists
//    @Override
//    public boolean hasThisUsername(String username) {
//        try (var conn = DatabaseManager.getConnection()) {
//            // Make query statement to count instances of key
//            String queryStmt = "SELECT COUNT(*) FROM " + ConfigConsts.USER_TABLE_NAME + " WHERE "
//                    + ConfigConsts.USER_TABLE_KEY_COL + " = ?";
//            try (var ps = conn.prepareStatement(queryStmt)) {
//                ps.setString(1, username);    // Replace '?' with key value
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
//            System.out.println("Could not look for user key!");
//        }
//        return false;
//    }
//
//    @Override
//    public void clearUserDatabase() {
//        String clearStmt = "TRUNCATE TABLE users";
//        try {
//            ExecuteSQL.executeUpdate(clearStmt);
//        } catch (Exception e) {
//            System.out.println("Error: could not clear user table from chess database.");
//        }
//    }
//
//    @Override
//    public void createUser(UserData userData) throws SQLException {
//        // Hash password
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        String hashedPassword = encoder.encode(userData.password());
//
//        String createStmt = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
//        ExecuteSQL.executeUpdate(createStmt, userData.username(), hashedPassword, userData.email());
//    }
//
//
//
//    @Override
//    public UserData getUser(String username) {
//        try (var conn = DatabaseManager.getConnection()) {
//            var statement = "SELECT username, password, email FROM users WHERE username=?";
//            try (var ps = conn.prepareStatement(statement)) {
//                ps.setString(1, username);
//                try (var rs = ps.executeQuery()) {
//                    if (rs.next()) {
//                        return readUser(rs);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("Error: could not retrieve auth.");
//        }
//        return null;
//    }
//
//    // Read output from SQL query for user data
//    private UserData readUser(ResultSet rs) throws SQLException {
//        String name = rs.getString("username");
//        String password = rs.getString("password");
//        String email = rs.getString("email");
//
//        return new UserData(name, password, email);
//    }
//
//    @Override
//    public void deleteUser(String username) {
//        String deleteStmt = "DELETE FROM " + ConfigConsts.USER_TABLE_NAME + " WHERE username=?";
//        try {
//            ExecuteSQL.executeUpdate(deleteStmt, username);
//        } catch (Exception e) {
//            System.out.println("Error: could not delete user.");
//        }
//    }
//}
