package hw2.main.chat;

import java.io.Serializable;

public class User implements Serializable {
    int userID;
    String username;
    String password;

    public User(int userID, String username) {
        this.userID = userID;
        this.username = username;
    }

    public User(int userID, String username, String password) {
        this.userID = userID;
        this.username = username;
        this.password = password;
    }

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
