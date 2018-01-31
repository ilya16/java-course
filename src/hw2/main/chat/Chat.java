package hw2.main.chat;

import hw2.main.server.Connection;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Chat implements Serializable {
    int chatID;
    ArrayList<ObjectOutputStream> outputStreams;
    ArrayList<User> users;

    public Chat() {
    }

    public Chat(int chatID) {
        this.chatID = chatID;
        outputStreams = new ArrayList<>();
        users = new ArrayList<>();
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public void stop() {

    }

    public int getChatID() {
        return chatID;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    public void addOutputStream(ObjectOutputStream outputStream) {
        outputStreams.add(outputStream);
    }

    public void removeOutputStream(ObjectOutputStream outputStream) {
        outputStreams.remove(outputStream);
    }

    public void sendMessage(Message message, ObjectOutputStream outputStream) {
        for (ObjectOutputStream oos : outputStreams) {
            if (oos != outputStream) {
                try {
                    oos.writeObject(message);
                    oos.flush();
                } catch (IOException e) {
                    // Socket closed for that stream
                    // outputStreams.remove(oos);
                }
            }
        }
    }
}
