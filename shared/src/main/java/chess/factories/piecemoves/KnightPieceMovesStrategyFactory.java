package chess.factories.piecemoves;

import chess.strategies.piecemoves.KnightPieceMovesStrategy;

public class KnightPieceMovesStrategyFactory extends BasePieceMovesStrategyFactory<KnightPieceMovesStrategy> {

    public KnightPieceMovesStrategyFactory() {
        super(new KnightPieceMovesStrategy());
    }
}
