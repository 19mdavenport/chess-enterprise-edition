package chess.factories.piecemoves;

import chess.strategies.piecemoves.KingPieceMovesStrategy;
import util.AbstractFactory;

public class KingPieceMovesStrategyFactory extends AbstractFactory<KingPieceMovesStrategy> {

    public KingPieceMovesStrategyFactory() {
        super(new KingPieceMovesStrategy());
    }
}