package websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketHandler.class);

    private final ConnectionManager connectionManager = new ConnectionManager();

    private final Gson gson = new Gson();

    private static final WebSocketHandler INSTANCE = new WebSocketHandler();

    private DataAccess dataAccess;

    private WebSocketHandler() {}


    public void setDataAccess(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public static WebSocketHandler getInstance() {
        return INSTANCE;
    }


    @OnWebSocketError
    public void error(Throwable error) {
        if (!((error instanceof EofException) ||
                error.getCause() != null && error.getCause().getMessage().contains("Connection reset by peer"))) {
            LOGGER.warn("WebSocket error: ", error);
        }
    }


    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        try {
            LOGGER.debug("Received from {}: {}", session.getRemoteAddress(), message);
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

            AuthData token = dataAccess.getAuthDAO().findAuth(command.getAuthString());
            if (token == null) {
                throw new WebsocketException("Error: Invalid authtoken");
            }

            GameData game = dataAccess.getGameDAO().findGame(command.getGameID());
            if (game == null) {
                throw new WebsocketException("Error: Invalid gameID");
            }

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, token.username(), game);
                case MAKE_MOVE ->
                        makeMove(session, gson.fromJson(message, MakeMoveCommand.class), token.username(), game);
                case LEAVE -> leave(session, token.username(), game);
                case RESIGN -> resign(session, token.username(), game);
            }
        } catch (DataAccessException e) {
            LOGGER.warn("DataAccessException: ", e);
            sendError(session, "Error: Unknown server error occurred: " + e.getMessage(), message);
        } catch (WebsocketException e) {
            sendError(session, e.getMessage(), message);
        }
    }

    private void sendError(Session session, String message, String input) throws IOException {
        connectionManager.sendError(session, message);
        LOGGER.info("Error to {}: Error message: {} Input was: {}", session.getRemoteAddress(), message, input);
    }


    private void connect(Session session, String username, GameData game) throws IOException {
        connectionManager.addSession(game.gameID(), session);

        ServerMessage loadGame = new LoadGameMessage(game.game());
        String loadGameJson = gson.toJson(loadGame);
        connectionManager.sendMessage(session, loadGameJson);

        String message = "User " + username + " is now ";
        if (username.equals(game.whiteUsername())) {
            message += "playing as white";
        }
        else if (username.equals(game.blackUsername())) {
            message += "playing as black";
        }
        else {
            message += "watching";
        }

        ServerMessage notify = new NotificationMessage(message);
        String notifyJson = gson.toJson(notify);

        connectionManager.broadcast(notifyJson, game.gameID(), session);
    }


    private void leave(Session session, String username, GameData game) throws IOException, DataAccessException {
        String description = "watching";
        if (username.equals(game.whiteUsername())) {
            game = new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game());
            description = "playing as white";
            dataAccess.getGameDAO().updateGame(game);
        } else if (username.equals(game.blackUsername())) {
            game = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game());
            description = "playing as black";
            dataAccess.getGameDAO().updateGame(game);
        }

        connectionManager.removeSession(game.gameID(), session);

        ServerMessage notify = new NotificationMessage("User " + username + " is no longer " + description);
        String notifyJson = gson.toJson(notify);

        connectionManager.broadcast(notifyJson, game.gameID(), session);
    }


    private void makeMove(Session session, MakeMoveCommand command, String username, GameData game)
            throws IOException, WebsocketException, DataAccessException {
        if (command.getMove() == null) {
            throw new WebsocketException("Error: Must include a move");
        }
        confirmGameStatus(game, username);
        if ((game.game().getTeamTurn() == ChessGame.TeamColor.WHITE && !Objects.equals(game.whiteUsername(), username)) ||
                (game.game().getTeamTurn() == ChessGame.TeamColor.BLACK &&
                        !Objects.equals(game.blackUsername(), username))) {
            throw new WebsocketException("Error: It's not your turn");
        }

        try {
            game.game().makeMove(command.getMove());
            dataAccess.getGameDAO().updateGame(game);
        } catch (InvalidMoveException e) {
            throw new WebsocketException("Error: That's not a valid move: " + e.getMessage());
        }

        ServerMessage loadGame = new LoadGameMessage(game.game());
        ServerMessage notify = new NotificationMessage(username + " makes move " + command.getMove());

        String loadGameJson = gson.toJson(loadGame);
        String notifyJson = gson.toJson(notify);

        connectionManager.broadcast(loadGameJson, game.gameID(), null);
        connectionManager.broadcast(notifyJson, game.gameID(), session);

        checkGameStatus(username, game);
    }

    private void checkGameStatus(String username, GameData game) throws IOException, DataAccessException {
        String notifyJson;
        ServerMessage notify;
        String extra = null;
        if (game.game().isInCheckmate(game.game().getTeamTurn())) {
            extra = "Checkmate. " + username + " wins!";
            game.game().setActive(false);
        } else if (game.game().isInStalemate(game.game().getTeamTurn())) {
            extra = "The game ends in a stalemate";
            game.game().setActive(false);
        } else if (game.game().isInCheck(game.game().getTeamTurn())) {
            extra = "Check";
        }
        if (extra != null) {
            notify = new NotificationMessage(extra);
            notifyJson = gson.toJson(notify);
            connectionManager.broadcast(notifyJson, game.gameID(), null);
            dataAccess.getGameDAO().updateGame(game);
        }
    }


    private void resign(Session session, String username, GameData game)
            throws IOException, WebsocketException, DataAccessException {
        confirmGameStatus(game, username);

        game.game().setActive(false);
        dataAccess.getGameDAO().updateGame(game);

        String opponent =
                (Objects.equals(username, game.whiteUsername())) ? game.blackUsername() : game.whiteUsername();
        ServerMessage notify = new NotificationMessage(
                username + " has resigned." + ((opponent != null) ? " " + opponent + " wins!" : ""));
        String notifyJson = gson.toJson(notify);
        connectionManager.broadcast(notifyJson, game.gameID(), session);


        notify = new NotificationMessage("You have resigned.");
        notifyJson = gson.toJson(notify);
        connectionManager.sendMessage(session, notifyJson);
    }

    private void confirmGameStatus(GameData game, String username) throws WebsocketException {
        if (!(Objects.equals(game.blackUsername(), username) || Objects.equals(game.whiteUsername(), username))) {
            throw new WebsocketException("Error: You are not a participant in this game");
        }

        if (!game.game().isActive()) {
            throw new WebsocketException("Error: Game is over");
        }
    }

    public void clear() {
        connectionManager.clear();
    }
}
