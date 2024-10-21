package chess.factories.piecemoves;

import chess.strategies.piecemoves.QueenPieceMovesStrategy;

public class QueenPieceMovesStrategyFactory extends BasePieceMovesStrategyFactory<QueenPieceMovesStrategy> {

    public QueenPieceMovesStrategyFactory() {
        super(new QueenPieceMovesStrategy());
    }
}
