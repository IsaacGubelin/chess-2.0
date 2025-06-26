package wsmessages.userCommands;

public class Resign extends UserGameCommand {
    private int gameID;
    public Resign(String authToken, int gameID) {
        super(authToken);
        this.commandType = CommandType.RESIGN;
        this.gameID = gameID;
    }

    public int getGameID() {
        return this.gameID;
    }
}
