package chess.factories.piecemoves;

import chess.strategies.piecemoves.KingPieceMovesStrategy;
import util.BaseFactory;

public class KingPieceMovesStrategyFactory extends BaseFactory<KingPieceMovesStrategy> {

    public KingPieceMovesStrategyFactory() {
        super(new KingPieceMovesStrategy());
    }
}