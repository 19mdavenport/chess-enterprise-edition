package chess.factories.piecemoves;

import chess.strategies.piecemoves.PieceMovesStrategy;
import util.Factory;

public interface PieceMovesStrategyFactory<T extends PieceMovesStrategy> extends Factory<T> {
}
