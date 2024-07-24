package data;

import chess.ChessGame;
import ui.*;
import web.ServerFacade;
import web.WebSocketClient;
import web.WebSocketClientObserver;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;

public class DataCache {
    private static DataCache instance = new DataCache();
    public static DataCache getInstance() {
        return instance;
    }
    private DataCache(){}

    public enum State {
        LOGGED_OUT, LOGGED_IN, IN_GAME
    }

    private String serverUrl;

    private String authToken;

    private String username;

    private int gameId;

    private ChessGame.TeamColor playerColor;

    private ServerFacade facade;

    private UserInterface userInterface = new LoginUserInterface();

    private State state;

    private ChessBoardColorScheme colorScheme = ChessBoardColorScheme.COLOR_SCHEMES.getFirst();

    private ChessGame lastGame;

    private WebSocketClient webSocketClient;

    public void setRunOptions(String host, int port, WebSocketClientObserver observer)
            throws DeploymentException, URISyntaxException, IOException {
        facade = new ServerFacade("http://%s:%d".formatted(host, port));
        webSocketClient = new WebSocketClient(observer, host, port);
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public ServerFacade getFacade() {
        return facade;
    }

    public UserInterface getUi() {
        return userInterface;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        userInterface = switch (state) {
            case LOGGED_OUT -> new LoginUserInterface();
            case LOGGED_IN -> new MainUserInterface();
            case IN_GAME -> new GameUserInterface();
        };
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(ChessGame.TeamColor playerColor) {
        this.playerColor = playerColor;
    }

    public ChessBoardColorScheme getColorScheme() {
        return colorScheme;
    }

    public void setColorScheme(ChessBoardColorScheme colorScheme) {
        this.colorScheme = colorScheme;
    }

    public ChessGame getLastGame() {
        return lastGame;
    }

    public void setLastGame(ChessGame lastGame) {
        this.lastGame = lastGame;
    }

    public WebSocketClient getWebSocketClient() {
        return webSocketClient;
    }
}
