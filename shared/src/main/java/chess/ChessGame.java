package chess;

import chess.checkendgame.CheckCalculator;

import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
        whiteKingLocation = new ChessPosition(1, 5);
        blackKingLocation = new ChessPosition(8, 5);
    }

    /**
     * Private data
     */
    private ChessBoard board;
    private ChessGame.TeamColor teamTurn;
    private ChessPosition whiteKingLocation; // Check these positions each turn for check/stalemate
    private ChessPosition blackKingLocation;

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        ChessPosition posToCheck = (teamColor == TeamColor.WHITE) ? whiteKingLocation : blackKingLocation;
        CheckCalculator checkCalc = new CheckCalculator();
        return checkCalc.positionInDanger(this.board, posToCheck);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) // Edge case: team must be in check
            return false;
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) // Edge case: no current attack is present during stalemate
            return false;
        throw new RuntimeException("Not implemented");
    }

    /**
     * Locates king with given team color and returns its coordinates.
     * @return ChessPosition object of king's location
     */
    private ChessPosition findKing(ChessGame.TeamColor team) {
        ChessPiece kingToFind = new ChessPiece(team, ChessPiece.PieceType.KING);
        for (int r = 1; r <= board.GRID_SIZE; r++) {
            for (int c = 1; c <= board.GRID_SIZE; c++) {
                ChessPosition searchPos = new ChessPosition(r, c);
                if (board.hasPieceAtPos(searchPos) && board.getPiece(searchPos).equals(kingToFind))
                    return searchPos;
            }
        }
        return null;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        whiteKingLocation = findKing(TeamColor.WHITE);
        blackKingLocation = findKing(TeamColor.BLACK);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame game = (ChessGame) o;
        return board.equals(game.board) && teamTurn == game.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}
