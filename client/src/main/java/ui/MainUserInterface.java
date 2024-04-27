package ui;

import chess.ChessGame;
import data.DataCache;
import model.GameData;
import model.JoinGameRequest;
import model.ListGamesResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class MainUserInterface implements UserInterface {
    private List<GameData> games;

    @Override
    public CommandOutput eval(String cmd, String[] args) {
        return switch (cmd) {
            case "c", "create" -> create(args);
            case "l", "list" -> list();
            case "j", "join" -> join(args);
            case "w", "watch" -> watch(args);
            case "logout" -> logout();
            case "h", "help" -> new CommandOutput(help(), true);
            default -> new CommandOutput(help(), false);
        };
    }


    @Override
    public String help() {
        return """
                Options:
                List current games: \"l\", \"list\"
                Create a new game: \"c\", \"create\" <GAME NAME>
                Join a game: \"j\", \"join\" <GAME ID> <COLOR>
                Watch a game: \"w\", \"watch\" <GAME ID>
                Logout: \"logout\"
                """;
    }


    @Override
    public String getPromptText() {
        return "Chess";
    }


    private CommandOutput create(String[] args) {
        if (args.length != 1) return new CommandOutput("Usage: create <GAME NAME>", false);
        GameData request = new GameData(0, null, null, args[0], null);
        GameData response = DataCache.getInstance().getFacade().createGame(request);

        return new CommandOutput("Successfully created game " + args[0], true);
    }


    private CommandOutput join(String[] args) {
        if (args.length != 2) return new CommandOutput("Usage: join <GAME ID> <COLOR>", false);
        ChessGame.TeamColor color;
        try {
            color = ChessGame.TeamColor.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            return new CommandOutput("Unable to parse " + args[1] + " as a color", false);
        }

        int gameID;
        try {
            gameID = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return new CommandOutput("Unable to parse " + args[0] + " as a game id", false);
        }


        JoinGameRequest request = new JoinGameRequest(color, gameID);
        DataCache.getInstance().getFacade().joinGame(request);
        DataCache.getInstance().setGameId(gameID);
        DataCache.getInstance().setPlayerColor(color);

        BoardPrinter.printGame(new ChessGame(), ChessBoardColorScheme.COLOR_SCHEMES[0],
                color, new HashSet<>(), new HashSet<>());

        return new CommandOutput("", true);
    }


    private CommandOutput list() {
        ListGamesResponse result = DataCache.getInstance().getFacade().listGames();

        if (result.games().isEmpty()) {
            return new CommandOutput("There are no open games", true);
        }

        games = new ArrayList<>(result.games());
        games.sort(Comparator.comparingInt(GameData::gameID));

        int longestName = 1;
        int longestWhiteUsername = 4;
        for (GameData game : games) {
            if (game.gameName().length() > longestName) longestName = game.gameName().length();
            if (game.whiteUsername() != null && game.whiteUsername().length() > longestWhiteUsername)
                longestWhiteUsername = game.whiteUsername().length();
        }

        int gameNumOffset = String.valueOf(games.size()).length() + 4;
        int gameNameOffset = longestName + 4;
        int whiteUsernameOffset = longestWhiteUsername + 4;

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < games.size(); i++) {
            GameData game = games.get(i);
            out.append(i + 1).append(". ");
            out.append(" ".repeat(gameNumOffset - String.valueOf(game.gameID()).length()));

            out.append("Game name: ").append(game.gameName());
            out.append(" ".repeat(gameNameOffset - game.gameName().length()));

            if (game.whiteUsername() == null) {
                out.append("White empty" + " ".repeat(whiteUsernameOffset - 4));
            } else {
                out.append("White: ").append(game.whiteUsername());
                out.append(" ".repeat(whiteUsernameOffset - game.whiteUsername().length()));
            }

            if (game.blackUsername() == null) {
                out.append("Black empty");
            } else {
                out.append("Black: ").append(game.blackUsername());
            }
            out.append("\n");
        }
        return new CommandOutput(out.toString(), true);
    }


    private CommandOutput logout() {
        DataCache.getInstance().getFacade().logout();
        DataCache.getInstance().setState(DataCache.State.LOGGED_OUT);
        return new CommandOutput("Sucessfully logged out", true);
    }


    private CommandOutput watch(String[] args) {
        if (args.length != 1) return new CommandOutput("Usage: watch <GAME ID>", false);

        int gameNum;
        try {
            gameNum = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return new CommandOutput("Unable to parse " + args[0] + " as a game number", false);
        }

        if(gameNum < 1 || gameNum > games.size()) {
            return new CommandOutput("Invalid game number " + gameNum, false);
        }

        int gameID = games.get(gameNum - 1).gameID();
        JoinGameRequest request = new JoinGameRequest(null, gameID);
        DataCache.getInstance().getFacade().joinGame(request);
        DataCache.getInstance().setGameId(gameID);
        DataCache.getInstance().setPlayerColor(null);

        BoardPrinter.printGame(new ChessGame(), ChessBoardColorScheme.COLOR_SCHEMES[0],
                ChessGame.TeamColor.WHITE, new HashSet<>(), new HashSet<>());

        return new CommandOutput("", true);
    }

}
