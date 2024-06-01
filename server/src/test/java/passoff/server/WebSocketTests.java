package passoff.server;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.*;
import passoff.websocket.WebsocketTestingEnvironment;
import server.Server;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static passoff.server.WebSocketTestUtils.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WebSocketTests {

    private static Server server;

    private WebSocketTestUtils.WebsocketUser white;

    private WebSocketTestUtils.WebsocketUser black;

    private WebSocketTestUtils.WebsocketUser observer;

    private Integer gameID;

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeAll
    public static void init() throws URISyntaxException {
        server = new Server();
        var port = Integer.toString(server.run(0));
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new TestServerFacade("localhost", port);
        serverFacade.clear();

        GsonBuilder gsonBuilder = TestFactory.getGsonBuilder();
        environment = new WebsocketTestingEnvironment("localhost", port, "/ws", gsonBuilder);

        waitTime = TestFactory.getMessageTime();
    }

    @BeforeEach
    public void setup() {
        //populate database
        serverFacade.clear();

        white = registerUser("white", "WHITE", "white@chess.com");
        black = registerUser("black", "BLACK", "black@chess.com");
        observer = registerUser("observer", "OBSERVER", "observer@chess.com");

        gameID = WebSocketTestUtils.createGame(white, "testGame");

        joinGame(gameID, white, ChessGame.TeamColor.WHITE);
        joinGame(gameID, black, ChessGame.TeamColor.BLACK);
    }

    @AfterEach
    public void tearDown() {
        environment.disconnectAll();
    }

    @Test
    @Order(1)
    @DisplayName("Normal Connect")
    public void connectGood() {
        setupNormalGame();
    }

    @Test
    @Order(2)
    @DisplayName("Connect Bad GameID")
    public void connectBadGameID() {
        //player connect with an incorrect game id
        connectToGame(white, gameID + 1, false, Set.of(), Set.of());

        //observer connect with an incorrect game id
        connectToGame(observer, gameID + 1, false, usernames(white), Set.of());
    }

    @Test
    @Order(2)
    @DisplayName("Connect Bad AuthToken")
    public void connectBadAuthToken() {
        connectToGame(new WebSocketTestUtils.WebsocketUser(black.username(), "badAuth"), gameID, false, Set.of(),
                Set.of());

        connectToGame(new WebSocketTestUtils.WebsocketUser(observer.username(), "badAuth"), gameID, false,
                usernames(black), Set.of());
    }

    @Test
    @Order(3)
    @DisplayName("Normal Make Move")
    public void validMove() {
        setupNormalGame();

        //create pawn move
        ChessMove move = new ChessMove(new ChessPosition(2, 5), new ChessPosition(3, 5), null);

        //make a valid move
        makeMove(white, gameID, move, true, false, usernames(black, observer), Set.of());
    }

    @Test
    @Order(4)
    @DisplayName("Make Move Bad Authtoken")
    public void makeMoveBadAuthtoken() {
        setupNormalGame();

        //set up valid move - pawn move two steps forward
        ChessMove move = new ChessMove(new ChessPosition(2, 6), new ChessPosition(4, 6), null);

        //send command with wrong authtoken
        makeMove(new WebsocketUser(white.username(), "badAuth"), gameID, move, false, false, usernames(black, observer),
                Set.of());
    }

    @Test
    @Order(4)
    @DisplayName("Make Invalid Move")
    public void invalidMoveBadMove() {
        setupNormalGame();

        //try to move rook through a pawn - invalid move
        ChessMove move = new ChessMove(new ChessPosition(1, 1), new ChessPosition(1, 5), null);
        makeMove(white, gameID, move, false, false, usernames(black, observer), Set.of());
    }

    @Test
    @Order(4)
    @DisplayName("Make Move Wrong Turn")
    public void invalidMoveWrongTurn() {
        setupNormalGame();

        //try to move pawn out of turn - would be valid if in turn
        ChessMove move = new ChessMove(new ChessPosition(7, 5), new ChessPosition(5, 5), null);
        makeMove(black, gameID, move, false, false, usernames(white, observer), Set.of());
    }

    @Test
    @Order(4)
    @DisplayName("Make Move for Opponent")
    public void invalidMoveOpponent() {
        setupNormalGame();

        //setup valid pawn move
        ChessMove move = new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null);

        //attempt to make the move as the other player
        makeMove(black, gameID, move, false, false, usernames(white, observer), Set.of());
    }

    @Test
    @Order(4)
    @DisplayName("Make Move Observer")
    public void invalidMoveObserver() {
        setupNormalGame();

        //setup valid pawn move
        ChessMove move = new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null);

        //have observer attempt to make a move
        makeMove(observer, gameID, move, false, false, usernames(white, black), Set.of());
    }

    @Test
    @Order(4)
    @DisplayName("Make Move Game Over")
    public void invalidMoveGameOver() {
        setupNormalGame();

        //Fools mate setup
        ChessMove move = new ChessMove(new ChessPosition(2, 7), new ChessPosition(4, 7), null);
        makeMove(white, gameID, move, true, false, usernames(black, observer), Set.of());

        move = new ChessMove(new ChessPosition(7, 5), new ChessPosition(6, 5), null);
        makeMove(black, gameID, move, true, false, usernames(white, observer), Set.of());

        move = new ChessMove(new ChessPosition(2, 6), new ChessPosition(3, 6), null);
        makeMove(white, gameID, move, true, false, usernames(black, observer), Set.of());

        move = new ChessMove(new ChessPosition(8, 4), new ChessPosition(4, 8), null);
        makeMove(black, gameID, move, true, true, usernames(white, observer), Set.of());
        //checkmate

        //attempt another move
        move = new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null);
        makeMove(white, gameID, move, false, false, usernames(black, observer), Set.of());
    }

    @Test
    @Order(5)
    @DisplayName("Normal Resign")
    public void validResign() {
        setupNormalGame();

        resign(white, gameID, true, usernames(black, observer), Set.of());
    }

    @Test
    @Order(6)
    @DisplayName("Cannot Move After Resign")
    public void moveAfterResign() {
        setupNormalGame();

        resign(black, gameID, true, usernames(white, observer), Set.of());

        //attempt to make a move after other player resigns
        ChessMove move = new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null);
        makeMove(white, gameID, move, false, false, usernames(black, observer), Set.of());
    }

    @Test
    @Order(6)
    @DisplayName("Observer Resign")
    public void invalidResignObserver() {
        setupNormalGame();

        //have observer try to resign - should reject
        resign(observer, gameID, false, usernames(white, black), Set.of());
    }

    @Test
    @Order(6)
    @DisplayName("Double Resign")
    public void invalidResignGameOver() {
        setupNormalGame();

        //normal resign
        resign(black, gameID, true, usernames(white, observer), Set.of());

        //attempt to resign after other player resigns
        resign(white, gameID, false, usernames(black, observer), Set.of());
    }

    @Test
    @Order(7)
    @DisplayName("Leave Game")
    public void leaveGame() {
        setupNormalGame();

        //have white player leave
        //all other players get notified, white player should not be
        leave(white, gameID, usernames(black, observer), Set.of());

        //observer leaves - only black player should get a notification
        leave(observer, gameID, usernames(black), usernames(white));
    }

    @Test
    @Order(8)
    @DisplayName("Join After Leave Game")
    public void joinAfterLeaveGame() {
        setupNormalGame();

        //have white player leave
        //all other players get notified, white player should not be
        leave(white, gameID, usernames(black, observer), Set.of());

        //replace white player with a different player
        WebsocketUser white2 = registerUser("white2", "WHITE", "white2@chess.com");
        joinGame(gameID, white2, ChessGame.TeamColor.WHITE);
        connectToGame(white2, gameID, true, usernames(black, observer), usernames(white));

        //new white player can make move
        ChessMove move = new ChessMove(new ChessPosition(2, 5), new ChessPosition(3, 5), null);
        makeMove(white2, gameID, move, true, false, usernames(black, observer), usernames(white));
    }

    @Test
    @Order(9)
    @DisplayName("Multiple Concurrent Games")
    public void multipleConcurrentGames() {
        setupNormalGame();

        //setup parallel game
        WebsocketUser white2 = registerUser("white2", "WHITE", "white2@chess.com");
        WebsocketUser black2 = registerUser("black2", "BLACK", "black2@chess.com");
        WebsocketUser observer2 = registerUser("observer2", "OBSERVER", "observer2@chess.com");

        int otherGameID = createGame(white, "testGame2");

        joinGame(otherGameID, white2, ChessGame.TeamColor.WHITE);
        joinGame(otherGameID, black2, ChessGame.TeamColor.BLACK);

        //setup second game
        connectToGame(white2, otherGameID, true, Set.of(), usernames(white, black, observer));
        connectToGame(black2, otherGameID, true, usernames(white2), usernames(white, black, observer));
        connectToGame(observer2, otherGameID, true, usernames(white2, black2), usernames(white, black, observer));

        //make move in first game - only users in first game should be notified
        ChessMove move = new ChessMove(new ChessPosition(2, 5), new ChessPosition(3, 5), null);
        makeMove(white, gameID, move, true, false, usernames(black, observer), usernames(white2, black2, observer2));

        //resign in second game - only users in second game should be notified
        resign(white2, otherGameID, true, usernames(black2, observer2), usernames(white, black, observer));

        //player leave in first game - only users remaining in first game should be notified
        leave(white, gameID, usernames(black, observer), usernames(white2, black2, observer2));
    }

    private void setupNormalGame() {
        //connect white player
        connectToGame(white, gameID, true, Set.of(), Set.of());

        //connect black player
        connectToGame(black, gameID, true, usernames(white), Set.of());

        //connect observer
        connectToGame(observer, gameID, true, usernames(white, black), Set.of());
    }

    private Set<String> usernames(WebsocketUser... users) {
        return Arrays.stream(users).map(user -> user.username()).collect(Collectors.toSet());
    }

}