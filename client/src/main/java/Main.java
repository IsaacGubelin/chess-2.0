import ui.Repl;
import exception.ResponseException;

public class Main {
    public static void main(String[] args) {
        var serverUrl = "http://localhost:8080";    // Default URL
        if (args.length == 1) {                     // Use URL from argument if provided
            serverUrl = args[0];
        }

        Repl repl;
        try {
            repl = new Repl(serverUrl);
            repl.runInterface();
        } catch (ResponseException e) {
            System.out.println("Error: Server may not be online.");
        }
    }
}