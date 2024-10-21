package chess.strategies.extrarules.castling;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.PieceType;
import chess.observers.BoardSetObserver;

public class CastlingBoardSetObserver implements BoardSetObserver {
    private final CastlingRules rules;

    public CastlingBoardSetObserver(CastlingRules rules) {this.rules = rules;}

    @Override
    public void setBoard(ChessBoard board) {
        boolean[] castlingOptions = rules.getCastlingOptions();
        ChessPiece whiteKing = board.getPiece(new ChessPosition(1, 5));
        ChessPiece whiteRookK = board.getPiece(new ChessPosition(1, 8));
        ChessPiece whiteRookQ = board.getPiece(new ChessPosition(1, 1));

        if (whiteKing == null || whiteKing.getPieceType() != PieceType.KING) {
            castlingOptions[0] = false;
            castlingOptions[1] = false;
        } else {
            castlingOptions[0] = (whiteRookK != null && whiteRookK.getPieceType() == PieceType.ROOK);
            castlingOptions[1] = (whiteRookQ != null && whiteRookQ.getPieceType() == PieceType.ROOK);
        }

        ChessPiece blackKing = board.getPiece(new ChessPosition(8, 5));
        ChessPiece blackRookK = board.getPiece(new ChessPosition(8, 8));
        ChessPiece blackRookQ = board.getPiece(new ChessPosition(8, 1));

        if (blackKing == null || blackKing.getPieceType() != PieceType.KING) {
            castlingOptions[2] = false;
            castlingOptions[3] = false;
        } else {
            castlingOptions[2] = (blackRookK != null && blackRookK.getPieceType() == PieceType.ROOK);
            castlingOptions[3] = (blackRookQ != null && blackRookQ.getPieceType() == PieceType.ROOK);
        }

        rules.setCastlingOptions(castlingOptions);
    }
}
