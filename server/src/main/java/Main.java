import server.Server;

public class Main {
    public static void main(String[] args) {

        Server server = new Server();
        System.out.println("♕ Starting 240 Chess Server...");
        if (args.length == 1) { // Use port from argument if provided
            try {
                int port = Integer.parseInt(args[0]);
                System.out.println("Using port: " + port);
                server.run(port);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number provided. Using default port 8080.");
                server.run(8080);
            }
        } else {
            System.out.println("No port specified, using default port 8080.");
            server.run(8080);
        }

    }
}