package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {

    public HashMap<String, AuthData> authDataTable = new HashMap<>();

    // Generate a unique authToken
    private String generateToken() {
        String token = UUID.randomUUID().toString();    // Make an authToken
        while (authDataTable.containsKey(token))
            token = UUID.randomUUID().toString();       // If the token already exists, make a new one
        return token;
    }


    /**
     *
     */
    @Override
    public void clearAuthTable() {
        authDataTable.clear();
    }

    /**
     * Creates a new authToken and adds it to the database
     * @param username used to serve as key to AuthData
     * @return auth token
     */
    @Override
    public String createAuth(String username) {
        String token = generateToken();
        authDataTable.put(token, new AuthData(token, username));
        return token;
    }

    // Check if the database contains an authToken
    @Override
    public boolean hasAuth(String authToken) {
        return authDataTable.containsKey(authToken);
    }

    // Retrieve auth data from a given authToken
    @Override
    public AuthData getAuth(String authToken) {
        if (!authDataTable.containsKey(authToken)) {
            return null;
        }
        return authDataTable.get(authToken);
    }


    // Remove an authData object from the database, given the corresponding token
    @Override
    public void deleteAuth(String authToken) {
        authDataTable.remove(authToken);
    }

    @Override
    public boolean isEmpty() {
        return authDataTable.isEmpty();
    }



}