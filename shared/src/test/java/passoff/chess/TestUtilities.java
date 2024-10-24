package passoff.chess;

import chess.*;
import org.junit.jupiter.api.Assertions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestUtilities {
    static public void validateMoves(String boardText, ChessPosition startPosition, int[][] endPositions) {
        var board = loadBoard(boardText);
        var testPiece = board.getPiece(startPosition);
        var validMoves = loadMoves(startPosition, endPositions);
        validateMoves(board, testPiece, startPosition, validMoves);
    }

    static public void validateMoves(ChessBoard board, ChessPiece testPiece, ChessPosition startPosition, Set<ChessMove> validMoves) {
        var pieceMoves = new HashSet<>(testPiece.pieceMoves(board, startPosition));
        assertCollectionsEquals(validMoves, pieceMoves, "Wrong moves");
    }

    static public <T> void assertCollectionsEquals(Collection<T> first, Collection<T> second, String message) {
        Assertions.assertEquals(new HashSet<>(first), new HashSet<>(second), message);
        Assertions.assertEquals(first.size(), second.size(), "Collections not the same size");
    }

    final static Map<Character, PieceType> CHAR_TO_TYPE_MAP = Map.of(
            'p', PieceType.PAWN,
            'n', PieceType.KNIGHT,
            'r', PieceType.ROOK,
            'q', PieceType.QUEEN,
            'k', PieceType.KING,
            'b', PieceType.BISHOP);

    public static ChessBoard loadBoard(String boardText) {
        var board = new ChessBoard();
        int row = 8;
        int column = 1;
        for (var c : boardText.toCharArray()) {
            switch (c) {
                case '\n' -> {
                    column = 1;
                    row--;
                }
                case ' ' -> column++;
                case '|' -> {
                }
                default -> {
                    TeamColor color = Character.isLowerCase(c) ? TeamColor.BLACK
                            : TeamColor.WHITE;
                    var type = CHAR_TO_TYPE_MAP.get(Character.toLowerCase(c));
                    var position = new ChessPosition(row, column);
                    var piece = new ChessPiece(color, type);
                    board.addPiece(position, piece);
                    column++;
                }
            }
        }
        return board;
    }

    public static ChessBoard defaultBoard() {
        return loadBoard("""
                |r|n|b|q|k|b|n|r|
                |p|p|p|p|p|p|p|p|
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                |P|P|P|P|P|P|P|P|
                |R|N|B|Q|K|B|N|R|
                """);
    }

    public static Set<ChessMove> loadMoves(ChessPosition startPosition, int[][] endPositions) {
        var validMoves = new HashSet<ChessMove>();
        for (var endPosition : endPositions) {
            validMoves.add(new ChessMove(startPosition,
                    new ChessPosition(endPosition[0], endPosition[1]), null));
        }
        return validMoves;
    }
}
