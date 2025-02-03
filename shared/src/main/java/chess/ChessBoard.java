package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    public final int GRID_SIZE = 8;

    public ChessBoard() {
        grid = new ChessPiece[GRID_SIZE][GRID_SIZE];
    }

    private ChessPiece[][] grid;

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        grid[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return grid[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Remove a chess piece from the chessboard
     *
     * @param position The position to remove the piece from
     */
    public void removePiece(ChessPosition position) {
        grid[position.getRow() - 1][position.getColumn() - 1] = null;
    }

    /**
     * Check if piece exists at given position
     *
     * @param position square to check
     * @return true if a chess piece lies on the given position
     */
    public boolean hasPieceAtPos(ChessPosition position) {
        return (position.isInBounds() && this.grid[position.getRow() - 1][position.getColumn() - 1] != null);
    }

    /**
     * Check if piece exists at given position. Overload for row/column integer parameters
     * @param row row to check
     * @param column column to check
     * @return true if a chess piece lies on the given coordinates
     */
    public boolean hasPieceAtPos(int row, int column) {
        return hasPieceAtPos(new ChessPosition(row, column));
    }

    /**
     * Check
     *
     * @param position square to check
     * @param color team color to compare against
     * @return true if space is occupied by opposing team of given team
     */
    public boolean hasRivalAtPos(ChessPosition position, ChessGame.TeamColor color) {
        if (!hasPieceAtPos(position)) {
            return false;
        }
        return !getPiece(position).getTeamColor().equals(color);
    }

    /**
     * Set up the white team chess pieces on the first two rows
     */
    private void setUpWhitePieces() {
        grid[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        grid[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        grid[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        grid[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        grid[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        grid[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        grid[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        grid[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
    }

    /**
     * Set up the black team chess pieces on the last two rows
     */
    private void setUpBlackPieces() {
        grid[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        grid[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        grid[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        grid[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        grid[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        grid[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        grid[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        grid[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
    }

    /**
     * Set up the pawns for both teams
     */
    private void setUpPawns() {
        Arrays.fill(grid[1], new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        Arrays.fill(grid[6], new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
    }

    /**
     * Clear the middle four rows of the chessboard
     */
    private void clearMiddleRows() {
        for (int r = 2; r < GRID_SIZE - 2; r++) {
            Arrays.fill(grid[r], null);
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        clearMiddleRows();
        setUpWhitePieces();
        setUpBlackPieces();
        setUpPawns();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(grid, that.grid);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(grid);
    }
}
