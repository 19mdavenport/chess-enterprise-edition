package passoff.chess.extracredit;

import chess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static passoff.chess.TestUtilities.loadBoard;

/**
 * Tests if the ChessGame implementation can handle Castling moves
 * Castling is a situational move the king can make as it's first move. If one of the rooks has not yet moved
 * and there are no pieces between the rook and the king, and the path is "safe", the king can castle. Castling is
 * performed by moving the king 2 spaces towards the qualifying rook, and the rook "jumping" the king to sit next
 * to the king on the opposite side it was previously. A path is considered "safe" if 1: the king is not in check
 * and 2: neither the space the king moves past nor the space the king ends up at can be reached by an opponents piece.
 */
public class CastlingTests {

    @Test
    @DisplayName("White Team Castle")
    public void castleWhite() {
        ChessBoard board = loadBoard("""
                | | | | |k| | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                |R| | | |K| | |R|
                """);
        ChessGame game = new ChessGame();
        game.setBoard(board);
        game.setTeamTurn(TeamColor.WHITE);

        //check that with nothing in way, king can castle
        ChessPosition kingPosition = new ChessPosition(1, 5);
        ChessMove queenSide = new ChessMove(kingPosition, new ChessPosition(1, 3), null);
        ChessMove kingSide = new ChessMove(kingPosition, new ChessPosition(1, 7), null);

        Assertions.assertTrue(game.validMoves(kingPosition).contains(queenSide),
                "ChessGame validMoves did not contain valid queen-side castle move");
        Assertions.assertTrue(game.validMoves(kingPosition).contains(kingSide),
                "ChessGame validMoves did not contain valid king-side castle move");

        //queen side castle works correctly
        Assertions.assertDoesNotThrow(() -> game.makeMove(queenSide));
        Assertions.assertEquals(loadBoard("""
                | | | | |k| | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | |K|R| | | |R|
                """), game.getBoard());

        //reset board
        board = loadBoard("""
                | | | | |k| | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                |R| | | |K| | |R|
                """);
        game.setBoard(board);
        game.setTeamTurn(TeamColor.WHITE);

        //king side castle works correctly
        Assertions.assertDoesNotThrow(() -> game.makeMove(kingSide));
        Assertions.assertEquals(loadBoard("""
                | | | | |k| | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                |R| | | | |R|K| |
                """), game.getBoard());
    }


    @Test
    @DisplayName("Black Team Castle")
    public void castleBlack() {
        ChessBoard board = loadBoard("""
                |r| | | |k| | |r|
                | |p| | | | | |q|
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | |K| | | |
                |R| | | | | | | |
                """);
        ChessGame game = new ChessGame();
        game.setBoard(board);
        game.setTeamTurn(TeamColor.BLACK);

        //check that with nothing in way, king can castle
        ChessPosition kingPosition = new ChessPosition(8, 5);
        ChessMove queenSide = new ChessMove(kingPosition, new ChessPosition(8, 3), null);
        ChessMove kingSide = new ChessMove(kingPosition, new ChessPosition(8, 7), null);

        Assertions.assertTrue(game.validMoves(kingPosition).contains(queenSide),
                "ChessGame validMoves did not contain valid queen-side castle move");
        Assertions.assertTrue(game.validMoves(kingPosition).contains(kingSide),
                "ChessGame validMoves did not contain valid king-side castle move");

        //queen side castle works correctly
        Assertions.assertDoesNotThrow(() -> game.makeMove(queenSide));
        Assertions.assertEquals(loadBoard("""
                | | |k|r| | | |r|
                | |p| | | | | |q|
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | |K| | | |
                |R| | | | | | | |
                """), game.getBoard());


        //reset board
        board = loadBoard("""
                |r| | | |k| | |r|
                | |p| | | | | |q|
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | |K| | | |
                |R| | | | | | | |
                """);
        game.setBoard(board);
        game.setTeamTurn(TeamColor.BLACK);

        //king side castle works correctly
        Assertions.assertDoesNotThrow(() -> game.makeMove(kingSide));
        Assertions.assertEquals(loadBoard("""
                |r| | | | |r|k| |
                | |p| | | | | |q|
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | |K| | | |
                |R| | | | | | | |
                """), game.getBoard());
    }


    @Test
    @DisplayName("Cannot Castle Through Pieces")
    public void castlingBlockedByTeam() {
        ChessBoard board = loadBoard("""
                | | | | |k| | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                |R| |B| |K| |Q|R|
                """);
        ChessGame game = new ChessGame();
        game.setBoard(board);
        game.setTeamTurn(TeamColor.WHITE);

        //check that with nothing in way, king can castle
        ChessPosition kingPosition = new ChessPosition(1, 5);
        ChessMove queenSide = new ChessMove(kingPosition, new ChessPosition(1, 3), null);
        ChessMove kingSide = new ChessMove(kingPosition, new ChessPosition(1, 7), null);

        //make sure king cannot castle
        Assertions.assertFalse(game.validMoves(kingPosition).contains(queenSide),
                "ChessGame validMoves contained an invalid castling move");
        Assertions.assertFalse(game.validMoves(kingPosition).contains(kingSide),
                "ChessGame validMoves contained an invalid castling move");
    }


    @Test
    @DisplayName("Cannot Castle in Check")
    public void castlingBlockedByEnemy() {
        ChessBoard board = loadBoard("""
                |r| | |B|k| | |r|
                | | | | | | | | |
                | | | | | |R| | |
                | | | | | | | | |
                | | | | | | | | |
                | |K| | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                """);
        ChessGame game = new ChessGame();
        game.setBoard(board);

        //make sure king cannot castle on either side
        ChessPosition kingPosition = new ChessPosition(8, 5);
        ChessMove queenSide = new ChessMove(kingPosition, new ChessPosition(8, 3), null);
        ChessMove kingSide = new ChessMove(kingPosition, new ChessPosition(8, 7), null);
        Assertions.assertFalse(game.validMoves(kingPosition).contains(queenSide),
                "ChessGame validMoves contained an invalid castling move");
        Assertions.assertFalse(game.validMoves(kingPosition).contains(kingSide),
                "ChessGame validMoves contained an invalid castling move");
    }


    @Test
    @DisplayName("Cannot Castle After Moving")
    public void noCastleAfterMove() throws InvalidMoveException {
        ChessBoard board = loadBoard("""
                | | |k| | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                |R| | | |K| | |R|
                """);
        ChessGame game = new ChessGame();
        game.setBoard(board);
        game.setTeamTurn(TeamColor.WHITE);

        //move left rook
        game.makeMove(new ChessMove(new ChessPosition(1, 1), new ChessPosition(1, 4), null));
        game.makeMove(new ChessMove(new ChessPosition(8, 3), new ChessPosition(8, 2), null));

        //move rook back to starting spot
        game.makeMove(new ChessMove(new ChessPosition(1, 4), new ChessPosition(1, 1), null));
        /*
                | |k| | | | | | |
		        | | | | | | | | |
		        | | | | | | | | |
		        | | | | | | | | |
		        | | | | | | | | |
		        | | | | | | | | |
		        | | | | | | | | |
		        |R| | | |K| | |R|
         */

        ChessPosition kingPosition = new ChessPosition(1, 5);
        ChessMove queenSide = new ChessMove(kingPosition, new ChessPosition(1, 3), null);
        ChessMove kingSide = new ChessMove(kingPosition, new ChessPosition(1, 7), null);

        //make sure king can't castle towards moved rook, but still can to unmoved rook
        Assertions.assertFalse(game.validMoves(kingPosition).contains(queenSide),
                "ChessGame validMoves contained an invalid castling move");
        Assertions.assertTrue(game.validMoves(kingPosition).contains(kingSide),
                "ChessGame validMoves did not contain valid king-side castle move");

        //move king
        game.makeMove(new ChessMove(new ChessPosition(8, 2), new ChessPosition(8, 3), null));
        game.makeMove(new ChessMove(kingPosition, new ChessPosition(1, 6), null));
        /*
                | | |k| | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                |R| | | | |K| |R|
         */

        //move king back to starting position
        game.makeMove(new ChessMove(new ChessPosition(8, 3), new ChessPosition(8, 4), null));
        game.makeMove(new ChessMove(new ChessPosition(1, 6), kingPosition, null));
        /*
                | | | |k| | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                |R| | | |K| | |R|
         */

        //make sure king can't castle anymore
        Assertions.assertFalse(game.validMoves(kingPosition).contains(queenSide),
                "ChessGame validMoves contained an invalid castling move");
        Assertions.assertFalse(game.validMoves(kingPosition).contains(kingSide),
                "ChessGame validMoves contained an invalid castling move");
    }

}
