package passoff.server;

import chess.ChessGame;
import chess.ChessMove;
import org.junit.jupiter.api.Assertions;
import passoff.model.*;
import passoff.websocket.TestCommand;
import passoff.websocket.TestMessage;
import passoff.websocket.WebsocketTestingEnvironment;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WebSocketTestUtils {

    static WebsocketTestingEnvironment environment;

    static TestServerFacade serverFacade;

    static Long waitTime;

    static record WebsocketUser(String username, String authToken) {}

    static WebsocketUser registerUser(String name, String password, String email) {
        TestAuthResult authResult = serverFacade.register(new TestUser(name, password, email));
        Assertions.assertEquals(200, serverFacade.getStatusCode(),
                String.format("HTTP Status code was not 200 for registering a new user, was %d. Message: %s",
                        serverFacade.getStatusCode(), authResult.getMessage()));
        return new WebsocketUser(authResult.getUsername(), authResult.getAuthToken());
    }

    static int createGame(WebsocketUser user, String name) {
        TestCreateResult createResult = serverFacade.createGame(new TestCreateRequest(name), user.authToken());
        Assertions.assertEquals(200, serverFacade.getStatusCode(),
                String.format("HTTP Status code was not 200 for creating a new game, was %d. Message: %s",
                        serverFacade.getStatusCode(), createResult.getMessage()));
        return createResult.getGameID();
    }

    static void joinGame(int gameID, WebsocketUser user, ChessGame.TeamColor color) {
        TestResult result = serverFacade.joinPlayer(new TestJoinRequest(color, gameID), user.authToken());
        Assertions.assertEquals(200, serverFacade.getStatusCode(),
                String.format("HTTP Status code was not 200 for joining a player to a game, was %d. Message: %s",
                        serverFacade.getStatusCode(), result.getMessage()));
    }


    static void connectToGame(WebsocketUser sender, int gameID, boolean expectSuccess, Set<String> inGame,
                              Set<String> otherClients) {
        TestCommand connectCommand = new TestCommand(UserGameCommand.CommandType.CONNECT, sender.authToken(), gameID);
        var numExpectedMessages = expectedMessages(sender, 1, inGame, (expectSuccess ? 1 : 0), otherClients);
        var actualMessages = environment.exchange(sender.username(), connectCommand, numExpectedMessages, waitTime);

        assertCommandMessages(actualMessages, expectSuccess, sender.username(), WebSocketTestUtils::assertLoadGameMessage,
                inGame, WebSocketTestUtils::assertNotificationMessage, otherClients);
    }

    static void makeMove(WebsocketUser sender, int gameID, ChessMove move, boolean expectSuccess,
                         boolean extraNotification, Set<String> inGame, Set<String> otherClients) {
        TestCommand moveCommand = new TestCommand(sender.authToken(), gameID, move);
        var numExpectedMessages = expectedMessages(sender, 1, inGame, (expectSuccess ? 2 : 0), otherClients);
        var actualMessages = environment.exchange(sender.username(), moveCommand, numExpectedMessages, waitTime);

        MessageAsserter userAsserter = WebSocketTestUtils::assertLoadGameMessage;
        MessageAsserter inGameAsserter = WebSocketTestUtils::assertMoveMadePair;
        if (extraNotification) {
            userAsserter = WebSocketTestUtils::assertLoadGameWithExtra;
            inGameAsserter = WebSocketTestUtils::assertMoveMadeWithExtra;
        }
        assertCommandMessages(actualMessages, expectSuccess, sender.username(), userAsserter, inGame, inGameAsserter,
                otherClients);
    }

    static void resign(WebsocketUser sender, int gameID, boolean expectSuccess, Set<String> inGame,
                       Set<String> otherClients) {
        TestCommand resignCommand = new TestCommand(UserGameCommand.CommandType.RESIGN, sender.authToken(), gameID);
        var numExpectedMessages = expectedMessages(sender, 1, inGame, (expectSuccess ? 1 : 0), otherClients);
        var actualMessages = environment.exchange(sender.username(), resignCommand, numExpectedMessages, waitTime);

        assertCommandMessages(actualMessages, expectSuccess, sender.username(), WebSocketTestUtils::assertNotificationMessage,
                inGame, WebSocketTestUtils::assertNotificationMessage, otherClients);
    }

    static void leave(WebsocketUser sender, int gameID, Set<String> inGame, Set<String> otherClients) {
        TestCommand leaveCommand = new TestCommand(UserGameCommand.CommandType.LEAVE, sender.authToken(), gameID);
        var numExpectedMessages = expectedMessages(sender, 0, inGame, 1, otherClients);
        var actualMessages = environment.exchange(sender.username(), leaveCommand, numExpectedMessages, waitTime);

        assertCommandMessages(actualMessages, true, sender.username(), WebSocketTestUtils::assertNoMessagesLeave,
                inGame, WebSocketTestUtils::assertNotificationMessage, otherClients);
    }

    private static Map<String, Integer> expectedMessages(WebsocketUser sender, int senderExpected, Set<String> inGame,
                                                         int inGameExpected, Set<String> otherClients) {
        Map<String, Integer> expectedMessages = new HashMap<>();
        expectedMessages.put(sender.username(), senderExpected);
        expectedMessages.putAll(inGame.stream().collect(Collectors.toMap(Function.identity(), s -> inGameExpected)));
        expectedMessages.putAll(otherClients.stream().collect(Collectors.toMap(Function.identity(), s -> 0)));
        return expectedMessages;
    }

    private static void assertCommandMessages(Map<String, List<TestMessage>> messages, boolean expectSuccess,
                                              String username, MessageAsserter userAsserter, Set<String> inGame,
                                              MessageAsserter inGameAsserter, Set<String> otherClients) {
        if (!expectSuccess) {
            userAsserter = WebSocketTestUtils::assertErrorMessage;
            inGameAsserter = WebSocketTestUtils::assertNoMessagesInvalid;
        }
        userAsserter.runAssertions(username, messages.get(username));
        for (String inGameUser : inGame) {
            inGameAsserter.runAssertions(inGameUser, messages.get(inGameUser));
        }
        for (String otherUser : otherClients) {
            assertNoMessagesFromOtherGame(otherUser, messages.get(otherUser));
        }
    }

    private static void assertLoadGame(String username, TestMessage message) {
        Assertions.assertEquals(ServerMessage.ServerMessageType.LOAD_GAME, message.getServerMessageType(),
                String.format("Message for %s was not a LOAD_GAME message: %s", username, message));
        Assertions.assertNotNull(message.getGame(), String.format(
                "%s's LOAD_GAME message did not contain a game (Make sure it's specifically called 'game')", username));
        Assertions.assertNull(message.getMessage(),
                String.format("%s's LOAD_GAME message contained a message: %s", username, message.getMessage()));
        Assertions.assertNull(message.getErrorMessage(),
                String.format("%s's LOAD_GAME message contained an error message: %s", username,
                        message.getErrorMessage()));
    }

    private static void assertNotification(String username, TestMessage message) {
        Assertions.assertEquals(ServerMessage.ServerMessageType.NOTIFICATION, message.getServerMessageType(),
                String.format("Message for %s was not a NOTIFICATION message: %s", username, message));
        Assertions.assertNotNull(message.getMessage(), String.format(
                "%s's NOTIFICATION message did not contain a message (Make sure it's specifically called 'message')",
                username));
        Assertions.assertNull(message.getGame(),
                String.format("%s's NOTIFICATION message contained a game: %s", username, message.getGame()));
        Assertions.assertNull(message.getErrorMessage(),
                String.format("%s's NOTIFICATION message contained an error message: %s", username,
                        message.getErrorMessage()));
    }

    private static void assertError(String username, TestMessage message) {
        Assertions.assertEquals(ServerMessage.ServerMessageType.ERROR, message.getServerMessageType(),
                String.format("Message for %s was not an ERROR message: %s", username, message));
        Assertions.assertNotNull(message.getErrorMessage(), String.format(
                ("%s's ERROR message did not contain an error message (Make sure it's specifically called " +
                        "'errorMessage')"), username));
        Assertions.assertNull(message.getGame(),
                String.format("%s's ERROR message contained a game: %s", username, message.getGame()));
        Assertions.assertNull(message.getMessage(),
                String.format("%s's ERROR message contained a non-error message: %s", username, message.getMessage()));
    }

    private static void assertLoadGameMessage(String username, List<TestMessage> messages) {
        Assertions.assertEquals(1, messages.size(),
                String.format("Expected 1 message for %s, got %s: %s", username, messages.size(), messages));
        assertLoadGame(username, messages.get(0));
    }

    private static void assertNotificationMessage(String username, List<TestMessage> messages) {
        Assertions.assertEquals(1, messages.size(),
                String.format("Expected 1 message for %s, got %s: %s", username, messages.size(), messages));
        assertNotification(username, messages.get(0));
    }

    private static void assertErrorMessage(String username, List<TestMessage> messages) {
        Assertions.assertEquals(1, messages.size(),
                String.format("Expected 1 message for %s, got %s: %s", username, messages.size(), messages));
        assertError(username, messages.get(0));
    }

    private static void assertMoveMadePair(String username, List<TestMessage> messages) {
        Assertions.assertEquals(2, messages.size(),
                String.format("Expected 2 messages for %s, got %s", username, messages.size()));
        messages.sort(Comparator.comparing(TestMessage::getServerMessageType));
        try {
            assertLoadGame(username, messages.get(0));
            assertNotification(username, messages.get(1));
        } catch (AssertionError e) {
            Assertions.fail(String.format("Expected a LOAD_GAME and a NOTIFICATION for %s, got %s", username,
                    messages.reversed()), e);
        }
    }

    private static void assertMoveMadeWithExtra(String username, List<TestMessage> messages) {
        Assertions.assertTrue(messages.size() == 2 || messages.size() == 3,
                "Expected 2 or 3 messages, got " + messages.size());
        messages.sort(Comparator.comparing(TestMessage::getServerMessageType));
        try {
            assertLoadGame(username, messages.get(0));
            assertNotification(username, messages.get(1));
            if (messages.size() == 3) {
                assertNotification(username, messages.get(2));
            }
        } catch (AssertionError e) {
            Assertions.fail(String.format("Expected a LOAD_GAME and 1 or 2 NOTIFICATION's for %s, got %s", username,
                    messages.reversed()), e);
        }
    }

    private static void assertLoadGameWithExtra(String username, List<TestMessage> messages) {
        Assertions.assertTrue(messages.size() == 1 || messages.size() == 2,
                "Expected 1 or 2 messages, got " + messages.size());
        messages.sort(Comparator.comparing(TestMessage::getServerMessageType));
        try {
            assertLoadGame(username, messages.get(0));
            if (messages.size() == 2) {
                assertNotification(username, messages.get(1));
            }
        } catch (AssertionError e) {
            Assertions.fail(String.format("Expected a LOAD_GAME and an optional NOTIFICATION for %s, got %s", username,
                    messages.reversed()), e);
        }
    }

    private static void assertNoMessages(String username, List<TestMessage> messages, String description) {
        Assertions.assertTrue(messages.isEmpty(),
                String.format("%s got a message after %s. messages: %s", username, description, messages));
    }

    private static void assertNoMessagesInvalid(String username, List<TestMessage> messages) {
        assertNoMessages(username, messages, "another user sent an invalid command");
    }

    private static void assertNoMessagesLeave(String username, List<TestMessage> messages) {
        assertNoMessages(username, messages, "leaving a game");
    }

    private static void assertNoMessagesFromOtherGame(String username, List<TestMessage> messages) {
        assertNoMessages(username, messages,
                "a user from a different game or a game WebSocketTestUtils user previously left sent a command");
    }

    @FunctionalInterface
    private static interface MessageAsserter {
        void runAssertions(String username, List<TestMessage> messages);
    }
}
