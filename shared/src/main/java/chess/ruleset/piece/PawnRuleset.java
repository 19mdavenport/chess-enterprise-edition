package chess.ruleset.piece;

import chess.*;

import java.util.Collection;
import java.util.HashSet;

public class PawnRuleset implements PieceRuleset {


    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessGame.TeamColor teamColor = board.getPiece(myPosition).getTeamColor();
        Collection<ChessMove> moves = new HashSet<>();

        int single =
                myPosition.getRow() + ((teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1); //Direction pawn is going

        ChessPosition oneSpaceMove = new ChessPosition(single, myPosition.getColumn());
        if (board.getPiece(oneSpaceMove) == null) {
            moves.add(new ChessMove(myPosition, oneSpaceMove));

            //If it's still on the starting line, can move 2 spaces
            if ((teamColor == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2) ||
                    (teamColor == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7)) {
                ChessPosition twoSpaceMove =
                        new ChessPosition(2 * single - myPosition.getRow(), myPosition.getColumn());
                if (board.getPiece(twoSpaceMove) == null) {
                    moves.add(new ChessMove(myPosition, twoSpaceMove));
                }
            }
        }

        //If piece of opposing color up & left, take is valid
        if (myPosition.getColumn() > 1) {
            ChessPosition takeLeft = new ChessPosition(single, myPosition.getColumn() - 1);
            ChessPiece takeLeftPiece = board.getPiece(takeLeft);
            if (takeLeftPiece != null && takeLeftPiece.getTeamColor() != teamColor) {
                moves.add(new ChessMove(myPosition, takeLeft));
            }
        }
        //If piece of opposing color up & right, take is valid
        if (myPosition.getColumn() < 8) {
            ChessPosition takeRight = new ChessPosition(single, myPosition.getColumn() + 1);
            ChessPiece takeRightPiece = board.getPiece(takeRight);
            if (takeRightPiece != null && takeRightPiece.getTeamColor() != teamColor) {
                moves.add(new ChessMove(myPosition, takeRight));
            }
        }


        if (single == 1 || single == 8) {
            //Go through all the moves, replace moves that end on edge with promotion moves
            Collection<ChessMove> promotions = new HashSet<>();
            for (ChessMove move : moves) {
                promotions.add(
                        new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.QUEEN));
                promotions.add(
                        new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.ROOK));
                promotions.add(
                        new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.BISHOP));
                promotions.add(
                        new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.KNIGHT));
            }
            return promotions;

        }
        return moves;
    }

}
