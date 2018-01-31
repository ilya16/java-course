package hw2.server;

import hw2.chat.Chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the logic of the Chat Application.
 * Stores the information about all chats.
 *
 * @author Ilya Borovik
 */
public class ChatSystem implements Runnable {

    /** Server Socket object */
    private ServerSocket serverSocket;

    /** List of connection with users (clients) */
    private List<Connection> connections;

    /** List of all chats in the Chat System */
    private List<Chat> chats;

    /** Registration module with all corresponding tools */
    private final RegistrationModule registrationModule;

    /**
     * Constructor
     *
     * @param serverSocket          ServerSocket object
     * @param registrationModule    RegistrationModule object
     */
    ChatSystem(ServerSocket serverSocket, RegistrationModule registrationModule) {
        this.serverSocket = serverSocket;
        this.registrationModule = registrationModule;
        this.connections = new ArrayList<>();
        this.chats = new ArrayList<>();
    }

    /**
     * Creates a new Chat (Room) in teh System.
     *
     * @return created Chat object
     */
    public Chat createChat() {
        Chat chat = new Chat(chats.size() + 1);
        chats.add(chat);
        return chat;
    }

    /**
     * Starts the Chat System.
     */
    @Override
    public void run() {
        System.out.println("Chat system was successfully started");

        ArrayList<Socket> sockets = new ArrayList<>();
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                socket.close();
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
    }
}
