package chess.strategies.validmoves;

import chess.*;
import chess.extrarules.ExtraRuleset;
import chess.factories.performmove.MovePerformanceStrategyFactory;
import chess.strategies.performmove.MovePerformanceStrategy;

import java.util.ArrayList;
import java.util.Collection;

public class NormalValidMovesStrategy implements ValidMovesStrategy {
    private final Collection<ExtraRuleset> extraRules;

    public NormalValidMovesStrategy(Collection<ExtraRuleset> extraRules) {
        this.extraRules = new ArrayList<>(extraRules);
    }

    @Override
    @SuppressWarnings("ReturnOfNull")
    public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);

        if (piece == null) {
            return null;
        }

        //Get all possible moves for the piece
        Collection<ChessMove> moves = piece.pieceMoves(board, position);

        for (ExtraRuleset extraRuleset : extraRules) {
            moves.addAll(extraRuleset.getValidMovesStrategy().validMoves(board, position));
        }

        //If making the move resulted in the king being placed in check, it's not legal
        moves.removeIf(move -> isMoveInvalid(move, board));
        return moves;
    }

    private boolean isMoveInvalid(ChessMove move, ChessBoard board) {
        try {
            ChessBoard copyBoard = new ChessBoard(board);
            ChessPiece movingPiece = copyBoard.getPiece(move.getStartPosition());
            MovePerformanceStrategyFactory factory = new MovePerformanceStrategyFactory(extraRules, move, board);
            MovePerformanceStrategy strategy = factory.get();
            strategy.performMove(move, copyBoard);
            return ChessGame.isInCheck(movingPiece.getTeamColor(), copyBoard);
        } catch (InvalidMoveException e) {
            return true;
        }
    }
}
