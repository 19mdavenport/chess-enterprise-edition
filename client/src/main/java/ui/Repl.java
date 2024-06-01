package ui;

import chess.ChessGame;
import data.DataCache;
import web.WebSocketClientObserver;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class Repl implements WebSocketClientObserver {

    public void run() {
        System.out.println(EscapeSequences.BLACK_QUEEN + "Welcome to Chess. Sign in to start." + EscapeSequences.BLACK_QUEEN);
        System.out.print(DataCache.getInstance().getUi().help());

        Scanner scanner = new Scanner(System.in);
        CommandOutput result = new CommandOutput("", true);
        while (!result.output().equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            String[] tokens = line.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            try {
                result = DataCache.getInstance().getUi().eval(cmd, params);
                System.out.print(
                        (result.success() ? EscapeSequences.SET_TEXT_COLOR_BLUE : EscapeSequences.SET_TEXT_COLOR_RED) +
                                result.output() + EscapeSequences.RESET_TEXT_COLOR);
            } catch (Throwable e) {
                System.out.print(e.getMessage());
            }
        }
        System.out.println(" Thanks for playing!");
    }

    private void printPrompt() {
        System.out.print("\n" + EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.SET_TEXT_ITALIC +
                DataCache.getInstance().getUi().getPromptText() + " >>> " + EscapeSequences.SET_TEXT_COLOR_GREEN +
                EscapeSequences.RESET_TEXT_ITALIC);
    }

    @Override
    public void loadGame(ChessGame game) {
        if(DataCache.getInstance().getLastGame() == null) {
            DataCache.getInstance().setLastGame(game);
        }
        BoardPrinter.printGame(game, DataCache.getInstance().getLastGame());
        DataCache.getInstance().setLastGame(game);
        printPrompt();
    }

    @Override
    public void notify(String message) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_MAGENTA + message+ EscapeSequences.RESET_TEXT_COLOR);
        printPrompt();
    }

    @Override
    public void error(String message) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + message + EscapeSequences.RESET_TEXT_COLOR);
        printPrompt();
    }

}
