package chess.factories.piecemoves;

import chess.strategies.piecemoves.KingPieceMovesStrategy;

public class KingPieceMovesStrategyFactory extends BasePieceMovesStrategyFactory<KingPieceMovesStrategy> {

    public KingPieceMovesStrategyFactory() {
        super(new KingPieceMovesStrategy());
    }
}