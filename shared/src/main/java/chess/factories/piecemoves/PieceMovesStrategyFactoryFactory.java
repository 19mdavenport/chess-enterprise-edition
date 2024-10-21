package chess.factories.piecemoves;

import chess.ChessPiece;
import chess.strategies.piecemoves.PieceMovesStrategy;
import util.BaseFactory;
import util.Factory;

public class PieceMovesStrategyFactoryFactory extends BaseFactory<Factory<? extends PieceMovesStrategy>> {

    protected PieceMovesStrategyFactoryFactory(ChessPiece.PieceType type) {
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
