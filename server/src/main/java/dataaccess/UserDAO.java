package dataaccess;

import model.UserData;

public interface UserDAO {
    /**
     * Clears the database of all users
     *
     * @throws DataAccessException A DataAccessException is thrown if data cannot be accessed for any reason
     */
    void clear() throws DataAccessException;


    /**
     * Inserts the provided User into the database
     *
     * @param user The User to insert
     * @throws DataAccessException A DataAccessException is thrown if data cannot be accessed for any reason
     */
    void insertUser(UserData user) throws DataAccessException;


    /**
     * Returns the User matching the provided username
     *
     * @param username The username of the User to look for
     * @return The requested User, or {@code null} if it couldn't be found
     * @throws DataAccessException A DataAccessException is thrown if data cannot be accessed for any reason
     */
    boolean usernameExists(String username) throws DataAccessException;

    boolean verifyUser(UserData user) throws DataAccessException;
}
