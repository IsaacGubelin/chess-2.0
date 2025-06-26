package chess;

import chess.checkendgame.CheckCalculator;

import java.util.Collection;
import java.util.HashSet;
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
        gameOver = false;
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
    private boolean gameOver;

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
        ChessPiece piece = board.getPiece(startPosition);
        ChessBoard boardCopy = board.copy();
        Collection<ChessMove> validMoves = new HashSet<>();

        if (!board.hasPieceAtPos(startPosition))
            return validMoves; // If space has no piece, return empty set
        for (ChessMove move : piece.pieceMoves(board, startPosition)) {
            board.removePiece(move.getStartPosition());
            board.addPiece(move.getEndPosition(), piece);
            updateKingLoc(move, piece);
            if (!isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }
            setBoard(boardCopy); // Reset board to original state, update the king locations if applicable
        }
        return validMoves;
    }

    /**
     * Check if a given team has no valid moves left for any of their pieces.
     * @param team team color to check valid moves for
     * @return true if team has no valid moves
     */
    private boolean noValidMovesLeft(ChessGame.TeamColor team) {
        Collection<ChessMove> moves = new HashSet<>();
        for (int r = 1; r <= board.GRID_SIZE; r++) {
            for (int c = 1; c <= board.GRID_SIZE; c++) {
                ChessPosition posToCheck = new ChessPosition(r, c);
                if (board.hasPieceAtPos(posToCheck) && board.getPiece(posToCheck).getTeamColor() == team &&
                        !validMoves(posToCheck).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (isInvalidMove(move))
            throw new InvalidMoveException("Illegal move.");
        ChessPiece movePiece = board.getPiece(move.getStartPosition());
        board.removePiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), movePiece);
        updateKingLoc(move, movePiece); // Check if piece was a king and update its location
        validatePromotion(move); // Assert promotion if pawn reaches end of board

        // TODO: Assert if game is over using checkmate/stalemate rules. Flip turn if not over. Set gameOver if over.
        flipTeamTurn();
    }

    /**
     * Use after making a move to check if a king was moved, and update its location
     *
     * @param move chess move that was previously performed
     * @param piece piece that was moved
     */
    private void updateKingLoc(ChessMove move, ChessPiece piece) {
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.getTeamColor() == TeamColor.WHITE)
                whiteKingLocation = move.getEndPosition();
            else
                blackKingLocation = move.getEndPosition();
        }
    }

    /**
     * Assert promotion for pawn. Used right after a move is made.
     * @param move
     */
    private void validatePromotion(ChessMove move) {
        int row = move.getEndPosition().getRow(); // Evaluate row piece ended on
        if (board.getPiece(move.getEndPosition()).getPieceType() == ChessPiece.PieceType.PAWN &&
                (row == 1 || row == 8)) {
            board.removePiece(move.getEndPosition());
            board.addPiece(move.getEndPosition(), new ChessPiece(teamTurn, move.getPromotionPiece()));
        }
    }

    /**
     * Flips the team turn. Performed after each move.
     */
    private void flipTeamTurn() {
        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
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
        return  (isInCheck(teamColor) && noValidMovesLeft(teamColor));
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return (!isInCheck(teamColor) && noValidMovesLeft(teamColor));
    }

    /**
     * Test the validity of a move.
     * @param move the ChessMove object to evaluate
     * @return true if the move is invalid, incomplete, doesn't move a piece, is out of bounds, or moves out of turn.
     */
    private boolean isInvalidMove(ChessMove move) {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (!validMoves.contains(move) || move.getStartPosition() == null || move.getEndPosition() == null ||
                !move.getEndPosition().isInBounds())
            return true;
        return (board.getPiece(move.getStartPosition()).getTeamColor() != teamTurn); // Check whose turn it is
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
        this.board = board.copy();
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
