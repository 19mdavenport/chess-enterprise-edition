package handler;

import com.google.gson.Gson;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;

import java.util.Map;


/**
 * Handles Exceptions thrown from the server
 *
 * @param <T> The type of exception
 */
public class ChessServerExceptionHandler<T extends Exception> implements ExceptionHandler<T> {

    private final int responseCode;


    /**
     * @param responseCode HTTP response code to use for this type of Exception
     */
    public ChessServerExceptionHandler(int responseCode) {
        this.responseCode = responseCode;
    }


    @Override
    public void handle(T t, Request request, Response response) {
        if (t.getCause() != null) t.printStackTrace();
        response.status(responseCode);
        response.body(new Gson().toJson(Map.of("message", t.getMessage())));
    }
}
