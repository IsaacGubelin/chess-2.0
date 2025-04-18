package dataaccess;

import exception.DataAccessException;
import exception.ResponseException;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {

    private Connection conn;
    private final String AUTH_TABLE_NAME = "auths";
    private final String AUTH_KEY_COL = "authToken";
    private final String AUTH_USER_COL = "username";

    private final String[] createAuthTableStatements = {
            String.format("""
            CREATE TABLE IF NOT EXISTS %s
            (
              `%s` varchar(256) NOT NULL,
              `%s` varchar(256) NOT NULL,
              PRIMARY KEY (`%s`)
            );
            """, AUTH_TABLE_NAME, AUTH_KEY_COL, AUTH_USER_COL, AUTH_KEY_COL)
    };

    // Constructor for AuthDAO
    public SQLAuthDAO() {
        try {
            DatabaseManager.createDatabase();
            conn = DatabaseManager.getConnection();
            for (String statement : createAuthTableStatements) {
                PreparedStatement ps = conn.prepareStatement(statement);
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException ex) {
            System.out.println("Couldn't create database");
        }
    }

    // Generate a unique authToken
    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void clearAuthTable() {
        String clearStmt = String.format("TRUNCATE TABLE %s", AUTH_TABLE_NAME);
        try {
            ExecuteSQL.executeUpdate(clearStmt);
        } catch (Exception e) {
            System.out.println("Error: could not clear auth table from chess database.");
        }
    }

    @Override
    public String createAuth(String username) throws DataAccessException {
        String token = generateToken();
        String createStmt = String.format(
                "INSERT INTO %s (%s, %s) VALUES (?, ?)", AUTH_TABLE_NAME, AUTH_KEY_COL, AUTH_USER_COL
        );
        ExecuteSQL.executeUpdate(createStmt, token, username); // Function replaces '?' with parameters
        return token;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {

        String query = String.format(
                "SELECT %s, %s FROM %s WHERE %s=?", AUTH_KEY_COL, AUTH_USER_COL, AUTH_TABLE_NAME, AUTH_KEY_COL
        );
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, authToken);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {  // If the SQL query returns a result
                return readAuth(rs);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: could not retrieve auth.");
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {
        String deleteStmt = String.format("DELETE FROM %s WHERE %s=?", AUTH_TABLE_NAME, AUTH_KEY_COL);
        try {
            ExecuteSQL.executeUpdate(deleteStmt, authToken);
        } catch (DataAccessException e) {
            System.out.println("Error: could not delete authToken.");
        }
    }

    @Override
    public boolean hasAuth(String authToken) throws DataAccessException {
        // Make query statement to count instances of key
        String query = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", AUTH_TABLE_NAME, AUTH_KEY_COL);
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, authToken);    // Replace '?' with key value

            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                // If count is greater than 0, key exists; otherwise, it does not exist
                return count > 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: could not find auth key!");
        }
        return false;
    }

    @Override
    public boolean isEmpty() throws DataAccessException {
        // Make query statement to count entries
        String query = String.format("SELECT COUNT(*) FROM %s", AUTH_TABLE_NAME);
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            // Executing the query and retrieving the result set
            ResultSet rSet = ps.executeQuery();

            // Checking if the result set has any rows
            if (rSet.next()) {
                int cnt = rSet.getInt(1);
                return (cnt == 0);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Could not look for auth key!");
        }
        return false;
    }


    // Read output from SQL query for auth data
    private AuthData readAuth(ResultSet rs) throws SQLException {
        String token = rs.getString(AUTH_KEY_COL);
        String name = rs.getString(AUTH_USER_COL);
        return new AuthData(token, name);
    }
}
