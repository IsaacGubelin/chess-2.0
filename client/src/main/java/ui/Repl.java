package ui;
import chess.*;
import com.google.gson.Gson;
import facade.ServerFacade;
import facade.WSFacade;
import model.*;
import exception.ResponseException;
import websocket.ServiceMessageHandler;
import wsmessages.serverMessages.*;
import wsmessages.serverMessages.Error;

import java.util.HashMap;
import java.util.Scanner;
import static ui.EscapeSequences.*;


public class Repl {


    private UserState status;                 // State of client
    private String name;                      // Username of client
    private ChessGame.TeamColor color;        // Keep track of user's team color
    private String authToken;                 // Keeps track of the user's authToken
    private ChessGame chessGame;              // Local copy of chess game when player joins/creates one

    // CONNECTIONS AND FACADES
    private ServiceMessageHandler msgHandler;

    private final WSFacade wsFacade;          // Access the websocket facade methods
    private final ServerFacade httpFacade;      // Access the HTTP facade methods
    private HashMap<Integer, Integer> gameIDs;  // Keeps track of listed games

    private int currentGameIndex;

    /**
     * ClientUI constructor. This does an in-line implementation of ServerMessageHandler.
     * <p>
     * This class contains a ServerFacade, which in turn contains a WebSocketFacade object.
     * <p>
     * Received messages from the WebSocket facade will be printed in this class.
     *
     * @param url
     * @throws ResponseException
     */
    public Repl(String url) throws ResponseException {
        msgHandler = new ServiceMessageHandler() {  // in-line implementation for needed functions
            @Override
            public void notify(String message) throws ResponseException {
                handleServerMessage(message);  // This function determines the message type and then does needed tasks
            }
        };
        wsFacade = new WSFacade(url, msgHandler); // Initialize websocket facade with given url
        httpFacade = new ServerFacade(url);       // Initialize HTTP facade with same url
        initClientUI();                 // Initialize all other variables
    }

    public Repl(int port) throws ResponseException {
        msgHandler = new ServiceMessageHandler() {  // in-line implementation for needed functions
            @Override
            public void notify(String message) throws ResponseException {
                handleServerMessage(message);  // This function determines the message type and then does needed tasks
            }
        };
        wsFacade = new WSFacade(port, msgHandler); // Initialize websocket facade with given url
        httpFacade = new ServerFacade(port);       // Initialize HTTP facade with same url
        initClientUI();                 // Initialize all other variables
    }

    private void initClientUI() throws ResponseException {   // Ordering matters on these initializations
        gameIDs = new HashMap<>();      // Initialize game ID container
        color = null;
        status = UserState.LOGGED_OUT;  // Status starts in logged out state
        authToken = "";                 // No token at startup
        currentGameIndex = -1;          // Will be updated to positive value when user joins/observes game
    }

    // The main looping interface that collects user input and prints prompt messages.
    public void runInterface() {
        System.out.print(SET_TEXT_COLOR_BLUE + SET_BG_COLOR_BLACK);     // Set welcome color format
        System.out.println("♛ Welcome to COMMAND LINE CHESS! ♛");      // Welcome message
        System.out.println("Type \"help\" to view actions.");           // Prompt user to open help menu

        // Begin main loop of collecting input and performing requested actions.
        while (true) {
            printStatus();
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            String[] inputs = line.split(" ");                    //  Collect and parse input
            int numArgs = inputs.length;
            inputs[0] = inputs[0].toLowerCase();                        // Ignore capitals of first argument


            switch (status) {                                        // State machine for user interface options
                case LOGGED_OUT -> promptLoggedOut(inputs, numArgs);
                case LOGGED_IN -> promptLoggedIn(inputs, numArgs);
                case IN_GAME -> promptInGame(inputs, numArgs, false);
                case OBSERVING -> promptInGame(inputs, numArgs, true);
            }

            if (line.equals("quit") && status.equals(UserState.LOGGED_OUT)) {
                break;               // Quit the application when user enters "quit"
            }
        }
    }

    private void printStatus() {
        setTextToPromptFormat();            // Set text to prompt format
        System.out.print(status + " >>> "); // Show status of user
    }

    // Prints prompts and evaluates input during logged-out state
    private void promptLoggedOut(String[] inputs, int numArgs) {
        // Look at first argument of user's entry. Check following arguments if applicable
        switch (inputs[0]) {
            case "help":
                printHelpScreenLoggedOut();
                break;

            case "register":
                if (numArgs < 2) {      // If user failed to type all required registration info
                    System.out.println("Missing username, password, and email. Use format below:");
                    System.out.println("register <USERNAME> <PASSWORD> <EMAIL>");
                } else if (numArgs < 4) {
                    System.out.println("Missing info. Please try register command again with this format:");
                    System.out.println("register <USERNAME> <PASSWORD> <EMAIL>");
                } else if (numArgs == 4) {
                    UserData userData = new UserData(inputs[1], inputs[2], inputs[3]);  // Generate user data
                    try {
                        var authData = httpFacade.register(userData);  // Call register method in http facade
                        System.out.println("Successful registration for " + inputs[1]);
                        name = inputs[1];                       // Store username
                        authToken = authData.authToken();       // Store token
                        status = UserState.LOGGED_IN;  // Status string shows that newly registered user is logged in

                    } catch (ResponseException ex) {
                        System.out.println(SET_TEXT_COLOR_RED + "Could not register.");
                        System.out.println(ex.getMessage());    // Print thrown error code
                    }
                }
                else {
                    System.out.println("Invalid registration. Please try again.");
                }
                break;

            case "login":
                if (numArgs < 3) {          // Too few arguments
                    System.out.println("Missing username and/or password. Follow below format for login:");
                    System.out.println("login <USERNAME> <PASSWORD>");
                } else if (numArgs > 3) {   // Too many arguments
                    System.out.println("Too many arguments typed in. Follow below format for login:");
                    System.out.println("login <USERNAME> <PASSWORD>");
                } else {    // Correct format, attempt to call server login endpoint
                    UserData userData = new UserData(inputs[1], inputs[2], null); // No email needed for login
                    try {
                        var authData = httpFacade.login(userData);
                        System.out.println("Successful login for " + inputs[1]);
                        name = inputs[1];                   // Store username
                        authToken = authData.authToken();   // Store token
                        status = UserState.LOGGED_IN;
                    } catch (ResponseException ex) {
                        System.out.println(SET_TEXT_COLOR_RED + "Could not log in.");
                        System.out.println(ex.getMessage());        // Print thrown error code
                    }
                }
                break;

            case "quit":
                if (numArgs > 1) {
                    System.out.println("To quit the game, type \"quit\" with no other text.");
                } else {
                    System.out.println("Good bye!" + SET_TEXT_COLOR_WHITE);
                }
                break;

            default:
                System.out.println("Invalid input. Follow these options:");
                printHelpScreenLoggedOut();
                break;
        }
    }

    // Prints prompts and evaluates input during logged-in state
    private void promptLoggedIn(String[] inputs, int numArgs) {
        // Look at first argument of user's entry. Check following arguments if applicable
        switch (inputs[0]) {

            case "create":
                if (numArgs != 2) {
                    System.out.println("Invalid format for creating game. Follow below format:");
                    System.out.println("create <GAME_NAME>");
                } else {
                    String gameName = inputs[1]; // Collect game name input
                    try {
                        GameData idData = httpFacade.createGame(authToken, gameName);
                        System.out.println("Successful game creation. Game ID: " + idData.gameID());
                    } catch (ResponseException ex) {
                        System.out.println(SET_TEXT_COLOR_RED + "Could not create game.");
                        System.out.println(ex.getMessage());
                    }
                }
                break;

            case "list":
                if (numArgs > 1) {
                    System.out.println("To list games, type \"list\" with no other text or arguments.");
                } else {
                    try {
                        ListGamesResponseData gamesList = httpFacade.getGamesList(authToken);
                        listGamesInfo(gamesList);  // Print info for all active chess games
                    } catch (ResponseException ex) {
                        System.out.println(SET_TEXT_COLOR_RED + "Could not list games.");
                        System.out.println(ex.getMessage());
                    }
                }

                break;

            case "join":
                parseJoinPlayer(inputs, numArgs);
                break;

            case "observe":
                if (numArgs != 2) {
                    System.out.println("Invalid entry. To observe a game, use below format:");
                    System.out.println("observe <GAME_ID>");
                } else {
                    updateGamesList();  // Get updated list of available game IDs
                    try {
                        int reqGameIndex = Integer.parseInt(inputs[1]); // Get integer from second argument
                        int id = gameIDs.get(reqGameIndex);             // Retrieve corresponding game ID
                        wsFacade.joinObserve(authToken, id);      // Try to get a LOAD_GAME message

                        currentGameIndex = reqGameIndex;      // If joined, update current game index and team color
                    } catch (NumberFormatException numEx) { // Prints error message if second argument wasn't a number
                        System.out.println("Please use only integers for ID of requested game.");
                    } catch (ResponseException ex) {
                        System.out.println(SET_TEXT_COLOR_RED + "Could not join game.");
                        System.out.println(ex.getMessage());
                    } catch (NullPointerException nullEx) {
                        System.out.println("Specified game ID does not exist.");
                    }
                }

                break;

            case "logout":
                try {
                    httpFacade.logout(authToken);
                    System.out.println("Logged out.");
                    status = UserState.LOGGED_OUT;    // Change status string to logged out state
                } catch (ResponseException ex) {
                    System.out.println(SET_TEXT_COLOR_RED + "Could not logout. Error code: " + ex.getStatusCode());
                    System.out.println(ex.getMessage());
                }
                break;

            case "help":
                printHelpScreenLoggedIn();
                break;


            default:
                System.out.println("Invalid input. Follow these options:");
                printHelpScreenLoggedIn();
                break;
        }
    }

    private void promptInGame(String[] inputs, int numArgs, boolean isObserving) {
        switch (inputs[0]) {
            case "help" -> printHelpScreenInGame(false);
            case "redraw" -> {
                System.out.println("Redraw chess board");
                BoardDisplay.printChessBoard(chessGame.getBoard(), color);
            }
            case "show" -> {
                if (numArgs != 2) {             // To be shown moves for a position, user must provide location
                    System.out.println("Invalid request. Follow format below:");
                    System.out.println("show <COLUMN_LETTER><ROW_NUMBER>");
                } else {
                    try {
                        ChessPosition highlightPos = InputToChessMove.getPositionFromString(inputs[1]);
                        boolean isTeamWhite = color.equals(ChessGame.TeamColor.WHITE);
                        BoardDisplay.printChessBoard(chessGame.getBoard(), isTeamWhite, highlightPos);

                    } catch (InvalidMoveException ex) {
                        System.out.println("Invalid position. Give a position in bounds, such as \"B4\".");
                    }
                }
            }

            case "leave" -> {
                try {
                    int id = gameIDs.get(currentGameIndex); // Get current game ID
                    wsFacade.leaveGame(authToken, id);  // Remove user from game
                    color = null;                   // Reset current color to null
                    status = UserState.LOGGED_IN;   // Transition back to menu for login state
                } catch (ResponseException ex) {
                    System.out.println(SET_TEXT_COLOR_RED + "Error: Could not leave game.");
                }
            }


            case "resign" -> {
                System.out.println("Are you sure you wish to forfeit the game? Enter 'y' or 'n'");
                Scanner scanner = new Scanner(System.in);                   // For reading input
                String line = scanner.nextLine();
                String[] choice = line.split(" ");                    //  Collect and parse input
                if (choice[0].equals("y")) {        // If YES
                    try {
                        wsFacade.playerResign(authToken, gameIDs.get(currentGameIndex));    // Send resign request
                    } catch (ResponseException ex) {
                        System.out.println(SET_TEXT_COLOR_RED + "Error: Couldn't resign.");
                    }
                } else {
                    System.out.println("Resignation cancelled, back in game.");
                }
            }

            case "move" -> {
                if (numArgs != 2) {
                    System.out.println("Invalid format.");
                }
                else try {
                    ChessMove desiredMove = InputToChessMove.getMoveFromString(inputs[1]);  // Parse input for move
                    int gameID = gameIDs.get(currentGameIndex);                             // Get ID of current game
                    wsFacade.playerMakeMove(authToken, gameID, desiredMove);        // Send move to websocket handler
                } catch (InvalidMoveException invalidEx) {
                    System.out.println(invalidEx.getMessage() + " Follow format below:");
                    System.out.println("move <OLD_COL><OLD_ROW><NEW_COL><NEW_ROW>");
                } catch (ResponseException ex) {
                    System.out.println(SET_TEXT_COLOR_RED + ex.getMessage());   // Print out exception if needed
                }
            }

            default -> {
                System.out.println("Invalid input. Follow these options:");
                printHelpScreenInGame(false);
            }
        }
    }

    /**
     * This helper method is called when the repl detects "join" in the input
     * @param inputs array of words typed into keyboard
     * @param numArgs number of words
     */
    private void parseJoinPlayer(String[] inputs, int numArgs) {
        if (numArgs != 3) {
            System.out.println("Invalid entry. To join a game, use below format:");
            System.out.println("join <ID> [WHITE|BLACK]");
        } else if (!inputs[2].equals("WHITE") && !inputs[2].equals("BLACK")) {
            System.out.println("Invalid team request. Type \"BLACK\" or \"WHITE\"");
        } else {
            updateGamesList();  // Update list of games
            try {
                int reqGameIndex = Integer.parseInt(inputs[1]); // Get integer from second argument
                int id = gameIDs.get(reqGameIndex);             // Retrieve corresponding game ID
                ChessGame.TeamColor chosenColor = inputs[2].equals("WHITE") ? ChessGame.TeamColor.WHITE
                        : ChessGame.TeamColor.BLACK;    // Get chosen team color
                JoinGameRequest gameReqData = new JoinGameRequest(chosenColor, id); // Make req
                httpFacade.joinGame(authToken, gameReqData);    // Attempt to join game
                wsFacade.joinGame(authToken, gameReqData);      // Try to get a LOAD_GAME message

                currentGameIndex = reqGameIndex;      // If joined, update current game index and team color
                color = inputs[2].equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;


            } catch (NumberFormatException numEx) { // Prints error message if second argument wasn't a number
                System.out.println("Please use only integers for ID of requested game.");
            } catch (ResponseException ex) {
                System.out.println(SET_TEXT_COLOR_RED + "Could not join game.");
                System.out.println(ex.getMessage());
            } catch (NullPointerException nullEx) {
                System.out.println("Specified game ID does not exist.");
            }
        }
    }


    /**
     * Helper function is used for implementation of ServiceMessageHandler (see client init)
     * @param message The ServerMessage to be evaluated
     */
    private void handleServerMessage(String message) throws ResponseException {
        var js = new Gson();                                                    // Make a Json conversion object
        ServerMessage msg = js.fromJson(message, ServerMessage.class);          // Deserialize into ServerMessage
        ServerMessage.ServerMessageType type = msg.getServerMessageType();      // Determine type of message
        switch (type) {                                             // Choose what to do with message based on type
            case LOAD_GAME -> {
                this.chessGame = js.fromJson(message, LoadGame.class).getGame(); // Update the local game
                System.out.printf("Chess game updated for %s.\n", name);    // Notify user
                // Determine team to print. If color is null, client is a spectator and will see white's perspective.
                boolean printWhiteSide = ((this.color == null) || this.color.equals(ChessGame.TeamColor.WHITE));
                BoardDisplay.printChessBoard(this.chessGame.getBoard(), printWhiteSide); // Print game board
                if (this.color == null) {
                    status = UserState.OBSERVING;
                } else {
                    status = UserState.IN_GAME;
                }
            }
            case ERROR -> {
                String errMsg = new Gson().fromJson(message, Error.class).getMessage();  // Get error message
                System.out.println(SET_TEXT_COLOR_RED + errMsg); // Print the error
            }
            case NOTIFICATION -> {  // If message is notification type, deserialize into notification class
                String notification = new Gson().fromJson(message, Notification.class).getMessage();
                System.out.println(SET_TEXT_COLOR_GREEN + notification);   // Print notification to user's terminal
            }
        }
        printStatus();      // Reprint the status and prompt input
    }

    private void updateGamesList() {
        try {
            ListGamesResponseData gamesList = httpFacade.getGamesList(authToken);
            int i = 0;  // For indexing list
            for (GameData game : gamesList.games()) {
                gameIDs.put(i, game.gameID());  // Pair index with game ID and add to list
                i++;                            // Increment index
            }
        } catch (ResponseException ex) {
            System.out.println(SET_TEXT_COLOR_RED + "Could not update games list.");
            System.out.println(ex.getMessage());
        }
    }



    // Helper method for printing all games and needed info
    private void listGamesInfo(ListGamesResponseData gamesList) {
        System.out.println("All active chess games:");
        int i = 0;      // Update private list of game IDs
        for (GameData game : gamesList.games()) {
            gameIDs.put(i, game.gameID());  // Update game ID list
            System.out.println(i + ". " + game.gameName());
            if (game.blackUsername() == null) {
                System.out.print("Black team is available. ");
            } else {
                System.out.print("Black team occupied by " + game.blackUsername() + ". ");
            }
            if (game.whiteUsername() == null) {
                System.out.println("White team is available.\n");
            } else {
                System.out.println("White team is occupied by " + game.whiteUsername() + ".\n");
            }
            i++;                            // Increment index
        }
    }

    private void printHelpScreenLoggedOut() {
        setTextToPromptFormat();
        System.out.println("register <USERNAME> <PASSWORD> <EMAIL>" + RESET_TEXT_BOLD_FAINT + " --> create an account");
        setTextToPromptFormat();
        System.out.println("login <USERNAME> <PASSWORD>" + RESET_TEXT_BOLD_FAINT + " --> login to play chess");
        setTextToPromptFormat();
        System.out.println("quit" + RESET_TEXT_ITALIC + " --> exit application");
    }

    private void printHelpScreenLoggedIn() {
        setTextToPromptFormat();
        System.out.println("create <GAME_NAME>" + RESET_TEXT_BOLD_FAINT + " --> make a new game");
        setTextToPromptFormat();
        System.out.println("list" + RESET_TEXT_BOLD_FAINT + " --> Show list of games");
        setTextToPromptFormat();
        System.out.println("join <ID> [WHITE|BLACK]" + RESET_TEXT_BOLD_FAINT + " --> join game");
        setTextToPromptFormat();
        System.out.println("observe <ID>" + RESET_TEXT_BOLD_FAINT + " --> be a spectator in a game");
        setTextToPromptFormat();
        System.out.println("logout" + RESET_TEXT_BOLD_FAINT + " --> exit when done");
        setTextToPromptFormat();
        System.out.println("help" + RESET_TEXT_BOLD_FAINT + " --> show available options");
    }

    /**
     * Prints the help menu for available options while in a chess game room.
     * @param isObserving Hides the "move" description, since observers don't participate.
     */
    private void printHelpScreenInGame(boolean isObserving) {
        setTextToPromptFormat();
        System.out.println("redraw" + RESET_TEXT_BOLD_FAINT + " --> Refresh the chess board display");
        setTextToPromptFormat();
        System.out.println("leave" + RESET_TEXT_BOLD_FAINT + " --> Leave current game");
        setTextToPromptFormat();
        if (!isObserving) {     // The make move command is only available for actual players in the chess game
            System.out.println("move <COLUMN><ROW><COLUMN><ROW>" + RESET_TEXT_BOLD_FAINT + " --> move chess piece");
            setTextToPromptFormat();
        }
        System.out.println("resign" + RESET_TEXT_BOLD_FAINT + " --> forfeit the game");
        setTextToPromptFormat();
        System.out.println("show <COLUMN><ROW>" + RESET_TEXT_BOLD_FAINT + " --> Highlight available moves");
        setTextToPromptFormat();
        System.out.println("help" + RESET_TEXT_BOLD_FAINT + " --> show available options");
    }



    // Calls unicode escape sequences to make command prompt text italicized and green
    private void setTextToPromptFormat() {
        System.out.print(SET_TEXT_BOLD + SET_TEXT_ITALIC + SET_TEXT_COLOR_BLUE);
    }
}
