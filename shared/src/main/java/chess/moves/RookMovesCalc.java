package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class RookMovesCalc extends MovesCalc {



    /**
     * Take a position for a rook and evaluate all available moves
     *
     * @param board current chessboard and arrangement of pieces
     * @param currPos position where rook currently sits
     * @return set of available moves for rook
     */
    public HashSet<ChessMove> getRookMoves(ChessBoard board, ChessPosition currPos) {
        HashSet<ChessMove> possibleMoves = new HashSet<>();

        possibleMoves.addAll(getOrthogonalMoves(board, currPos, UP, SAME_LINE));
        possibleMoves.addAll(getOrthogonalMoves(board, currPos, DOWN, SAME_LINE));
        possibleMoves.addAll(getOrthogonalMoves(board, currPos, SAME_LINE, LEFT));
        possibleMoves.addAll(getOrthogonalMoves(board, currPos, SAME_LINE, RIGHT));
        return possibleMoves;
    }
    /**
     * Helper method to look for available moves in an up, down, left, or right direction on the board
     *
     * @param board current chessboard
     * @param currPos start position for move
     * @param rowOffset +1 or -1 for indicating search up/down
     * @param colOffset +1 or -1 for indicating search left/right
     * @return all moves in requested orthogonal direction
     */
    private HashSet<ChessMove> getOrthogonalMoves(ChessBoard board, ChessPosition currPos, int rowOffset, int colOffset) {
        HashSet<ChessMove> possibleMoves = new HashSet<>();
        int r = currPos.getRow();
        int c = currPos.getColumn();
        ChessGame.TeamColor team = board.getPiece(currPos).getTeamColor();

        // Set position of interest one diagonal space away and continue searching in that direction
        ChessPosition endPos = new ChessPosition(r + rowOffset, c + colOffset);
        while (endPos.isInBounds()) {

            // Check if the current position of interest has a piece
            if (board.hasPieceAtPos(endPos)) {
                // If the encountered piece in this direction is a rival, add possible capture move
                if (board.hasRivalAtPos(endPos, team)) {
                    possibleMoves.add(new ChessMove(currPos, endPos.clone()));
                }
                // Cannot move over a piece, break and check moves in other directions
                break;
            }
            // Space must be empty. Add to collection and keep checking for more available moves.
            possibleMoves.add(new ChessMove(currPos, endPos.clone()));
            moveToNextPosition(endPos, rowOffset, colOffset);
        }
        return possibleMoves;
    }

    /**
     * Helper to move a position pointer in the direction a search is being made
     *
     * @param pos ChessPosition object
     * @param rowOffset indicate up/down movement
     * @param colOffset indicate left/right movement
     */
    private void moveToNextPosition(ChessPosition pos, int rowOffset, int colOffset) {
        if (rowOffset == UP) {
            pos.incRow();
        } else if (rowOffset == DOWN) {
            pos.decRow();
        }
        if (colOffset == LEFT) {
            pos.decCol();
        } else if (colOffset == RIGHT) {
            pos.incCol();
        }
    }
}
