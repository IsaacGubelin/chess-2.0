package service;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import exception.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;


public class LoginService {
    private final UserDAO userDao;
    private final AuthDAO authDao;

    public LoginService(UserDAO uDao, AuthDAO aDao) {
        this.userDao = uDao;
        this.authDao = aDao;
    }

    /**
     * Check if the password is correct for the user.
     * @param user user data to check password
     * @throws ResponseException if the password is incorrect
     */
    private void checkPassword(UserData user) throws ResponseException {
        String hashedPassword = userDao.getUser(user.username()).password();
        if (!BCrypt.checkpw(user.password(), hashedPassword)) {
            throw new ResponseException(401, "Error: Incorrect password.");
        }
    }

    public AuthData login(UserData user) throws ResponseException, DataAccessException {

        if (user.username() == null || user.password() == null) { // Check for bad request
            throw new ResponseException(400, "Error: Missing data field(s) in login.");
        } else if (!userDao.hasThisUsername(user.username())) { // Check that authToken exists
            throw new ResponseException(401, "Error: User not registered in database.");
        }

        checkPassword(user);
        String token = authDao.createAuth(user.username());

        return new AuthData(token, user.username());
    }
}
