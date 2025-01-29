package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessPiece;

import java.util.HashSet;

public class PawnMovesCalc {

    /**
     * Top level pawn move calculator. Determines which helper method to call based on team color.
     * @param board current arrangement of pieces
     * @param currPos position of pawn
     * @return set of valid moves for pawn
     */
    public HashSet<ChessMove> getPawnMoves(ChessBoard board, ChessPosition currPos) {
        ChessGame.TeamColor team = board.getPiece(currPos).getTeamColor();
        return (team == ChessGame.TeamColor.WHITE) ? whitePawnMoves(board, currPos) : blackPawnMoves(board, currPos);
    }

    private HashSet<ChessMove> blackPawnMoves(ChessBoard board, ChessPosition currPos) {
        HashSet<ChessMove> moves = new HashSet<>();

        int r = currPos.getRow();
        int c = currPos.getColumn();

        // First, check if pawn hasn't moved. Check 1 and 2 spaces ahead. If clear, pawn can move two spaces.
        if (r == 7 && !board.hasPieceAtPos(r - 1, c) && !board.hasPieceAtPos(r - 1, c)) {
            moves.add(new ChessMove(currPos, new ChessPosition(r - 2, c)));
        }

        // Check for ordinary forward move availability. A promotion may occur if pawn is in row 2.
        if (r > 1 && !board.hasPieceAtPos(r - 1, c)) {
            // Check if pawn is about to reach last row. If so, add promotion piece moves.
            if (r == 2)
                addPromotionMoves(moves, currPos, new ChessPosition(r - 1, c));
            else // Otherwise, add just a standard forward move.
                moves.add(new ChessMove(currPos, new ChessPosition(r - 1, c)));
        }

        // Check for possible capture moves. These may also result in promotion.
        // Check down and left for enemy white piece. Check that pawn is not at far left of board.
        if (c > 1 && r > 1 && board.hasPieceAtPos(r - 1, c - 1) &&
                board.getPiece(new ChessPosition(r - 1, c - 1)).getTeamColor() == ChessGame.TeamColor.WHITE) {
            // Check for promotion possibility
            if (r == 2)
                addPromotionMoves(moves, currPos, new ChessPosition(r - 1, c - 1));
            else // Otherwise, add ordinary forward move
                moves.add(new ChessMove(currPos, new ChessPosition(r - 1, c - 1)));
        }
        // Check down and right for enemy white piece. Check that pawn is not at far right of board.
        if (c < 8 && r > 1 && board.hasPieceAtPos(r - 1, c + 1) &&
                board.getPiece(new ChessPosition(r - 1, c + 1)).getTeamColor() == ChessGame.TeamColor.WHITE) {
            // Check for promotion possibility
            if (r == 2)
                addPromotionMoves(moves, currPos, new ChessPosition(r - 1, c + 1));
            else
                moves.add(new ChessMove(currPos, new ChessPosition(r - 1, c + 1)));
        }
        // All possible black pawn moves collected.
        return moves;

    }

    private HashSet<ChessMove> whitePawnMoves(ChessBoard board, ChessPosition currPos) {
        HashSet<ChessMove> moves = new HashSet<>();

        int r = currPos.getRow();
        int c = currPos.getColumn();

        // First, check if pawn hasn't moved. Check 1 and 2 spaces ahead. If clear, pawn can move two spaces.
        if (r == 2 && !board.hasPieceAtPos(r + 1, c) && !board.hasPieceAtPos(r + 2, c)) {
            moves.add(new ChessMove(currPos, new ChessPosition(r + 2, c)));
        }

        // Check for ordinary forward move availability. A promotion may occur if pawn is in row 7.
        if (r < 8 && !board.hasPieceAtPos(r + 1, c)) {
            // Check if pawn is about to reach last row. If so, add promotion piece moves.
            if (r == 7)
                addPromotionMoves(moves, currPos, new ChessPosition(r + 1, c));
            else
                moves.add(new ChessMove(currPos, new ChessPosition(r + 1, c)));
        }

        // Check for possible capture moves. These may also result in promotion.
        // Check up and left for enemy black piece. Check that pawn is not at far left of board.
        if (c > 1 && r < 8 && board.hasPieceAtPos(r + 1, c - 1) &&
                board.getPiece(new ChessPosition(r + 1, c - 1)).getTeamColor() == ChessGame.TeamColor.BLACK) {
            // Check for promotion possibility
            if (r == 7)
                addPromotionMoves(moves, currPos, new ChessPosition(r + 1, c - 1));
            else // Otherwise, add ordinary forward move
                moves.add(new ChessMove(currPos, new ChessPosition(r + 1, c - 1)));
        }
        // Check up and right for enemy black piece. Check that pawn is not at far right of board.
        if (c < 8 && r < 8 && board.hasPieceAtPos(r + 1, c + 1) &&
                board.getPiece(new ChessPosition(r + 1, c + 1)).getTeamColor() == ChessGame.TeamColor.BLACK) {
            // Check for promotion possibility
            if (r == 7)
                addPromotionMoves(moves, currPos, new ChessPosition(r + 1, c + 1));
            else
                moves.add(new ChessMove(currPos, new ChessPosition(r + 1, c + 1)));
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
        moves.add(new ChessMove(currPos, newPos, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(currPos, newPos, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(currPos, newPos, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(currPos, newPos, ChessPiece.PieceType.QUEEN));
    }
}
