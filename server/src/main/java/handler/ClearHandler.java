package handler;

import com.google.gson.Gson;
import dataaccess.*;
import model.MessageData;
import exception.ResponseException;
import service.ClearService;
import spark.Request;
import spark.Response;

public class ClearHandler {

    private ClearService clearService;

    public ClearHandler(GameDAO gDao, UserDAO uDao, AuthDAO aDao) {
        this.clearService = new ClearService(gDao, uDao, aDao);
    }

    /**
     * Clear all tables in the chess database
     * @param req HTTP request
     * @param res HTTP response
     * @return JSON response and status code
     */
    public Object handleClearDatabase(Request req, Response res) {

        try {
            clearService.clearDatabase();
        } catch (ResponseException e) {
            res.status(e.getStatusCode());
            return new Gson().toJson(new MessageData(e.getMessage()));
        }
        res.status(200); // Success
        return "{}";
    }

}
