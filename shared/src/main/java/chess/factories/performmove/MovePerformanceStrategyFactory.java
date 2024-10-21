package chess.factories.performmove;

import chess.ChessBoard;
import chess.ChessMove;
import chess.strategies.extra.ExtraRuleset;
import chess.strategies.performmove.MovePerformanceStrategy;
import chess.strategies.performmove.NormalMovePerformanceStrategy;
import chess.strategies.performmove.PromotionMovePerformanceStrategy;
import util.BaseFactory;

import java.util.Collection;

public class MovePerformanceStrategyFactory extends BaseFactory<MovePerformanceStrategy> {
    public MovePerformanceStrategyFactory(Collection<ExtraRuleset> extraRules, ChessMove move, ChessBoard board) {
        super(makeStrategy(extraRules, move, board));
    }

    private static MovePerformanceStrategy makeStrategy(Collection<ExtraRuleset> extraRules, ChessMove move,
                                                 ChessBoard board) {
        for (ExtraRuleset extraRuleset : extraRules) {
            if (extraRuleset.isMoveMatch(move, board)) {
                return extraRuleset.getMovePerformanceStrategy();
            }
        }
        return move.getPromotionPiece() == null ?
                new NormalMovePerformanceStrategy() :
                new PromotionMovePerformanceStrategy();
    }
}
