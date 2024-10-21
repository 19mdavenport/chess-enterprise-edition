package chess.factories.piecemoves;

import chess.strategies.piecemoves.QueenPieceMovesStrategy;
import util.AbstractFactory;

public class QueenPieceMovesStrategyFactory extends AbstractFactory<QueenPieceMovesStrategy> {

    public QueenPieceMovesStrategyFactory() {
        super(new QueenPieceMovesStrategy());
    }
}
