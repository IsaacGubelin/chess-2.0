package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class KingMovesCalc extends MovesCalc {

    /**
     * Check available moves for a king. Does not evaluate stalemate or check.
     * @param board current arrangement of pieces
     * @param currPos current position where king sits
     * @return set of available moves for king
     */
    public HashSet<ChessMove> getKingMoves(ChessBoard board, ChessPosition currPos) {
        HashSet<ChessMove> moves = new HashSet<>();

        final int[][] kingOffsets = {
                {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}
        };
        // Evaluate each move
        for (int[] offset : kingOffsets) {
            int newRow = currPos.getRow() + offset[0];
            int newCol = currPos.getColumn() + offset[1];
            ChessPosition endPos = new ChessPosition(newRow, newCol);
            checkMoveAndAdd(moves, board, currPos, endPos);
        }
        return moves;
    }
}
