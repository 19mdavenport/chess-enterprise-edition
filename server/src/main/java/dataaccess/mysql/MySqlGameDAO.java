package dataaccess.mysql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

public class MySqlGameDAO extends MySqlDAO implements GameDAO {
    public MySqlGameDAO() throws DataAccessException {}

    @Override
    public void clear() throws DataAccessException {
        executeUpdate("TRUNCATE TABLE game;");
    }

    @Override
    public GameData findGame(int gameID) throws DataAccessException {
        return executeQuery("SELECT * FROM game WHERE gameID = ?;", rs -> {
            if (!rs.next()) {
                return null;
            }
            return readGame(rs);
        }, gameID);
    }

    @Override
    public Collection<GameData> findAllGames() throws DataAccessException {
        return executeQuery("SELECT * FROM game;", rs -> {
            Collection<GameData> ret = new HashSet<>();
            while (rs.next()) {
                ret.add(readGame(rs));
            }
            return ret;
        });
    }

    @Override
    public GameData insertGame(GameData game) throws DataAccessException {
        int gameId =
                executeUpdate("INSERT INTO game (gameName, whiteUsername, blackUsername, game) VALUES (?, ?, ?, ?);",
                        game.gameName(), game.whiteUsername(), game.blackUsername(), game.game());
        return new GameData(gameId, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
    }


    @Override
    public void updateGame(GameData game) throws DataAccessException {
        executeUpdate("UPDATE game SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameID = ?",
                game.whiteUsername(), game.blackUsername(), game.game(), game.gameID());
    }

    @Override
    protected String[] getCreateStatements() {
        return new String[]{"""
            CREATE TABLE IF NOT EXISTS `game` (
                `gameID` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                `gameName` VARCHAR(64) NOT NULL,
                `whiteUsername` VARCHAR(64),
                `blackUsername` VARCHAR(64),
                `game` LONGTEXT NOT NULL
            )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """};
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"),
                rs.getString("gameName"), new Gson().fromJson(rs.getString("game"), ChessGame.class));
    }
}
