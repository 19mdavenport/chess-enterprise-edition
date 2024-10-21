package chess.factories.piecemoves;

import chess.strategies.piecemoves.RookPieceMovesStrategy;
import util.BaseFactory;

public class RookPieceMovesStrategyFactory extends BaseFactory<RookPieceMovesStrategy> {

    public RookPieceMovesStrategyFactory() {
        super(new RookPieceMovesStrategy());
    }
}
