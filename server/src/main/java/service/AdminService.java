package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import websocket.WebSocketHandler;

public class AdminService {
    private final DataAccess dataAccess;

    public AdminService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() throws ChessServerException {
        try {
            dataAccess.getAuthDAO().clear();
            dataAccess.getGameDAO().clear();
            dataAccess.getUserDAO().clear();
            WebSocketHandler.getInstance().clear();
        }catch (DataAccessException e) {
            throw new ChessServerException(e);
        }

    }

}
