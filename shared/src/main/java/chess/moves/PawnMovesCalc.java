package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessPiece;

import java.util.HashSet;

public class PawnMovesCalc {

    /**
     * Get valid pawn moves. Works for black and white pawns.
     * @param board current arrangement of chess pieces
     * @param currPos current position of pawn
     * @return set of valid moves for the pawn at the given position
     */
    public HashSet<ChessMove> getPawnMoves(ChessBoard board, ChessPosition currPos) {
        HashSet<ChessMove> moves = new HashSet<>();
        ChessGame.TeamColor team = board.getPiece(currPos).getTeamColor();
        ChessGame.TeamColor opponent =
                (team == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        int moveDir = (team == ChessGame.TeamColor.WHITE) ? 1 : -1; // White moves up, Black moves down
        int r = currPos.getRow();
        int c = currPos.getColumn();
        final int START_ROW = (moveDir == 1) ? 2 : 7;
        final int PROMOTION_ROW = (moveDir == 1) ? 8 : 1;

        // Check for available double-forward move if pawn is at start
        if (r == START_ROW && !board.hasPieceAtPos(r + moveDir, c) &&
                !board.hasPieceAtPos(r + 2 * moveDir, c)) {
            moves.add(new ChessMove(currPos, new ChessPosition(r + 2 * moveDir, c)));
        }

        // Regular one-square forward move
        if (r + moveDir > 0 && r + moveDir <= 8 && !board.hasPieceAtPos(r + moveDir, c)) {
            ChessPosition newPos = new ChessPosition(r + moveDir, c);
            if (r + moveDir == PROMOTION_ROW)
                addPromotionMoves(moves, currPos, newPos);
            else
                moves.add(new ChessMove(currPos, newPos));
        }

        // Capture moves (diagonal left and right)
        for (int dc : new int[]{-1, 1}) {
            int newC = c + dc;
            if (newC >= 1 && newC <= 8 && board.hasPieceAtPos(r + moveDir, newC) &&
                    board.getPiece(new ChessPosition(r + moveDir, newC)).getTeamColor() == opponent) {
                ChessPosition newPos = new ChessPosition(r + moveDir, newC);
                if (r + moveDir == PROMOTION_ROW)
                    addPromotionMoves(moves, currPos, newPos);
                else
                    moves.add(new ChessMove(currPos, newPos));
            }
        }
        return moves;
    }

    /**
     * Helper method to add all four pawn promotion moves to a set of ChessMove objects.
     * @param moves set of moves to add to
     * @param currPos current position of pawn
     * @param newPos new position of pawn
     */
    private void addPromotionMoves(HashSet<ChessMove> moves, ChessPosition currPos, ChessPosition newPos) {
        for (ChessPiece.PieceType type : new ChessPiece.PieceType[]{
                ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN }) {
            moves.add(new ChessMove(currPos, newPos, type));
        }
    }
}
