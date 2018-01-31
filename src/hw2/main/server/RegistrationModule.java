package hw2.main.server;

import hw2.main.chat.User;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegistrationModule {
    private Map<String, User> users;
    private final Object lock = new Object();

    public RegistrationModule() {
        this.users = readUsersFromDisk();
    }

    public RegistrationModule(Map<String, User> users) {
        this.users = users;
    }

    public boolean usernameExists(String username) {
        boolean result;
        synchronized (lock) {
            result = users.containsKey(username);
        }
        return result;
    }

    public User registerUser(String username, String password) throws AuthorizationException {
        if (usernameExists(username)) {
            throw new AuthorizationException(
                    String.format("Username \"%s\" is already taken", username));
        } else {
            synchronized (lock) {
                User newUser = new User(users.size() + 1, username, password);
                users.put(username, newUser);
                return newUser;
            }
        }
    }

    public User authorizeUser(String username, String password) throws AuthorizationException {
        if (usernameExists(username)) {
            User user = null;
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

    public void writeUsersToDisk(Map<String, User> users) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(
                new FileOutputStream(new File("src/hw2/data/users.dat")))) {
            outputStream.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, User> readUsersFromDisk() {
        try (ObjectInputStream inputStream = new ObjectInputStream(
                new FileInputStream("src/hw2/data/users.dat"))) {
            HashMap<String, User> users = (HashMap<String, User>)inputStream.readObject();
            return users;
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
            System.out.println("SD");
        }
        return new HashMap<>();
    }

    public void save() {
        writeUsersToDisk(users);
    }
}
