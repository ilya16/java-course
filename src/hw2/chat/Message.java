package hw2.chat;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

/**
 * Stores the information about the Message.
 *
 * @author Ilya Borovik
 */
public class Message implements Serializable {

    /** Message variables */
    private String text;
    private int chatID;
    private User sender;
    private Date date;

    /** DateFormat instance */
    private final DateFormat dateFormat = DateFormat.getDateInstance();

    /**
     * Constructor
     * @param text      message text
     * @param chatID    chat id of the chat that is related to this message
     * @param sender    User object of the sender
     */
    public Message(String text, int chatID, User sender) {
        this.text = text;
        this.chatID = chatID;
        this.sender = sender;
        this.date = new Date();
    }

    /** Getters and Setters */
    public String getText() {
        return text;
    }

    public int getChatID() {
        return chatID;
    }

    public User getSender() {
        return sender;
    }

    public Date getDate() {
        return date;
    }

    /**
     * Returns a pretty String representation of the Message
     *
     * @return  String representation
     */
    @Override
    public String toString() {
        // TODO: fix date
        return String.format("%s\t%s:\t%s",
                dateFormat.format(date), sender.getUsername(), text);
    }
}
