package handler;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import service.ChessServerException;
import spark.Request;
import spark.Response;
import spark.Route;

import java.net.HttpURLConnection;

public abstract class HttpHandler<T> implements Route {

    private final DataAccess dataAccess;


    public HttpHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @Override
    public Object handle(Request request, Response response) throws ChessServerException {
        Gson gson = new Gson();
        String authToken = request.headers("Authorization");

        T requestObject = null;
        Class<T> requestClass = getRequestClass();
        if(requestClass != null) {
            requestObject = gson.fromJson(request.body(), requestClass);
        }

        Object result = getServiceResult(dataAccess, requestObject, authToken);

        response.status(HttpURLConnection.HTTP_OK);

        return gson.toJson(result);
    }

    protected abstract Class<T> getRequestClass();

    protected abstract Object getServiceResult(DataAccess dataAccess, T request, String authtoken) throws ChessServerException;


}
