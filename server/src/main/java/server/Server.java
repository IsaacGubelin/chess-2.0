package server;

import dataaccess.*;
import handler.*;
import spark.*;

public class Server {

    public SQLUserDAO userDAO;
    public MemoryGameDAO gameDAO;
    public SQLAuthDAO authDAO;

    public Server() {
        userDAO = new SQLUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new SQLAuthDAO();
    }

    /**
     * Contains all endpoints for the server
     *
     * @param desiredPort port to run server on
     * @return port number
     */
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");


        // CLEAR APPLICATION
        Spark.delete("/db", (req, res) ->new ClearHandler(gameDAO, userDAO, authDAO).handleClearDatabase(res));

        // REGISTER USER
        Spark.post("/user", (req, res) -> new RegisterHandler(userDAO, authDAO).handleRegister(req, res));

        // LOGIN USER
        Spark.post("session", (req, res) -> new LoginHandler(userDAO, authDAO).handleLogin(req, res));

        // LOGOUT USER
        Spark.delete("session", (req, res) -> new LogoutHandler(authDAO).handleLogout(req, res));

        // CREATE GAME
        Spark.post("/game", (req, res) -> new GameHandler(authDAO, gameDAO).handleCreateGame(req, res));

        // LIST GAMES
        Spark.get("/game", (req, res) -> new GameHandler(authDAO, gameDAO).handleListGames(req, res));

        // JOIN GAME
        Spark.put("/game", (req, res) -> new GameHandler(authDAO, gameDAO).handleJoinGame(req, res));





        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
