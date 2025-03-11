package service;


import dataaccess.*;
import exception.AlreadyTakenException;
import exception.ResponseException;
import exception.DataAccessException;

import java.sql.SQLException;

public class RegisterService {

    private UserDAO userDao;
    private AuthDAO authDao;

    public RegisterService(UserDAO uDao, AuthDAO aDao) {
        this.userDao = uDao;
        this.authDao = aDao;
    }
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
            userDao.createUser(user);
            // Add new auth to auth database and get a new authToken
            String authToken = authDao.createAuth(user.username());
            return authToken;

        } catch (SQLException ex) {
            throw new ResponseException(500, "Error: Could not create user in SQL.");
        }


    }

}