package chess.factories.piecemoves;

import chess.strategies.piecemoves.QueenPieceMovesStrategy;
import util.BaseFactory;

public class QueenPieceMovesStrategyFactory extends BaseFactory<QueenPieceMovesStrategy> {

    public QueenPieceMovesStrategyFactory() {
        super(new QueenPieceMovesStrategy());
    }
}
