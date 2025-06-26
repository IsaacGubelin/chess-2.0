package ui;

import static ui.EscapeSequences.*;
import chess.*;
import java.util.HashSet;

/**
 * ChessBoardPrint.java created by Isaac Gubelin on February 9, 2024
 * <p>
 *     This class contains a method that prints the layout of a given chessboard.
 * </p>
 */
public class BoardDisplay {

    // Maximum row/column dimension
    private static final int OUT_BOUNDS = 9;

    // Unicode constant chars for printing chess pieces
    private static final char WHITE_KING   = '\u2654';
    private static final char WHITE_QUEEN  = '\u2655';
    private static final char WHITE_ROOK   = '\u2656';
    private static final char WHITE_BISHOP = '\u2657';
    private static final char WHITE_KNIGHT = '\u2658';
    private static final char WHITE_PAWN   = '\u2659';
    private static final char BLACK_KING   = '\u265A';
    private static final char BLACK_QUEEN  = '\u265B';
    private static final char BLACK_ROOK   = '\u265C';
    private static final char BLACK_BISHOP = '\u265D';
    private static final char BLACK_KNIGHT = '\u265E';
    private static final char BLACK_PAWN   = '\u265f';

    private static final String UNICODE_ESCAPE = "\u001b";  // Used for setting background color


    // Get just the letter for the chess piece
    // K is used for king, N used for knight
    private static char getPieceLetter(ChessPiece piece) {

        return switch (piece.getPieceType()) { // Automatically sets piece character to correct unicode character and color.
            case KING -> '♔';
            case QUEEN -> '♕';
            case BISHOP -> '♗';
            case KNIGHT -> '♘';
            case ROOK -> '♖';
            case PAWN -> '♙';
            default -> '\u2003';
        };
    }

    /**
     * Prints the chessboard with all pieces in their current positions. Boolean toggles team perspective
     * @param board ChessBoard object to print
     * @param isWhitePieceSide Boolean to determine perspective of board
     */
    public static void printChessBoard(ChessBoard board, boolean isWhitePieceSide, ChessPosition currentPos) {

        // Determine the direction of iteration based on the perspective
        int startRow = isWhitePieceSide ? 8 : 1;
        int endRow = isWhitePieceSide ? 0 : 9;
        int rowChange = isWhitePieceSide ? -1 : 1;

        HashSet<ChessPosition> endPlaces = new HashSet<>(); // Keep track of end moves to highlight on board
        if (currentPos != null) {                       // If collection of ChessMoves parameter was given
            ChessGame tempGame = new ChessGame();       // Make temporary game object
            tempGame.setBoard(board);                   // Apply current state of board to temporary chess object
            HashSet<ChessMove> highlightMoves = (HashSet<ChessMove>) tempGame.validMoves(currentPos); // Get moves
            for (ChessMove move : highlightMoves) {     // Make a list of all end positions to highlight
                endPlaces.add(move.getEndPosition());
            }
        }

        // Print the column labels based on perspective
        if (isWhitePieceSide) {
            System.out.println(SET_TEXT_COLOR_DARK_GREY + SET_BG_COLOR_LIGHT_GREY +
                    "    a  b  c  d  e  f  g  h    " + SET_BG_COLOR_BLACK);
        } else {
            System.out.println(SET_TEXT_COLOR_DARK_GREY + SET_BG_COLOR_LIGHT_GREY +
                    "    h  g  f  e  d  c  b  a    " + SET_BG_COLOR_BLACK);
        }

        // Go through every row in the board
        for (int row = startRow; row != endRow; row += rowChange) {
            // print row number on chessboard border
            System.out.print(SET_TEXT_COLOR_DARK_GREY + SET_BG_COLOR_LIGHT_GREY + " " + row + " ");

            // Determine the direction of iteration for columns based on perspective
            int startCol = isWhitePieceSide ? 1 : 8;
            int endCol = isWhitePieceSide ? 9 : 0;
            int colChange = isWhitePieceSide ? 1 : -1;
            // Go through all columns in row
            for (int col = startCol; col != endCol; col += colChange) {
                if (endPlaces.contains(new ChessPosition(row, col))) {          // If position is an available move
                    setBackgroundHighlightMove(row, col);                       // Highlight it on the board
                } else if (currentPos != null && row == currentPos.getRow() && col == currentPos.getColumn()) {
                    System.out.print(SET_BG_COLOR_YELLOW);    // If at current position of given piece, highlight
                } else {
                    setBackgroundToSquareColor(row, col);                       // Otherwise, use regular square color
                }
                if (board.hasPieceAtPos(row, col)) {                               // If there's a piece, print it
                    setTextToPieceColor(board.getPiece(new ChessPosition(row, col)).getTeamColor()); // Print text in correct color
                    System.out.print(" " + getPieceLetter(board.getPiece(new ChessPosition(row, col))) + " ");
                }
                else
                    System.out.print(" \u2009  ");   // Otherwise, print blank square
            }
            System.out.print(SET_TEXT_COLOR_DARK_GREY + SET_BG_COLOR_LIGHT_GREY + " " + row + " ");
            System.out.print(SET_BG_COLOR_BLACK + "\n");
        }
        // Print bottom border of board with column labels
        if (isWhitePieceSide) {
            System.out.println(SET_TEXT_COLOR_DARK_GREY + SET_BG_COLOR_LIGHT_GREY +
                    "    a  b  c  d  e  f  g  h    " + SET_BG_COLOR_BLACK);
        } else {
            System.out.println(SET_TEXT_COLOR_DARK_GREY + SET_BG_COLOR_LIGHT_GREY +
                    "    h  g  f  e  d  c  b  a    " + SET_BG_COLOR_BLACK);
        }
        System.out.println(RESET_BG_COLOR + SET_TEXT_COLOR_WHITE); // Reset text to usual color
    }

    // Same function, but accepts a team color as the second parameter
    public static void printChessBoard(ChessBoard board, ChessGame.TeamColor color) {
        boolean isWhitePieceSide = color.equals(ChessGame.TeamColor.WHITE);
        printChessBoard(board, isWhitePieceSide);
    }

    public static void printChessBoard(ChessBoard board, boolean isWhitePieceSide) {
        printChessBoard(board, isWhitePieceSide, null);
    }



    private static void setTextToPieceColor(ChessGame.TeamColor color) {
        if (color.equals(ChessGame.TeamColor.BLACK))
            System.out.print(SET_TEXT_COLOR_BLACK + SET_TEXT_BOLD);
        else
            System.out.print(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD);
    }

    private static void setBackgroundToSquareColor(int row, int col) {
        if ((row + col) % 2 == 0)                     // Check if square is odd or even for choosing color
            System.out.print(UNICODE_ESCAPE + "[48;5;222m");    // Creme yellow
        else
            System.out.print(UNICODE_ESCAPE + "[48;5;95m");     // Brown
    }

    private static void setBackgroundHighlightMove(int row, int col) {
        if ((row + col) % 2 == 0)                     // Check if square is odd or even for choosing color
            System.out.print(SET_BG_COLOR_GREEN);    // Creme yellow
        else
            System.out.print(SET_BG_COLOR_DARK_GREEN);     // Brown
    }

    //TODO:
    // Test the board drawer using this main method
    // Delete when development is done
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        ChessGame game = new ChessGame();

        printChessBoard(game.getBoard(), ChessGame.TeamColor.WHITE);
    }

}
