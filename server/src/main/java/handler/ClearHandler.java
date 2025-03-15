package handler;

import com.google.gson.Gson;
import dataaccess.*;
import exception.DataAccessException;
import model.MessageData;
import exception.ResponseException;
import service.ClearService;
import spark.Response;

public class ClearHandler {

    private final ClearService clearService;

    public ClearHandler(GameDAO gDao, UserDAO uDao, AuthDAO aDao) {
        this.clearService = new ClearService(gDao, uDao, aDao);
    }

    /**
     * Clear all tables in the chess database. Does not use data from HTTP request body.
     * @param res HTTP response to report success or failure
     * @return JSON response and status code
     */
    public Object handleClearDatabase(Response res) {

        try {
            clearService.clearDatabase();
            res.status(200); // Success
            return "{}";
        } catch (ResponseException e) {
            res.status(e.getStatusCode());
            return new Gson().toJson(new MessageData(e.getMessage()));
        } catch (DataAccessException e) {
            res.status(500);
            return new Gson().toJson(new MessageData(e.getMessage()));
        }

    }

}
