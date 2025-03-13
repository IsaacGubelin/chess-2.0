package handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import exception.DataAccessException;
import exception.ResponseException;
import model.MessageData;
import service.LogoutService;
import spark.Request;
import spark.Response;

import javax.xml.crypto.Data;

public class LogoutHandler {

    private final LogoutService logoutService;
    private final String AUTH_HEADER = "authorization";


    public LogoutHandler(AuthDAO aDao) {
        this.logoutService = new LogoutService(aDao);
    }

    public Object handleLogout(Request req, Response res) {
        String authToken = req.headers(AUTH_HEADER);
        try {
            logoutService.logout(authToken);
            res.status(200);
            return "{}";
        } catch (ResponseException e) {
            res.status(e.getStatusCode());
            return new Gson().toJson(new MessageData(e.getMessage()));
        } catch (DataAccessException e) {
            res.status(500);
            return new Gson().toJson(new MessageData("Error: SQL malfunction."));
        }
    }
}
