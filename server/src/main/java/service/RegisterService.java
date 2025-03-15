package service;


import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import exception.DataAccessException;
import exception.ResponseException;
import model.UserData;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;

public class RegisterService {

    private final UserDAO userDao;
    private final AuthDAO authDao;

    public RegisterService(UserDAO uDao, AuthDAO aDao) {
        this.userDao = uDao;
        this.authDao = aDao;
    }

    /**
     * Hash the user's password and return the user data with the hashed password.
     * @param user user data to hash
     * @return user data with hashed password
     */
    private UserData hashUserPassword(UserData user) {
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        return new UserData(user.username(), hashedPassword, user.email());
    }

    /**
     * Register a new user in the database.
     * @param user user data to register
     * @return authToken for the new user
     * @throws ResponseException if the user data is invalid or the user already exists
     */
    public String register(model.UserData user)
            throws ResponseException {

        // Check for bad request (if any field is null)
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new ResponseException(400, "Error: missing info field in user data.");
        }


        // Check that username is valid
        if (userDao.hasThisUsername(user.username())) {
            throw new ResponseException(403, "Error: Username already taken.");
        }
        // Create and add user to database
        try {
            UserData hashedUser = hashUserPassword(user);
            userDao.createUser(hashedUser);
            // Add new auth to auth database and get a new authToken
            return authDao.createAuth(user.username());

        } catch (DataAccessException ex) {
            throw new ResponseException(500, "Error: Could not create user in SQL.");
        }


    }

}