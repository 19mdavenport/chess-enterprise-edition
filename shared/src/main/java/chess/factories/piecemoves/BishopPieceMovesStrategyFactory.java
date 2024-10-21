package chess.factories.piecemoves;

import chess.strategies.piecemoves.BishopPieceMovesStrategy;

public class BishopPieceMovesStrategyFactory extends BasePieceMovesStrategyFactory<BishopPieceMovesStrategy> {

    public BishopPieceMovesStrategyFactory() {
        super(new BishopPieceMovesStrategy());
    }
}