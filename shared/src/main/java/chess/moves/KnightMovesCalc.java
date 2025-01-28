package chess.moves;
import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class KnightMovesCalc implements MovesCalc {

    /**
     * Take a position for a knight and evaluate all possible spaces it can move to
     *
     * @param board the chessboard and pieces
     * @param currPos position where knight currently sits
     * @return set of all available moves
     */
    public HashSet<ChessMove> getKnightMoves(ChessBoard board, ChessPosition currPos) {
        HashSet<ChessMove> possibleMoves = new HashSet<>();

        final int[][] knightOffsets = {
                {2, -1}, {2, 1}, {1, -2}, {1, 2}, {-1, -2}, {-1, 2}, {-2, -1}, {-2, 1}
        };
        // Evaluate each possible move
        for (int[] offset : knightOffsets) {
            int newRow = currPos.getRow() + offset[0];
            int newCol = currPos.getColumn() + offset[1];
            ChessPosition endPos = new ChessPosition(newRow, newCol);
            checkMoveAndAdd(possibleMoves, board, currPos, endPos);
        }
        return possibleMoves;
    }
}
