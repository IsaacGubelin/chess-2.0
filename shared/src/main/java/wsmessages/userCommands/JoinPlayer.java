package wsmessages.userCommands;

import chess.ChessGame;

public class JoinPlayer extends UserGameCommand {

    private final int gameID;
    private final ChessGame.TeamColor playerColor;
    public JoinPlayer(String authToken, int gameID, ChessGame.TeamColor color) {
        super(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.gameID = gameID;
        this.playerColor = color;
    }

    public int getGameID() {                            // Getter for game ID
        return gameID;
    }

    public ChessGame.TeamColor getRequestedColor() {    // Getter for team color
        return this.playerColor;
    }

}
