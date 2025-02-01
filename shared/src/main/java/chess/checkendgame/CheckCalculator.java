package chess.checkendgame;

import chess.*;
import chess.moves.BishopMovesCalc;
import chess.moves.RookMovesCalc;

import java.util.HashSet;


public class CheckCalculator {

    //TODO: consolidate duplicate code

    public boolean positionInDanger(ChessBoard board, ChessPosition position) {
        if (!board.hasPieceAtPos(position)) { // Edge case if position is empty
            return false;
        }
        ChessGame.TeamColor team = board.getPiece(position).getTeamColor();
        ChessGame.TeamColor opponent =
                (team == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

        return (inCheckDiagonally(board, position, opponent) ||
                inCheckOrthogonally(board, position, opponent));
        // Retrieve valid knight moves for a pretend knight of opponent color
        // Check each end position for an opponent knight
        // Return true if one is found!

        // Rinse and repeat for other pieces EXCEPT

        // the pawn. Just check diagonal spaces manually.
    }

    /**
     * Check spaces diagonally from a king's spot for an enemy bishop or queen.
     * <p>
     *     Uses the bishop moves calculator to treat the king's position as a possible move for a diagonally-moving
     *     enemy. Calculates the moves possible for an enemy bishop, treating the king's location as a destination.
     *     Checks all end positions returned from the calculator for potential danger from an enemy bishop or queen.
     * @param board current arrangement of chess pieces.
     * @param position position to check safety for
     * @param opponent team color of opponent
     * @return true if an enemy bishop or queen is found in a diagonal position from the king.
     */
    private boolean inCheckDiagonally(ChessBoard board, ChessPosition position, ChessGame.TeamColor opponent) {
        BishopMovesCalc bishopCalc = new BishopMovesCalc();
        ChessPiece enemyBishop = new ChessPiece(opponent, ChessPiece.PieceType.BISHOP);
        ChessPiece enemyQueen = new ChessPiece(opponent, ChessPiece.PieceType.QUEEN);

        HashSet<ChessMove> diagonalMoves = bishopCalc.getBishopMoves(board, position);
        for (ChessMove move : diagonalMoves) {
            ChessPosition pos = move.getEndPosition();
            ChessPiece possibleEnemy = board.getPiece(pos);
            if (possibleEnemy != null && (possibleEnemy.equals(enemyBishop) || possibleEnemy.equals(enemyQueen))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check spaces horizontally and vertically from a king's spot for an enemy rook or queen.
     * <p>
     *     Uses the rook moves calculator to treat the king's position as a possible move for an enemy.
     *     Calculates the moves possible for an enemy rook, treating the king's location as a destination.
     *     Checks all end positions returned from the calculator for potential danger from an enemy rook or queen.
     * @param board current arrangement of chess pieces.
     * @param position position to check safety for
     * @param opponent team color of opponent
     * @return true if an enemy rook or queen is found horizontally or vertically from the king.
     */
    private boolean inCheckOrthogonally(ChessBoard board, ChessPosition position, ChessGame.TeamColor opponent) {
        RookMovesCalc rookCalc = new RookMovesCalc();
        ChessPiece enemyRook = new ChessPiece(opponent, ChessPiece.PieceType.ROOK);
        ChessPiece enemyQueen = new ChessPiece(opponent, ChessPiece.PieceType.QUEEN);

        HashSet<ChessMove> orthogonalMoves = rookCalc.getRookMoves(board, position);
        for (ChessMove move : orthogonalMoves) {
            ChessPosition pos = move.getEndPosition();
            ChessPiece possibleEnemy = board.getPiece(pos);
            if (possibleEnemy != null && (possibleEnemy.equals(enemyRook) || possibleEnemy.equals(enemyQueen))) {
                return true;
            }
        }
        return false;
    }
}
