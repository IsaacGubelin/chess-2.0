package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

public interface MovesCalc {

    final int TEST_NUM = 9;

    /**
     *  Given a move, this function checks if a chess piece of interest may claim the given new position.
     * @param board the current arrangement of chess pieces
     * @param move the move to be made
     * @return true if the end position is either empty or contains a rival piece and is in bounds.
     */
    default boolean isClaimablePos(ChessBoard board, ChessMove move) {
        ChessGame.TeamColor team = board.getPiece(move.getStartPosition()).getTeamColor();
        ChessPosition endPos = move.getEndPosition();
        return endPos.isInBounds() && (!board.hasPieceAtPos(endPos) || board.hasRivalAtPos(endPos, team));
    }


}
