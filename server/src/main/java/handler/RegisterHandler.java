package handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.MessageData;
import model.UserData;
import service.RegisterService;
import spark.Request;
import spark.Response;

public class RegisterHandler {

    private RegisterService registerService;

    public RegisterHandler(UserDAO uDao, AuthDAO aDao) {
        this.registerService = new RegisterService(uDao, aDao);
    }

    /**
     * The handler for registering a new user. Deserializes the request body and calls RegisterService.
     * @param req HTTP request data
     * @param res Has response body and status code
     * @return Response body in JSON format
     */
    public Object handleRegister(Request req, Response res) {

        UserData user = new Gson().fromJson(req.body(), UserData.class); // Convert Json to user data

        try {
            String authToken = registerService.register(user); // Get authToken
            res.status(200);
            return new Gson().toJson(new AuthData(authToken, user.username()));
        } catch (ResponseException e) {
            res.status(e.getStatusCode());      // Set status value to error code
            return new Gson().toJson(new MessageData(e.getMessage()));    // Return error message
        }
    }
}
