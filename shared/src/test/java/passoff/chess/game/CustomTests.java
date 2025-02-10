package passoff.chess.game;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import passoff.chess.TestUtilities;

public class CustomTests {


    @Test
    @DisplayName("White in Check diagonally")
    public void whiteCheckDiagonally() {
        ChessGame game = new ChessGame();
        game.setBoard(TestUtilities.loadBoard("""
                | | | | | | | |k|
                | | | | | |b| | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | |r| | |
                | |K| | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                """));
        game.setTeamTurn(ChessGame.TeamColor.WHITE);
        Assertions.assertTrue(game.isInCheck(ChessGame.TeamColor.WHITE));
    }

    @Test
    @DisplayName("Black king surrounded, but safe")
    public void blackSurroundedButNotInCheck() {
        ChessGame game = new ChessGame();
        game.setBoard(TestUtilities.loadBoard("""
                | | | | | | | | |
                | | | | | | | | |
                | | |N|B|P| | | |
                | | |P|k|N| | | |
                | | |R|B|R| | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                """));
        game.setTeamTurn(ChessGame.TeamColor.BLACK);
        Assertions.assertFalse(game.isInCheck(ChessGame.TeamColor.BLACK));
    }

    @Test
    @DisplayName("Test clone functionality of ChessBoard")
    public void makeCloneOfBoard() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        ChessBoard copyBoard = board.copy();
        Assertions.assertEquals(board, copyBoard); // Boards should be identical

        ChessPiece extraPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        copyBoard.addPiece(new ChessPosition(5, 5), extraPiece);
        copyBoard.removePiece(new ChessPosition(2, 5));
        Assertions.assertNotEquals(board, copyBoard); // Boards are no longer identical
        Assertions.assertNotSame(board, copyBoard);

    }

    @Test
    @DisplayName("Black in check by pawn")
    public void blackCheckByPawn() {
        ChessGame game = new ChessGame();
        game.setBoard(TestUtilities.loadBoard("""
                | | | | | | | | |
                | | |k| | | | | |
                | | | |P|b| | | |
                | | | | | | | | |
                | | | | | |r| | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                """));
        game.setTeamTurn(ChessGame.TeamColor.BLACK);
        Assertions.assertTrue(game.isInCheck(ChessGame.TeamColor.BLACK));
    }

    @Test
    @DisplayName("White in check by pawn")
    public void whiteCheckByPawn() {
        ChessGame game = new ChessGame();
        game.setBoard(TestUtilities.loadBoard("""
                | | | | | | | | |
                | | | | | | | | |
                | | | | |b| | | |
                | | | | | | | | |
                | | | | | |R| | |
                | | |p| | | | | |
                | | | |K| | | | |
                | | | | | | | | |
                """));
        game.setTeamTurn(ChessGame.TeamColor.WHITE);
        Assertions.assertTrue(game.isInCheck(ChessGame.TeamColor.WHITE));
    }
}
