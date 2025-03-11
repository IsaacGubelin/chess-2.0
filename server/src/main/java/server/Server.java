package server;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import handler.ClearHandler;
import handler.RegisterHandler;
import spark.*;

public class Server {

    public MemoryUserDAO userDAO = new MemoryUserDAO();
    public MemoryGameDAO gameDAO = new MemoryGameDAO();
    public MemoryAuthDAO authDAO = new MemoryAuthDAO();

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
        Spark.delete("/db", (req, res) ->new ClearHandler(gameDAO, userDAO, authDAO).handleClearDatabase(req, res));

        // REGISTER USER
        Spark.post("/user", (req, res) -> new RegisterHandler(userDAO, authDAO).registerHandle(req, res));

        // LOGIN USER





        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
