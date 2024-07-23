package ui;

import chess.*;
import data.DataCache;

import java.text.DecimalFormat;
import java.util.*;

public class ColorSchemeCreator {

    private static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("0.0##");
    
    public void createColorScheme() {
        ChessBoardColorScheme previous = DataCache.getInstance().getColorScheme();
        System.out.println("Welcome to the color scheme creator!");
        float hue = getFloat("base hue", 0f, 360f, 0f, 360f);
        float saturation = getFloat("saturation", 0, 1, 0.45f, 0.6f);
        float border = getFloat("border lightness", 0f, 1f, 0.5f, 0.6f);
        float borderText = getFloat("border text lightness", 0f, 1f, 0.1f, 0.2f);
        float darkSquare = getFloat("dark square lightness", 0f, 0.6f, 0.35f, 0.55f);
        float lightSquare = getFloat("light square lightness", darkSquare, 1f, 0.5f, 0.7f);
        float whitePiece = getFloat("white piece lightness", 0.7f, 1f, 0.85f, 0.95f);
        float blackPiece = getFloat("black piece lightness", 0f, 0.3f, 0.05f, 0.15f);

        ChessBoardColorScheme created = new ChessBoardColorScheme(
                hue, saturation, border, borderText, darkSquare, lightSquare, whitePiece, blackPiece);
        DataCache.getInstance().setColorScheme(created);

        System.out.println("Color scheme created! Here's how it looks:");

        ChessGame lastGame = DataCache.getInstance().getLastGame();
        if(lastGame == null) {
            lastGame = new ChessGame();
        }
        printHighlightPiece(lastGame);
        printMoveMade(lastGame);

        DataCache.getInstance().setColorScheme(previous);

        System.out.println("Would you like to save this color scheme? (\"y\" or \"yes\" to save) ");
        Scanner scanner = new Scanner(System.in);
        String in = scanner.next();
        if (in.equals("y") || in.equals("yes")) {
            ChessBoardColorScheme.COLOR_SCHEMES.add(created);
            System.out.printf("Saved as color scheme %d\n", ChessBoardColorScheme.COLOR_SCHEMES.size());
            DataCache.getInstance().setColorScheme(created);
        }
        else {
            System.out.println("Color scheme not saved. Would you like to try again? (\"y\" or \"yes\" to try again)");
            in = scanner.next();
            if (in.equals("y") || in.equals("yes")) {
                createColorScheme();
            }
        }
    }
    
    private float getFloat(String name, float min, float max, float typicalMin, float typicalMax) {
        System.out.printf("Please enter a value for %s (%s-%s) typically %s-%s: ", name,
                DECIMAL_FORMATTER.format(min), DECIMAL_FORMATTER.format(max),
                DECIMAL_FORMATTER.format(typicalMin), DECIMAL_FORMATTER.format(typicalMax));
        Scanner scanner = new Scanner(System.in);
        String str = scanner.next();
        try {
            float value = Float.parseFloat(str);
            if(value >= min && value <= max) {
                return value;
            }
            System.out.printf("%s not within bounds %s-%s. ", value, DECIMAL_FORMATTER.format(min), DECIMAL_FORMATTER.format(max));
        } catch (NumberFormatException e) {
            System.out.printf("Could not parse %s as a float. ", str);
        }
        return getFloat(name, min, max, typicalMin, typicalMax);
    }

    private void printMoveMade(ChessGame game) {
        List<ChessMove> possibleMoves = new ArrayList<>();
        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = game.getBoard().getPiece(pos);
                if(piece != null && piece.getTeamColor() == game.getTeamTurn()) {
                    possibleMoves.addAll(game.validMoves(pos));
                }
            }
        }
        if(possibleMoves.isEmpty()) {
            printMoveMade(new ChessGame());
        }
        else {
            ChessMove move = possibleMoves.get(new Random().nextInt(possibleMoves.size()));
            ChessGame copy = new ChessGame(game);
            try {
                copy.makeMove(move);
            } catch (InvalidMoveException e) {
                throw new RuntimeException(e);
            }
            BoardPrinter.printGame(copy, game);
        }
    }

    private void printHighlightPiece(ChessGame game) {
        List<ChessPosition> possiblePositions = new ArrayList<>();
        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = game.getBoard().getPiece(pos);
                if(piece != null && !game.validMoves(pos).isEmpty()) {
                    possiblePositions.add(pos);
                }
            }
        }
        if(possiblePositions.isEmpty()) {
            printHighlightPiece(new ChessGame());
        }
        else {
            ChessPosition pos = possiblePositions.get(new Random().nextInt(possiblePositions.size()));
            BoardPrinter.printGame(game, pos);
        }
    }
}
