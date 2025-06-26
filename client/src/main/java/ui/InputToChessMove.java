package ui;

import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;

/**
 * This class gives static methods for turning string into a chess position or move.
 */
public class InputToChessMove {

    /**
     * Takes in a string and returns a chess position
     * @param input string input from player
     * @return ChessPosition
     * @throws InvalidMoveException
     */
    public static ChessPosition getPositionFromString(String input) throws InvalidMoveException {
        if (input.length() != 2) {
            throw new InvalidMoveException("Input string gives invalid position.");
        }
        input = input.toUpperCase();        // Convert any lowercase letters to uppercase
        char columnChar = input.charAt(0);  // Collect column character
        char rowChar = input.charAt(1);     // Collect row number
        int col = columnChar - 'A' + 1;     // Convert column character to integer (A -> 1, B -> 2, ..., H -> 8)
        int row = Character.getNumericValue(rowChar); // Convert row character to integer
        if (row < 1 || row > 8 || col < 1 || col > 8) {
            throw new InvalidMoveException("Requested position is out of bounds");
        }
        return new ChessPosition(row, col);
    }

    public static ChessMove getMoveFromString(String input) throws InvalidMoveException {
        if (input.length() != 4) {
            throw new InvalidMoveException("Input string is invalid");
        }
        input = input.toUpperCase();            // Make letters uppercase if needed
        char columnCharOld = input.charAt(0);   // Collect column character of current position
        char rowCharOld = input.charAt(1);      // Collect row number of current position
        char columnCharNew = input.charAt(2);   // Collect end position column
        char rowCharNew = input.charAt(3);      // collect end position row
        // Convert to integers
        int colOld = columnCharOld - 'A' + 1;     // Convert column character to integer (A -> 1, B -> 2, ..., H -> 8)
        int rowOld = Character.getNumericValue(rowCharOld); // Convert row character to integer
        int colNew = columnCharNew - 'A' + 1;     // Do same with new position row and column
        int rowNew = Character.getNumericValue(rowCharNew);
        if (rowOld < 1 || rowOld > 8 || colOld < 1 || colOld > 8) { // Check to make sure old position is in bounds
            throw new InvalidMoveException("Given current position is out of bounds.");
        } else if (rowNew < 1 || rowNew > 8 || colNew < 1 || colNew > 8) {  // Check new position bounds
            throw new InvalidMoveException("Requested position out of bounds.");
        }
        return new ChessMove(new ChessPosition(rowOld, colOld), new ChessPosition(rowNew, colNew)); // Return move
    }
}
