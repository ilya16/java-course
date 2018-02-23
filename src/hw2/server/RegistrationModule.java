package hw2.server;

import hw2.chat.User;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores information about users.
 * Processes registrations and authorizations of users.
 *
 * @author Ilya Borovik
 */
class RegistrationModule {

    /** Collection with users */
    private Map<String, User> users;

    /** Path to the file with users on disk */
    private String filepath;

    /** Lock Object used for synchronizations */
    private final Object lock = new Object();

    /**
     * Constructor
     */
    RegistrationModule() {
        this.users = new HashMap<>();
    }

    /**
     * Constructor
     *
     * @param users Collection of users
     */
    RegistrationModule(Map<String, User> users) {
        this.users = users;
    }

    /**
     * Constructor
     *
     * @param filepath path to the file with users on disk
     */
    RegistrationModule(String filepath) {
        this.filepath = filepath;
        this.users = readUsersFromDisk();
    }

    /**
     * Checks for the existence of the username in the system.
     *
     * @param username  value to be checked
     * @return          result of the check
     */
    public boolean usernameExists(String username) {
        boolean result;
        synchronized (lock) {
            result = users.containsKey(username);
        }
        return result;
    }

    /**
     * Registers new user in the system.
     *
     * @param username  String value of user's login
     * @param password  String value of the password
     *
     * @return          User object of the registered user
     *
     * @throws AuthorizationException if username already exists in the system
     */
    public User registerUser(String username, String password) throws AuthorizationException {
        if (usernameExists(username)) {
            throw new AuthorizationException(
                    String.format("Username \"%s\" is already taken", username));
        } else {
            synchronized (lock) {
                User newUser = new User(users.size() + 1, username, password);
                users.put(username, newUser);
                save();
                return newUser;
            }
        }
    }

    /**
     * Authorizes user in the system.
     *
     * @param username  String value of user's login
     * @param password  String value of the password
     *
     * @return          User object of the authorized user
     *
     * @throws AuthorizationException if provided login information is not valid
     */
    public User authorizeUser(String username, String password) throws AuthorizationException {
        if (usernameExists(username)) {
            User user;
            synchronized (lock) {
                user = users.get(username);
            }
            if (user.getPassword().equals(password)) {
                return user;
            } else {
                throw new AuthorizationException("Password is wrong");
            }
        } else {
            throw new AuthorizationException(
                    String.format("Username \"%s\" does not exist in the system", username));
        }
    }

    /**
     * Updates the username of the user.
     *
     * @param username      old username
     * @param newUsername   new username
     */
    public void updateUsername(String username, String newUsername) {
        synchronized (lock) {
            User user = users.get(username);
            users.remove(username);
            user.setUsername(newUsername);
            users.put(newUsername, user);
            save();
        }
    }

    /**
     * Updates the password of the user.
     *
     * @param username      user login
     * @param newPassword   new password
     */
    public void updatePassword(String username, String newPassword) {
       synchronized (lock) {
           User user = users.get(username);
           user.setPassword(newPassword);
           users.put(username, user);
           save();
       }
    }

    /**
     * Dumps Collection of users to the disk.
     *
     * @param users Collection of users
     */
    public void writeUsersToDisk(Map<String, User> users) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(
                new FileOutputStream(new File(filepath)))) {
            outputStream.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a Collection of users from the disk
     *
     * @return read Collection of users
     */
    public Map<String, User> readUsersFromDisk() {
        Map<String, User> users = new HashMap<>();
        try (ObjectInputStream inputStream = new ObjectInputStream(
                new FileInputStream(filepath))) {
            users = (HashMap<String, User>)inputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return users;
    }

    /** Saves information in the system */
    private void save() {
        writeUsersToDisk(users);
    }
}
