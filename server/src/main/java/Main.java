import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {

        Server server = new Server();
        System.out.println("♕ Starting 240 Chess Server...");
        server.run(8080); //FIXME: Use arg[] port
    }
}