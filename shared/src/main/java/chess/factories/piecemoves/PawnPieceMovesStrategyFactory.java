package chess.factories.piecemoves;

import chess.strategies.piecemoves.PawnPieceMovesStrategy;

public class PawnPieceMovesStrategyFactory extends BasePieceMovesStrategyFactory<PawnPieceMovesStrategy> {

    public PawnPieceMovesStrategyFactory() {
        super(new PawnPieceMovesStrategy());
    }
}
