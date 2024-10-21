package chess.strategies.extrarules.castling;

import chess.*;
import chess.observers.BoardSetObserver;
import chess.strategies.extrarules.ExtraRuleset;
import chess.strategies.performmove.MovePerformanceStrategy;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public class CastlingRules implements ExtraRuleset {

    private boolean[] castlingOptions;

    public CastlingRules() {
        castlingOptions = new boolean[4];
        Arrays.fill(castlingOptions, true);
    }

    boolean[] getCastlingOptions() {
        return castlingOptions.clone();
    }

    void setCastlingOptions(boolean[] castlingOptions) {
        this.castlingOptions = castlingOptions.clone();
    }

    @Override
    public MovePerformanceStrategy getMovePerformanceStrategy() {
        return new CastlingMovePerformanceStrategy();
    }

    @Override
    public BoardSetObserver getBoardSetObserver() {
        return new CastlingBoardSetObserver(this);
    }

    @Override
    public boolean isMoveMatch(ChessMove move, ChessBoard board) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        return (piece.getPieceType() == PieceType.KING &&
                Math.abs(move.getStartPosition().getColumn() - move.getEndPosition().getColumn()) == 2);
    }

    public void moveMade(ChessMove move, ChessBoard board) {
        ChessPiece piece = board.getPiece(move.getEndPosition());
        if (piece.getPieceType() == PieceType.KING) {
            if (piece.getTeamColor() == TeamColor.WHITE) {
                castlingOptions[0] = false;
                castlingOptions[1] = false;
            } else {
                castlingOptions[2] = false;
                castlingOptions[3] = false;
            }
        } else if (piece.getPieceType() == PieceType.ROOK) {
            ChessPosition startPos = move.getStartPosition();
            if (castlingOptions[0] && startPos.getRow() == 1 && startPos.getColumn() == 8) {
                castlingOptions[0] = false;
            }
            if (castlingOptions[1] && startPos.getRow() == 1 && startPos.getColumn() == 1) {
                castlingOptions[1] = false;
            }
            if (castlingOptions[2] && startPos.getRow() == 8 && startPos.getColumn() == 8) {
                castlingOptions[2] = false;
            }
            if (castlingOptions[3] && startPos.getRow() == 8 && startPos.getColumn() == 1) {
                castlingOptions[3] = false;
            }
        }
    }

    public void setBoard(ChessBoard board) {

    }

    public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> ret = new HashSet<>();

        ChessPiece piece = board.getPiece(position);

        if (piece == null || piece.getPieceType() != PieceType.KING || position.getColumn() != 5 ||
                ((piece.getTeamColor() == TeamColor.WHITE && position.getRow() != 1) ||
                        (piece.getTeamColor() == TeamColor.BLACK && position.getRow() != 8))) {
            return ret;
        }

        int offset = piece.getTeamColor() == TeamColor.WHITE ? 0 : 2;

        if (castlingOptions[offset]) {
            Optional<ChessMove> kingSideCastle = singleCastlingMove(board, piece.getTeamColor(), true);
            kingSideCastle.ifPresent(ret::add);
        }
        if (castlingOptions[offset + 1]) {
            Optional<ChessMove> queenSideCastle = singleCastlingMove(board, piece.getTeamColor(), false);
            queenSideCastle.ifPresent(ret::add);
        }

        return ret;
    }

    private Optional<ChessMove> singleCastlingMove(ChessBoard board, TeamColor color, boolean kingSide) {
        if (ChessGame.isInCheck(color, board)) {
            return Optional.empty();
        }

        int row = (color == TeamColor.WHITE) ? 1 : 8;
        int col = (kingSide) ? 6 : 4;

        for (int i = col; i < 8 && i > 1; i += col - 5) {
            if (board.getPiece(new ChessPosition(row, i)) != null) {
                return Optional.empty();
            }
        }

        ChessPosition orig = new ChessPosition(row, 5);
        ChessBoard betweenBoard = new ChessBoard(board);
        betweenBoard.addPiece(new ChessPosition(row, col), betweenBoard.getPiece(orig));
        betweenBoard.addPiece(orig, null);
        if (ChessGame.isInCheck(color, betweenBoard)) {
            return Optional.empty();
        }

        ChessPosition end = new ChessPosition(row, 5 + 2 * (col - 5));
        ChessMove out = new ChessMove(orig, end);
        ChessBoard outBoard = new ChessBoard(board);
        try {
            getMovePerformanceStrategy().performMove(out, outBoard);
        } catch (InvalidMoveException e) {
            return Optional.empty();
        }
        return Optional.ofNullable((ChessGame.isInCheck(color, outBoard)) ? null : out);
    }


    @Override
    public int hashCode() {
        return Arrays.hashCode(castlingOptions);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CastlingRules obj1 = (CastlingRules) obj;
        return Arrays.equals(castlingOptions, obj1.castlingOptions);
    }


    @Override
    public String toString() {
        return "%s%s%s%s".formatted(castlingOptions[0] ? 'K' : '-', castlingOptions[1] ? 'Q' : '-',
                castlingOptions[2] ? 'k' : '-', castlingOptions[3] ? 'q' : '-');
    }

}
