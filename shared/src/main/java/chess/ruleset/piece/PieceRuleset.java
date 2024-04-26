package chess.ruleset.piece;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public interface PieceRuleset {

    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

}
