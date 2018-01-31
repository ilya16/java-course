package hw2.chat;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores the information about the Chat:
 * identifier in the Chat System and users
 *
 * @author Ilya Borovik
 */
public class Chat implements Serializable {

    /** Chat identifier */
    private int chatID;

    /** Users that have access to the chat */
    private List<User> users;

    /** Output Streams associated with users when they open connections */
    private List<ObjectOutputStream> outputStreams;

    /**
     * Constructor
     *
     * @param chatID chat identifier
     */
    public Chat(int chatID) {
        this.chatID = chatID;
        users = new ArrayList<>();
        outputStreams = new ArrayList<>();
    }

    /**
     * Sets users for a chat
     *
     * @param users a list of users
     */
    public void setUsers(List<User> users) {
        this.users = users;
    }

    /**
     * Returns chat identifier
     *
     * @return chat identifier
     */
    public int getChatID() {
        return chatID;
    }

    /**
     * Adds new user to a chat
     *
     * @param user User object to be added
     */
    public void addUser(User user) {
        users.add(user);
    }

    /**
     * Removes the user from the chat
     *
     * @param user User object to be removed
     */
    public void removeUser(User user) {
        users.remove(user);
    }

    /**
     * Adds new ObjectOutputStream to a chat instance
     *
     * @param outputStream stream to be added
     */
    public void addOutputStream(ObjectOutputStream outputStream) {
        outputStreams.add(outputStream);
    }

    /**
     * Removes the ObjectOutputStream from the chat instance
     *
     * @param outputStream stream to be removed
     */
    public void removeOutputStream(ObjectOutputStream outputStream) {
        outputStreams.remove(outputStream);
    }

    /**
     * Sends the message to all chat members
     *
     * @param message       a Message object to be sent
     * @param outputStream
     */
    public void sendMessage(Message message, ObjectOutputStream outputStream) {
        ArrayList<ObjectOutputStream> closedStreams = new ArrayList<>();
        for (ObjectOutputStream oos : outputStreams) {
            if (oos != outputStream) {
                try {
                    oos.writeObject(message);
                    oos.flush();
                } catch (IOException e) {
                    // Socket was closed for that stream
                    closedStreams.add(oos);
                }
            }
        }
        outputStreams.removeAll(closedStreams);
    }
}
