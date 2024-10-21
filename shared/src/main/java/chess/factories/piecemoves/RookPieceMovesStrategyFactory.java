package chess.factories.piecemoves;

import chess.strategies.piecemoves.RookPieceMovesStrategy;

public class RookPieceMovesStrategyFactory extends BasePieceMovesStrategyFactory<RookPieceMovesStrategy> {

    public RookPieceMovesStrategyFactory() {
        super(new RookPieceMovesStrategy());
    }
}
