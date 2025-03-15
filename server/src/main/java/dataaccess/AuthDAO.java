package dataaccess;

import model.AuthData;
import exception.DataAccessException;

public interface AuthDAO {

    void clearAuthTable();
    String createAuth(String username) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    boolean hasAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    boolean isEmpty() throws DataAccessException;
}