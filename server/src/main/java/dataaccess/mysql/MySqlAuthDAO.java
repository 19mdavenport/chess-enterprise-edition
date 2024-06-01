package dataaccess.mysql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

public class MySqlAuthDAO extends MySqlDAO implements AuthDAO {
    public MySqlAuthDAO() throws DataAccessException {}

    @Override
    public void clear() throws DataAccessException {
        executeUpdate("TRUNCATE TABLE auth;");
    }

    @Override
    public void insertAuth(AuthData authToken) throws DataAccessException {
        String statement = "INSERT INTO auth (authToken, username) VALUES (?, ?);";
        executeUpdate(statement, authToken.authToken(), authToken.username());
    }

    @Override
    public AuthData findAuth(String authToken) throws DataAccessException {
        return executeQuery("SELECT * FROM auth WHERE authToken = ?;", rs -> {
            if (!rs.next()) {
                return null;
            }
            return new AuthData(rs.getString("authToken"), rs.getString("username"));
        }, authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        executeUpdate("DELETE FROM auth WHERE authToken = ?;", authToken);
    }

    @Override
    protected String[] getCreateStatements() {
        return new String[]{"""
            CREATE TABLE IF NOT EXISTS `auth` (
                `authToken` VARCHAR(64) NOT NULL PRIMARY KEY,
                `username` VARCHAR(64) NOT NULL
            )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """};
    }
}
