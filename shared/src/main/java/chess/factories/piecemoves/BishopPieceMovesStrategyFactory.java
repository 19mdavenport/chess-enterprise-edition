package chess.factories.piecemoves;

import chess.strategies.piecemoves.BishopPieceMovesStrategy;
import util.AbstractFactory;

public class BishopPieceMovesStrategyFactory extends AbstractFactory<BishopPieceMovesStrategy> {

    public BishopPieceMovesStrategyFactory() {
        super(new BishopPieceMovesStrategy());
    }
}