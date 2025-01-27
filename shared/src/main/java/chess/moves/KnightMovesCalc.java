package chess.moves;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

public class KnightMovesCalc implements MovesCalc{

    /**
     * Take a position for a knight and evaluate all possible spaces it can move to
     *
     * @param board the chessboard and pieces
     * @param currPos position where knight currently sits
     * @return set of all available moves
     */
    public Collection<ChessMove> getKnightMoves(ChessBoard board, ChessPosition currPos) {
        HashSet<ChessMove> possibleMoves = new HashSet<>();

        int r = currPos.getRow();
        int c = currPos.getColumn();
        ChessGame.TeamColor team = board.getPiece(currPos).getTeamColor();

        ChessPosition endPos = new ChessPosition(r + 2, c - 1);
        checkPosAndAdd(possibleMoves, board, currPos, endPos);
        endPos = new ChessPosition(r + 2, c + 1);
        checkPosAndAdd(possibleMoves, board, currPos, endPos);
        endPos = new ChessPosition(r + 1, c - 2);
        checkPosAndAdd(possibleMoves, board, currPos, endPos);
        endPos = new ChessPosition(r + 1, c + 2);
        checkPosAndAdd(possibleMoves, board, currPos, endPos);
        endPos = new ChessPosition(r - 1, c - 2);
        checkPosAndAdd(possibleMoves, board, currPos, endPos);
        endPos = new ChessPosition(r - 1, c + 2);
        checkPosAndAdd(possibleMoves, board, currPos, endPos);
        endPos = new ChessPosition(r - 2, c - 1);
        checkPosAndAdd(possibleMoves, board, currPos, endPos);
        endPos = new ChessPosition(r - 2, c + 1);
        checkPosAndAdd(possibleMoves, board, currPos, endPos);
        return possibleMoves;
    }

    /**
     * Check if a piece can move from the start to the end position and add the move if valid.
     * @param moves collection of moves to add to
     * @param board current arrangment of chess pieces
     * @param start old position
     * @param end potential new position
     */
    void checkPosAndAdd(HashSet<ChessMove> moves, ChessBoard board, ChessPosition start, ChessPosition end) {
        if (isClaimablePos(board, new ChessMove(start, end, null))) {
            moves.add(new ChessMove(start, end.clone(), null));
        }
    }

}
