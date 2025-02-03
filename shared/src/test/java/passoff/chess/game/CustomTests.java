package passoff.chess.game;

import chess.ChessGame;
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
                | | | | | | | | |
                | | | | |b| | | |
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
