package websocket;

import org.eclipse.jetty.websocket.api.Session;
import serialize.Serializer;
import websocket.messages.ErrorMessage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private static final ByteBuffer PING_BUFFER = ByteBuffer.wrap("PING".getBytes());

    private final Map<Integer, Set<Session>> sessions = new ConcurrentHashMap<>();

    public ConnectionManager() {
        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(10000);
                    pingClients();
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void pingClients() throws IOException {
        for (Set<Session> set : sessions.values()) {
            for (Session session : set) {
                session.getRemote().sendPing(PING_BUFFER);
            }
        }
    }

    public void addSession(int gameId, Session session) {
        if(!sessions.containsKey(gameId)) {
            sessions.put(gameId, Collections.synchronizedSet(new HashSet<>()));
        }
        sessions.get(gameId).add(session);
    }

    public void removeSession(int gameId, Session session) {
        if(sessions.containsKey(gameId)) {
            sessions.get(gameId).remove(session);
        }
    }

    public void broadcast(String message, int gameId, Session exclude) throws IOException {
        for (Session ses : sessions.get(gameId)) {
            if (ses != exclude) {
                sendMessage(ses, message);
            }
        }
    }

    public void sendError(Session session, String message) throws IOException {
        sendMessage(session, Serializer.serialize(new ErrorMessage(message)));
    }

    public void sendMessage(Session session, String message) throws IOException {
        if (session.isOpen()) {
            session.getRemote().sendString(message);
        }
    }

    public void clear() {
//        sessions.clear();
    }
}
