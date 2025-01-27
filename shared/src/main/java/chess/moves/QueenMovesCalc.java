package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class QueenMovesCalc {

    /**
     * Function to collect valid moves for a queen piece.
     * @param board current arrangement of chess pieces
     * @param startPosition current position of queen piece
     * @return union of moves returned from bishop/rook move calculators
     */
    public HashSet<ChessMove> getQueenMoves(ChessBoard board, ChessPosition startPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        BishopMovesCalc diagMoves = new BishopMovesCalc(); // For finding diagonal moves
        RookMovesCalc orthogMoves = new RookMovesCalc(); // For finding horizontal/vertical moves
        moves.addAll(diagMoves.getBishopMoves(board, startPosition));
        moves.addAll(orthogMoves.getRookMoves(board, startPosition));

        return moves;
    }
}
