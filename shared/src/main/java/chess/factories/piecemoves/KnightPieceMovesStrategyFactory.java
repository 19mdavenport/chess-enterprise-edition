package chess.factories.piecemoves;

import chess.strategies.piecemoves.KnightPieceMovesStrategy;
import util.BaseFactory;

public class KnightPieceMovesStrategyFactory extends BaseFactory<KnightPieceMovesStrategy> {

    public KnightPieceMovesStrategyFactory() {
        super(new KnightPieceMovesStrategy());
    }
}
