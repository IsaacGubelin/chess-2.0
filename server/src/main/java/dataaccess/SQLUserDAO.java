package dataaccess;

import exception.DataAccessException;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {

    private Connection conn;
    private final String USER_TABLE_NAME = "users";
    private final String USER_KEY_COL = "username";
    private final String USER_PASS_COL = "password";
    private final String USER_EMAIL_COL = "email";


    /**
     * SQL statements for generating new table called "users"
     */
    private final String[] createUserTableStatements = {
            String.format("""
            CREATE TABLE IF NOT EXISTS %s
            (
              `%s` varchar(256) NOT NULL,
              `%s` varchar(512) NOT NULL,
              `%s` varchar(256) NOT NULL,
              PRIMARY KEY (`%s`)
            );
            """, USER_TABLE_NAME, USER_KEY_COL, USER_PASS_COL, USER_EMAIL_COL, USER_KEY_COL)
    };

    public SQLUserDAO() {
        try { // Create database if it doesn't exist
            DatabaseManager.createDatabase();
            this.conn = DatabaseManager.getConnection();

            for (String statement : createUserTableStatements) {
                PreparedStatement ps = conn.prepareStatement(statement);
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException ex) {
            System.out.println("Error: Couldn't create database");
        }
    }

    /**
     * Check if a username exists in the database
     * @param username string to verify
     * @return true if name exists
     */
    @Override
    public boolean hasThisUsername(String username) {
        // Make query statement to count instances of key
        String queryStmt = "SELECT COUNT(*) FROM " + USER_TABLE_NAME + " WHERE "
                + USER_KEY_COL + " = ?";
        try (PreparedStatement ps = conn.prepareStatement(queryStmt)) {
            ps.setString(1, username);  // Replace '?' with key value
            ResultSet resultSet = ps.executeQuery();

            // Checking if the result set has any rows
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0; // If count is greater than 0, key exists
            }
        }
        catch (SQLException e) {
            System.out.println("Could not look for user key!");
        }
        return false;
    }

    @Override
    public void clearUserTable() {
        String clearStmt = String.format("TRUNCATE TABLE %s", USER_TABLE_NAME);
        try {
            ExecuteSQL.executeUpdate(clearStmt);
        } catch (Exception e) {
            System.out.println("Error: could not clear user table from chess database.");
        }
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {

        String createStmt = String.format(
                "INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)",
                USER_TABLE_NAME, USER_KEY_COL, USER_PASS_COL, USER_EMAIL_COL
        );
        ExecuteSQL.executeUpdate(createStmt, userData.username(), userData.password(), userData.email());
    }


    @Override
    public UserData getUser(String username) {
            String statement = String.format(
                    "SELECT %s, %s, %s FROM %s WHERE %s=?",
                    USER_KEY_COL, USER_PASS_COL, USER_EMAIL_COL, USER_TABLE_NAME, USER_KEY_COL
            );
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        catch (SQLException e) {
            System.out.println("Error: could not retrieve auth.");
        }
        return null;
    }

    /**
     * Helper method to read user data from a result set
     * @param rs SQL ResultSet object
     * @return UserData object
     * @throws SQLException if the result set cannot be read
     */
    private UserData readUser(ResultSet rs) throws SQLException {
        String name = rs.getString(USER_KEY_COL);
        String password = rs.getString(USER_PASS_COL);
        String email = rs.getString(USER_EMAIL_COL);

        return new UserData(name, password, email);
    }

    @Override
    public void deleteUser(String username) {
        String deleteStmt = String.format("DELETE FROM %s WHERE %s=?", USER_TABLE_NAME, USER_KEY_COL);
        try {
            ExecuteSQL.executeUpdate(deleteStmt, username);
        } catch (Exception e) {
            System.out.println("Error: could not delete user.");
        }
    }

    @Override
    public boolean isEmpty() throws DataAccessException {
        String authQueryStmt = String.format("SELECT COUNT(*) FROM %s", USER_TABLE_NAME);
        try (PreparedStatement ps = conn.prepareStatement(authQueryStmt)) {

            ResultSet rSet = ps.executeQuery();
            if (rSet.next()) {
                int cnt = rSet.getInt(1);
                return (cnt == 0);  // Check if result set has no rows
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("Could not look for auth key!");
        }
        return false;
    }
}
