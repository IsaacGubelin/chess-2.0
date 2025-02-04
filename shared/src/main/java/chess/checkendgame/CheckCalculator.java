package chess.checkendgame;

import chess.*;
import chess.moves.BishopMovesCalc;
import chess.moves.KingMovesCalc;
import chess.moves.KnightMovesCalc;
import chess.moves.RookMovesCalc;

import java.util.HashSet;


public class CheckCalculator {

    //TODO: consolidate duplicate code

    /**
     * Check a given position's safety by evaluating if it is under attack by an opponent piece.
     * @param board current arrangement of chess pieces
     * @param position position to check safety for
     * @return true if position is under threat of capture by opponent
     */
    public boolean positionInDanger(ChessBoard board, ChessPosition position) {
        if (!board.hasPieceAtPos(position)) { // Edge case if position is empty
            return false;
        }
        ChessGame.TeamColor team = board.getPiece(position).getTeamColor();
        ChessGame.TeamColor opponent =
                (team == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

        return (inCheckDiagonally(board, position, opponent) || inCheckOrthogonally(board, position, opponent)) ||
                inCheckByPawn(board, position, opponent) || inCheckByKnight(board, position, opponent) ||
                inCheckByKing(board, position, opponent);
    }

    /**
     * Check spaces diagonally from a king's spot for an enemy bishop or queen.
     * <p>
     *     Uses the bishop moves calculator to treat the king's position as a possible move for a diagonally-moving
     *     enemy. Calculates the moves possible for an enemy bishop, treating the king's location as a destination.
     *     Checks all end positions returned from the calculator for potential danger from an enemy bishop or queen.
     * @param board current arrangement of chess pieces
     * @param position position to check safety for
     * @param opponent team color of opponent
     * @return true if an enemy bishop or queen is found in a diagonal position from the king.
     */
    private boolean inCheckDiagonally(ChessBoard board, ChessPosition position, ChessGame.TeamColor opponent) {
        BishopMovesCalc bishopCalc = new BishopMovesCalc();
        ChessPiece enemyBishop = new ChessPiece(opponent, ChessPiece.PieceType.BISHOP);
        ChessPiece enemyQueen = new ChessPiece(opponent, ChessPiece.PieceType.QUEEN);

        for (ChessMove move : bishopCalc.getBishopMoves(board, position)) {
            ChessPiece possibleEnemy = board.getPiece(move.getEndPosition());
            if (possibleEnemy != null && (possibleEnemy.equals(enemyBishop) || possibleEnemy.equals(enemyQueen)))
                return true;
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

        for (ChessMove move : rookCalc.getRookMoves(board, position)) {
            ChessPiece possibleEnemy = board.getPiece(move.getEndPosition());
            if (possibleEnemy != null && (possibleEnemy.equals(enemyRook) || possibleEnemy.equals(enemyQueen)))
                return true;
        }
        return false;
    }

    /**
     * Evaluate danger to king from a pawn.
     * @param board current arrangement of pieces
     * @param position position of king
     * @param opponent team color of opponent
     * @return true if a pawn is in a position to capture the king
     */
    private boolean inCheckByPawn(ChessBoard board, ChessPosition position, ChessGame.TeamColor opponent) {
        ChessPiece enemyPawn = new ChessPiece(opponent, ChessPiece.PieceType.PAWN);

        int checkRow = (opponent == ChessGame.TeamColor.WHITE) ? position.getRow() - 1 : position.getRow() + 1;
        ChessPosition checkPawnLeft = new ChessPosition(checkRow, position.getColumn() - 1);
        ChessPosition checkPawnRight = new ChessPosition(checkRow, position.getColumn() + 1);
        return (board.hasPieceAtPos(checkPawnLeft) && board.getPiece(checkPawnLeft).equals(enemyPawn) ||
                board.hasPieceAtPos(checkPawnRight) && board.getPiece(checkPawnRight).equals(enemyPawn));
    }

    /**
     * Check if an enemy knight can move and land on the given position.
     * @param board current arrangement of pieces
     * @param position position to check safety for
     * @param opponent team color of opponent
     * @return true if an opponent knight can move into the position
     */
    private boolean inCheckByKnight(ChessBoard board, ChessPosition position, ChessGame.TeamColor opponent) {
        ChessPiece enemyKnight = new ChessPiece(opponent, ChessPiece.PieceType.KNIGHT);
        KnightMovesCalc knightCalc = new KnightMovesCalc();

        for (ChessMove move : knightCalc.getKnightMoves(board, position)) {
            ChessPiece possibleEnemy = board.getPiece(move.getEndPosition());
            if (possibleEnemy != null && possibleEnemy.equals(enemyKnight))
                return true;
        }
        return false;
    }

    private boolean inCheckByKing(ChessBoard board, ChessPosition position, ChessGame.TeamColor opponent) {
        ChessPiece enemyKing = new ChessPiece(opponent, ChessPiece.PieceType.KING);
        KingMovesCalc kingCalc = new KingMovesCalc();

        for (ChessMove move : kingCalc.getKingMoves(board, position)) {
            ChessPiece possibleEnemy = board.getPiece(move.getEndPosition());
            if (possibleEnemy != null && possibleEnemy.equals(enemyKing))
                return true;
        }
        return false;
    }
}
