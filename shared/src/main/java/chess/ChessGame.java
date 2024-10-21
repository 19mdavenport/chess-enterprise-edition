package chess;

import chess.factories.performmove.MovePerformanceStrategyFactory;
import chess.strategies.extra.CastlingRules;
import chess.strategies.extra.EnPassantRules;
import chess.strategies.extra.ExtraRuleset;
import chess.strategies.performmove.MovePerformanceStrategy;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private final List<ExtraRuleset> extraRules;

    private boolean active;

    private ChessBoard board;

    private TeamColor teamTurn;

    public ChessGame() {
        board = new ChessBoard();
        teamTurn = TeamColor.WHITE;

        board.resetBoard();
        active = true;
        extraRules = List.of(new CastlingRules(), new EnPassantRules());
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        for (ExtraRuleset extraRuleset : extraRules) {
            extraRuleset.setBoard(board);
        }
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    @Override
    public int hashCode() {
        int result = board != null ? board.hashCode() : 0;
        result = 31 * result + (teamTurn != null ? teamTurn.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) obj;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public String toString() {
        return String.format("%s %s", board.toString(), (teamTurn == TeamColor.WHITE) ? 'w' : 'b');
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(teamColor, board);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && isTeamTrapped(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && isTeamTrapped(teamColor);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());

        //If there is no piece at the starting location or if said piece is the wrong color, the move is invalid
        if (piece == null) {
            throw new InvalidMoveException("No piece at starting position");
        } else if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Piece of wrong color for turn");
        } else {
            //If the piece can't actually make that move, it's invalid
            Collection<ChessMove> moves = validMoves(move.getStartPosition());
            if (!moves.contains(move)) {
                throw new InvalidMoveException("Not a valid move");
            }
        }

        performMove(move, board);

        for (ExtraRuleset extraRuleset : extraRules) {
            extraRuleset.moveMade(move, board);
        }

        teamTurn = teamTurn.getOpposite();
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    @SuppressWarnings("ReturnOfNull")
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            return null;
        }

        //Get all possible moves for the piece
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);

        for (ExtraRuleset extraRuleset : extraRules) {
            moves.addAll(extraRuleset.validMoves(board, startPosition));
        }

        //If making the move resulted in the king being placed in check, it's not legal
        moves.removeIf(move -> isMoveInvalid(move, board));
        return moves;
    }

    private boolean isMoveInvalid(ChessMove move, ChessBoard board) {
        try {
            ChessBoard copyBoard = new ChessBoard(board);
            ChessPiece movingPiece = copyBoard.getPiece(move.getStartPosition());
            performMove(move, copyBoard);
            return isInCheck(movingPiece.getTeamColor(), copyBoard);
        } catch (InvalidMoveException e) {
            return true;
        }
    }

    public static boolean isInCheck(TeamColor teamColor, ChessBoard board) {
        ChessPosition king = null;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == PieceType.KING) {
                    king = pos;
                    break;
                }
            }
        }

        if (king == null) {
            return false;
        }

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                if (isKingThreatened(new ChessPosition(i, j), board, king, teamColor)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isKingThreatened(ChessPosition position, ChessBoard board, ChessPosition kingPosition,
                                            TeamColor teamColor) {
        ChessPiece piece = board.getPiece(position);
        if (piece != null && piece.getTeamColor() != teamColor) {
            for (ChessMove move : piece.pieceMoves(board, position)) {
                if (move.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void performMove(ChessMove move, ChessBoard board) throws InvalidMoveException {
        MovePerformanceStrategyFactory factory = new MovePerformanceStrategyFactory(extraRules, move, board);
        MovePerformanceStrategy strategy = factory.get();
        strategy.performMove(move, board);
    }

    private boolean isTeamTrapped(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (!validMoves(pos).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
