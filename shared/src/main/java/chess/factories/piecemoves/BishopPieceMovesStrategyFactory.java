package chess.factories.piecemoves;

import chess.strategies.piecemoves.BishopPieceMovesStrategy;
import util.BaseFactory;

public class BishopPieceMovesStrategyFactory extends BaseFactory<BishopPieceMovesStrategy> {

    public BishopPieceMovesStrategyFactory() {
        super(new BishopPieceMovesStrategy());
    }
}