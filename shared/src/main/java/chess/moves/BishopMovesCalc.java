package chess.moves;
import chess.*;

import java.util.Collection;
import java.util.HashSet;

public class BishopMovesCalc {

    /**
     * Take a position for a bishop and evaluate all possible squares it can move to
     *
     * @param board
     * @param currPos
     * @return
     */
    public Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition currPos) {
        HashSet<ChessMove> possibleMoves = new HashSet<>();

        ChessGame.TeamColor team = board.getPiece(currPos).getTeamColor();
        int r = currPos.getRow();
        int c = currPos.getColumn();

        // Start checking ^> direction
        ChessPosition endPos = new ChessPosition(r + 1, c + 1);
        while (endPos.isInBounds()) {
            // Check if the current position of interest has a piece
            if (board.hasPieceAtPos(endPos)) {
                // If the encountered piece in this direction is a rival, add possible capture move
                if (board.hasRivalAtPos(endPos, team)) {
                    possibleMoves.add(new ChessMove(currPos, endPos.clone(), null));
                }
                // Cannot move over a piece, break and check moves in other directions
                break;
            }
            // Space must be empty. Add to collection and keep checking for more available moves.
            possibleMoves.add(new ChessMove(currPos, endPos.clone(), null));
            endPos.incRow(); // Move position pointer up
            endPos.incCol(); // and to the right
        }

        endPos = new ChessPosition(r + 1, c - 1); // Check <^ direction
        while (endPos.isInBounds()) {
            if (board.hasPieceAtPos(endPos)) {
                if (board.hasRivalAtPos(endPos, team)) {
                    possibleMoves.add(new ChessMove(currPos, endPos.clone(), null));
                }
                break;
            }
            possibleMoves.add(new ChessMove(currPos, endPos.clone(), null));
            endPos.incRow();
            endPos.decCol();
        }

        endPos = new ChessPosition(r - 1, c - 1); // Check <v direction
        while (endPos.isInBounds()) {
            if (board.hasPieceAtPos(endPos)) {
                if (board.hasRivalAtPos(endPos, team)) {
                    possibleMoves.add(new ChessMove(currPos, endPos.clone(), null));
                }
                break;
            }
            possibleMoves.add(new ChessMove(currPos, endPos.clone(), null));
            endPos.decRow();
            endPos.decCol();
        }

        endPos = new ChessPosition(r - 1, c + 1); // Check v> direction
        while (endPos.isInBounds()) {
            if (board.hasPieceAtPos(endPos)) {
                if (board.hasRivalAtPos(endPos, team)) {
                    possibleMoves.add(new ChessMove(currPos, endPos.clone(), null));
                }
                break;
            }
            possibleMoves.add(new ChessMove(currPos, endPos.clone(), null));
            endPos.decRow();
            endPos.incCol();
        }
        return possibleMoves;
    }
}
