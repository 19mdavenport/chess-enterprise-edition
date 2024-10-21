package chess.factories.piecemoves;

import chess.ChessPiece;
import util.BaseFactory;

public class PieceMovesStrategyFactoryFactory extends BaseFactory<PieceMovesStrategyFactory<?>> {

    public PieceMovesStrategyFactoryFactory(ChessPiece.PieceType type) {
        super(switch (type) {
            case KING -> new KingPieceMovesStrategyFactory();
            case QUEEN -> new QueenPieceMovesStrategyFactory();
            case BISHOP -> new BishopPieceMovesStrategyFactory();
            case KNIGHT -> new KnightPieceMovesStrategyFactory();
            case ROOK -> new RookPieceMovesStrategyFactory();
            case PAWN -> new PawnPieceMovesStrategyFactory();
        });
    }

    
}
