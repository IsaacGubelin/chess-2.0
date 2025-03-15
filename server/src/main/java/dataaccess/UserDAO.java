package dataaccess;

import model.UserData;

import java.sql.SQLException;
import exception.DataAccessException;

public interface UserDAO {

    void clearUserTable();

    void createUser(UserData userData) throws DataAccessException;

    UserData getUser(String username);

    void deleteUser(String username) throws DataAccessException;

    boolean hasThisUsername(String username);

    boolean isEmpty() throws DataAccessException;
}