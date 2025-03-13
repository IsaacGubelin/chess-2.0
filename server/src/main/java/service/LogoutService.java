package service;
import dataaccess.AuthDAO;
import exception.DataAccessException;
import exception.ResponseException;
import model.AuthData;

public class LogoutService {

    private final AuthDAO authDAO;

    public LogoutService(AuthDAO aDao) {
        this.authDAO = aDao;
    }

    /**
     * Log out a user by deleting their auth token from the database.
     * @param token Auth token to delete
     * @throws ResponseException if the user is not logged in
     * @throws DataAccessException if there is an error accessing the database
     */
    public void logout(String token) throws ResponseException, DataAccessException {
        AuthData authData = authDAO.getAuth(token);
        if (authData == null) {
            throw new ResponseException(401, "Error: User not logged in.");
        }
        authDAO.deleteAuth(token);
    }
}
