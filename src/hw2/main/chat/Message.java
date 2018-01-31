package hw2.main.chat;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Message implements Serializable {
    private String text;
    private int chatID;
    private User sender;
    private Date date;

    public Message(String text, int chatID, User sender) {
        this.text = text;
        this.chatID = chatID;
        this.sender = sender;
        this.date = new Date();
    }

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
}
