package chess.factories.piecemoves;

import chess.strategies.piecemoves.RookPieceMovesStrategy;
import util.AbstractFactory;

public class RookPieceMovesStrategyFactory extends AbstractFactory<RookPieceMovesStrategy> {

    public RookPieceMovesStrategyFactory() {
        super(new RookPieceMovesStrategy());
    }
}
