package facade;

import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class HTTPFacade {

    private final String url;

    private static final String REQ_HEADER_AUTHORIZATION = "authorization";    // Used as key for HTTP request headers

    public HTTPFacade(String url) {
        this.url = url;
    }

    public AuthData register(UserData userData) throws ResponseException {
        String path = "/user";  // HTTP path
        return this.makeRequest("POST", path, userData, AuthData.class);
    }





    public AuthData login(UserData userData) throws ResponseException {
        String path = "/session";
        return this.makeRequest("POST", path, userData, AuthData.class);
    }

    public void logout(String authToken) throws ResponseException {
        String path = "/session";
        String method = "DELETE";
        try {
            URL serverUrl = (new URI(url + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) serverUrl.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);                     // Indicate that the connection will output data

            http.addRequestProperty(REQ_HEADER_AUTHORIZATION, authToken);
            http.connect();                             // Connect to server
            throwIfNotSuccessful(http);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public GameData createGame(String authToken, String gameName) throws ResponseException {
        String path = "/game";
        String method = "POST";
        CreateGameRequestData newGameData = new CreateGameRequestData(gameName);

        try {
            URL serverUrl = (new URI(url + path).toURL());
            HttpURLConnection http = (HttpURLConnection) serverUrl.openConnection();
            http.setRequestMethod(method);  // Set the request method (GET, DELETE, POST, etc)
            http.setDoOutput(true);         // Indicate that the connection will output data

            http.addRequestProperty(REQ_HEADER_AUTHORIZATION, authToken);  // Add auth token to http header
            writeBody(newGameData, http);

            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, GameData.class);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    /**
     * A function called by the chess client to join an available game.
     * @param gameReqData
     * @param authToken
     */
    public void joinGame(String authToken, JoinGameRequest gameReqData) throws ResponseException {
        String path = "/game";
        String method = "PUT";
        try {
            URL serverUrl = (new URI(url + path).toURL());
            HttpURLConnection http = (HttpURLConnection) serverUrl.openConnection();
            http.setRequestMethod(method);              // Set the request method (GET, DELETE, POST, etc)
            http.setDoOutput(true);                     // Indicate that the connection will output data

            http.addRequestProperty(REQ_HEADER_AUTHORIZATION, authToken);   // Add auth token to http header
            writeBody(gameReqData, http);       // Prepare http body using game request data
            http.connect();                     // Make connection
            throwIfNotSuccessful(http);

        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public ListGamesResponseData getGamesList(String authToken) throws ResponseException {
        String path = "/game";
        String method = "GET";
        try {
            URL serverUrl = (new URI(url + path).toURL());
            HttpURLConnection http = (HttpURLConnection) serverUrl.openConnection();
            http.setRequestMethod(method);              // Set the request method (GET, DELETE, POST, etc)
            http.setDoOutput(true);                     // Indicate that the connection will output data

            http.addRequestProperty(REQ_HEADER_AUTHORIZATION, authToken);   // Add auth token to http header
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, ListGamesResponseData.class);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL serverUrl = (new URI(url + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) serverUrl.openConnection();
            http.setRequestMethod(method);              // Set the request method (GET, DELETE, POST, etc)
            http.setDoOutput(true);                     // Indicate that the connection will output data

            writeBody(request, http);
            http.connect();                             // Connect to server
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    public static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    // Reads the response body from the input stream of the HTTP connection.
    public static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {  // If content length is negative, there is content to read
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);  // Deserialize
                }
            }
        }
        return response;
    }

    public void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    // Returns true if status code is 200-299 (good)
    public boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}
