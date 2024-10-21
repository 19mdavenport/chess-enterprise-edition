package chess.factories.piecemoves;

import chess.strategies.piecemoves.PawnPieceMovesStrategy;
import util.BaseFactory;

public class PawnPieceMovesStrategyFactory extends BaseFactory<PawnPieceMovesStrategy> {

    public PawnPieceMovesStrategyFactory() {
        super(new PawnPieceMovesStrategy());
    }
}
