package chess.factories.piecemoves;

import chess.strategies.piecemoves.PieceMovesStrategy;
import util.BaseFactory;

public abstract class BasePieceMovesStrategyFactory<T extends PieceMovesStrategy> extends BaseFactory<T>
        implements PieceMovesStrategyFactory<T> {
    protected BasePieceMovesStrategyFactory(T t) {
        super(t);
    }
}
