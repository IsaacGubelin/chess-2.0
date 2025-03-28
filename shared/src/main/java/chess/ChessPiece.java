package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import chess.moves.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * Private data
     */
    final private ChessGame.TeamColor color;
    private PieceType type;


    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to.
     * Does not take into account moves that are illegal due to leaving the king in
     * danger.
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();

        switch (board.getPiece(myPosition).getPieceType()) {
            case ROOK -> {
                RookMovesCalc rookCalc = new RookMovesCalc();
                moves.addAll(rookCalc.getRookMoves(board, myPosition));
            }
            case KNIGHT -> {
                KnightMovesCalc knightCalc = new KnightMovesCalc();
                moves.addAll(knightCalc.getKnightMoves(board, myPosition));
            }
            case BISHOP -> {
                BishopMovesCalc bishopCalc = new BishopMovesCalc();
                moves.addAll(bishopCalc.getBishopMoves(board, myPosition));
            }
            case QUEEN -> {
                QueenMovesCalc queenCalc = new QueenMovesCalc();
                moves.addAll(queenCalc.getQueenMoves(board, myPosition));
            }
            case KING -> {
                KingMovesCalc kingCalc = new KingMovesCalc();
                moves.addAll(kingCalc.getKingMoves(board, myPosition));
            }
            case PAWN -> {
                PawnMovesCalc pawnCalc = new PawnMovesCalc();
                moves.addAll(pawnCalc.getPawnMoves(board, myPosition));
            }
        }
        return moves;
    }

    public ChessPiece copy() {
        return new ChessPiece(color, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "color=" + color +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }
}
