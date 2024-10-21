package client;

import chess.TeamColor;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import web.ResponseException;
import web.ServerFacade;

import java.util.Random;


public class ServerFacadeTests {
    private static final String PASSWORD = "password";

    private static final String EMAIL = "no-reply@byu.edu";

    private static Server server;

    private static ServerFacade facade;

    private static String username;

    private String authToken;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @BeforeEach
    public void register() {
        username = "user" + new Random().nextInt(9999);
        UserData request = new UserData(username, PASSWORD, EMAIL);
        authToken = facade.register(request).authToken();
    }


    @Test
    public void registerPass() {
        String otherUsername = username;
        while (username.equals(otherUsername)) {
            otherUsername = "user" + new Random().nextInt(9999);
        }

        UserData request = new UserData(otherUsername, PASSWORD, EMAIL);
        AuthData result = facade.register(request);

        Assertions.assertNotNull(result);

        Assertions.assertNotNull(result.authToken());
        Assertions.assertNotEquals(authToken, result.authToken());
    }


    @Test
    public void registerFail() {
        UserData request = new UserData(username, PASSWORD, EMAIL);
        Assertions.assertThrows(ResponseException.class, () -> facade.register(request));
    }


    @Test
    public void loginPass() {
        UserData request = new UserData(username, PASSWORD, null);
        AuthData result = facade.login(request);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.authToken());
        Assertions.assertNotEquals(authToken, result.authToken());
    }


    @Test
    public void loginFail() {
        String otherUsername = username;
        while (username.equals(otherUsername)) {
            otherUsername = "user" + new Random().nextInt(9999);
        }

        UserData request = new UserData(otherUsername, PASSWORD, null);
        Assertions.assertThrows(ResponseException.class, () -> facade.login(request));

    }


    @Test
    public void logoutPass() {
        facade.logout();
        Assertions.assertThrows(ResponseException.class, () -> facade.listGames());
    }


    @Test
    public void logoutFail() {
        facade.logout();
        Assertions.assertThrows(ResponseException.class, () -> facade.logout());
    }


    @Test
    public void createGamePass() {
        String gameName = "game" + new Random().nextInt(9999);
        GameData request = new GameData(0, null, null, gameName, null);
        GameData result = facade.createGame(request);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(request.gameName(), result.gameName());
        Assertions.assertTrue(result.gameID() > 0);
    }


    @Test
    public void createGameFail() {
        GameData request = new GameData(0, null, null, null, null);
        Assertions.assertThrows(ResponseException.class, () -> facade.createGame(request));
    }


    @Test
    public void joinGamePass() {
        String gameName = "game" + new Random().nextInt(9999);
        GameData cGRequest = new GameData(0, null, null, gameName, null);
        GameData cGResult = facade.createGame(cGRequest);

        JoinGameRequest request = new JoinGameRequest(TeamColor.BLACK, cGResult.gameID());
        Assertions.assertDoesNotThrow(() -> facade.joinGame(request));
    }


    @Test
    public void joinGameFail() {
        JoinGameRequest request = new JoinGameRequest(TeamColor.WHITE, 123456789);
        Assertions.assertThrows(ResponseException.class, () -> facade.joinGame(request));
    }


    @Test
    public void listGamesPass() {
        String gameName = "game" + new Random().nextInt(9999);
        GameData request = new GameData(0, null, null, gameName, null);
        facade.createGame(request);
        ListGamesResponse result = facade.listGames();

        Assertions.assertNotNull(result);

        Assertions.assertFalse(result.games().isEmpty());
    }


    @Test
    public void listGamesFail() {
        facade.logout();
        Assertions.assertThrows(ResponseException.class, () -> facade.listGames());
    }

}
