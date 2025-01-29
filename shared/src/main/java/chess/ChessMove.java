package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.start = startPosition;
        this.end = endPosition;
        this.promotionType = promotionPiece;
    }

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition) {
        this.start = startPosition;
        this.end = endPosition;
        this.promotionType = null;
    }

    /**
     * Private data
     */
    private ChessPosition start;
    private ChessPosition end;
    private ChessPiece.PieceType promotionType;

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return this.start;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return this.end;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return this.promotionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return start.equals(chessMove.start) && end.equals(chessMove.end) && promotionType == chessMove.promotionType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, promotionType);
    }
}

