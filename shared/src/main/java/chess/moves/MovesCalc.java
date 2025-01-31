package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

/**
 * Moves Calculator. Houses available functions for child classes to use.
 */
public class MovesCalc {

    static final int UP = 1;
    static final int LEFT = -1;
    static final int DOWN = -1;
    static final int RIGHT = 1;
    static final int SAME_LINE = 0;

    /**
     *  Given a move, this function checks if a chess piece of interest may claim the given new position.
     * @param board the current arrangement of chess pieces
     * @param move the move to be made
     * @return true if the end position is either empty or contains a rival piece and is in bounds.
     */
    private boolean isClaimablePos(ChessBoard board, ChessMove move) {
        ChessGame.TeamColor team = board.getPiece(move.getStartPosition()).getTeamColor();
        ChessPosition endPos = move.getEndPosition();
        return endPos.isInBounds() && (!board.hasPieceAtPos(endPos) || board.hasRivalAtPos(endPos, team));
    }

    /**
     * Check if a piece can move from the start to the end position and add the move if valid.
     * @param moves collection of moves to add to
     * @param board current arrangement of chess pieces
     * @param start old position
     * @param end potential new position
     */
    protected void checkMoveAndAdd(HashSet<ChessMove> moves, ChessBoard board, ChessPosition start, ChessPosition end) {
        if (isClaimablePos(board, new ChessMove(start, end))) {
            moves.add(new ChessMove(start, end.clone()));
        }
    }


}
