package wsmessages.userCommands;

import chess.ChessMove;

public class MakeMove extends UserGameCommand {

    private final int gameID;
    private final ChessMove move;

    public MakeMove(String authToken, int gameID, ChessMove move) {
        super(authToken);
        this.commandType = CommandType.MAKE_MOVE;
        this.gameID = gameID;
        this.move = move;
    }

    public int getGameID() {
        return this.gameID;
    }

    public ChessMove getMove() {
        return this.move;
    }
}
