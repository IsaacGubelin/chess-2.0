package service;
import dataaccess.*;
import exception.ResponseException;

public class ClearService {
    private GameDAO gameDao;
    private UserDAO userDao;
    private AuthDAO authDao;

    public ClearService(GameDAO gDao, UserDAO uDao, AuthDAO aDao) {
        this.gameDao = gDao;
        this.userDao = uDao;
        this.authDao = aDao;
    }

    public void clearDatabase() throws ResponseException {
        gameDao.clearGameTable();
        userDao.clearUserTable();
        authDao.clearAuthTable();
        if (!gameDao.isEmpty() || !userDao.isEmpty() || !authDao.isEmpty()) {
            throw new ResponseException(500, "Error: Could not clear database");
        }
    }
}
