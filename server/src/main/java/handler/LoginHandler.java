package handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import exception.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import model.MessageData;
import model.UserData;
import service.LoginService;
import spark.Request;
import spark.Response;

public class LoginHandler {

    private final LoginService loginService;

    public LoginHandler(UserDAO uDao, AuthDAO aDao) {
        this.loginService = new LoginService(uDao, aDao);
    }

    /**
     * Handle login request.
     * @param req HTTP request with user data
     * @param res HTTP response to report success or failure
     * @return JSON response and status code
     */
    public Object handleLogin(Request req, Response res) {
        UserData user = new Gson().fromJson(req.body(), UserData.class);    // Convert Json to user data

        try {
            AuthData auth = loginService.login(user);
            res.status(200);    // Success code
            return new Gson().toJson(auth); // Make auth data response body
        } catch (ResponseException e) {
            res.status(e.getStatusCode());
            return new Gson().toJson(new MessageData(e.getMessage()));
        } catch (DataAccessException e) {
            res.status(500);
            return new Gson().toJson(new MessageData("Error: SQL Database issue"));
        }
    }

}
