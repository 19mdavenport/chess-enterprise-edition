package chess.factories.piecemoves;

import chess.strategies.piecemoves.KnightPieceMovesStrategy;
import util.AbstractFactory;

public class KnightPieceMovesStrategyFactory extends AbstractFactory<KnightPieceMovesStrategy> {

    public KnightPieceMovesStrategyFactory() {
        super(new KnightPieceMovesStrategy());
    }
}
