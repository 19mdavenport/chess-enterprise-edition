package web;

import data.DataCache;
import model.*;
import serialize.Serializer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class ServerFacade {

    private final String url;


    public ServerFacade(String url) {
        this.url = url;
    }


    public GameData createGame(GameData request) {
        return execute("/game", "POST", request, GameData.class);
    }


    public void joinGame(JoinGameRequest request) {
        execute("/game", "PUT", request, null);
    }


    public ListGamesResponse listGames() {
        return execute("/game", "GET", null, ListGamesResponse.class);
    }


    public AuthData login(UserData request) {
        AuthData out = execute("/session", "POST", request, AuthData.class);
        DataCache.getInstance().setAuthToken(out.authToken());
        return out;
    }


    public void logout() {
        execute("/session", "DELETE", null, null);
    }


    public AuthData register(UserData request) {
        AuthData out = execute("/user", "POST", request, AuthData.class);
        DataCache.getInstance().setAuthToken(out.authToken());
        return out;
    }


    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (InputStreamReader sr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            char[] buf = new char[1024];
            int len;
            while ((len = sr.read(buf)) > 0) {
                sb.append(buf, 0, len);
            }
            return sb.toString();
        }
    }


    private static void writeString(String str, OutputStream os) throws IOException {
        try (OutputStreamWriter sw = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
            sw.write(str);
            sw.flush();
        }
    }


    private <T> T execute(String apiEndpoint, String requestMethod, Object request,
                          Class<T> responseClass) {
        try {
            boolean requestPresent = request != null;

            URL url = new URI(this.url + apiEndpoint).toURL();

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(requestMethod.toUpperCase(Locale.ROOT));
            http.setDoOutput(requestPresent);
            http.addRequestProperty("Accept", "application/json");

            String authtoken = DataCache.getInstance().getAuthToken();
            if (authtoken != null) {
                http.addRequestProperty("Authorization", authtoken);
            }


            http.connect();

            if (requestPresent) {
                OutputStream reqBody = http.getOutputStream();
                writeString(Serializer.serialize(request), reqBody);
                reqBody.close();
            }


            if (!(http.getResponseCode() / 100 == 2)) {
                throw new ResponseException(String.format("Response was %d %s", http.getResponseCode(), http.getResponseMessage()));
            }

            String resp = readString(http.getInputStream());
            if(responseClass != null) {
                return Serializer.deserialize(resp, responseClass);
            }
            return null;
        } catch (IOException | URISyntaxException e) {
            throw new ResponseException("Error connecting to server", e);
        }
    }


}
