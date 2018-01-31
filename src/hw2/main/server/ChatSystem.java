package hw2.main.server;

import hw2.main.chat.Chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatSystem implements Runnable {
    ServerSocket serverSocket;
    ArrayList<Connection> connections;
    RegistrationModule registrationModule;
    ArrayList<Chat> chats;


    public ChatSystem(ServerSocket serverSocket, RegistrationModule registrationModule) {
        this.serverSocket = serverSocket;
        this.registrationModule = registrationModule;
        this.connections = new ArrayList<>();
        this.chats = new ArrayList<>();
    }

    public Chat createChat() {
        Chat chat = new Chat(chats.size() + 1);
        chats.add(chat);
        return chat;
    }

    @Override
    public void run() {
        System.out.println("Chat system was successfully started");

        ArrayList<Socket> sockets = new ArrayList<>();
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("New Connection Accepted!");
                Connection connection = new Connection(socket, chats, registrationModule);
                connections.add(connection);

                Thread connectionThread = new Thread(connection);
                connectionThread.setName("Connection-" + connections.size());
                connectionThread.start();


            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

//        System.out.println("Chat System was turned off");
    }
}
