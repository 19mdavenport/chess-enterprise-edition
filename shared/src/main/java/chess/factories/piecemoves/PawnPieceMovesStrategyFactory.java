package chess.factories.piecemoves;

import chess.strategies.piecemoves.PawnPieceMovesStrategy;
import util.AbstractFactory;

public class PawnPieceMovesStrategyFactory extends AbstractFactory<PawnPieceMovesStrategy> {

    public PawnPieceMovesStrategyFactory() {
        super(new PawnPieceMovesStrategy());
    }
}
