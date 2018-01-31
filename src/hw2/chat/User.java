package hw2.chat;

import java.io.Serializable;

/**
 * Stores the information about the User:
 * identifier in the Chat System, username and password.
 *
 * @author Ilya Borovik
 */
public class User implements Serializable {

    /** User identifier */
    private int userID;

    /** User information */
    private String username;
    private String password;

    /**
     * Constructor
     *
     * @param userID    user identifier
     * @param username  user login
     * @param password  user password
     */
    public User(int userID, String username, String password) {
        this.userID = userID;
        this.username = username;
        this.password = password;
    }

    /** Getters and Setters */
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
