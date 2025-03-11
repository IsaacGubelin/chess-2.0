package dataaccess;

import model.AuthData;
import exception.DataAccessException;

public interface AuthDAO {



    void clearAuthTable();
    String createAuth(String username);

    AuthData getAuth(String authToken) throws DataAccessException;

    boolean hasAuth(String authToken);

    void deleteAuth(String authToken) throws DataAccessException;

    boolean isEmpty();
}