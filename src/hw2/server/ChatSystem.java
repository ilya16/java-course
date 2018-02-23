package hw2.server;

import hw2.chat.Chat;
import hw2.utils.Status;
import hw2.utils.StatusMonitor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    /** Lock and Monitor used during the interaction with other Server modules */
    private final StatusMonitor monitor;

    /**
     * Constructor
     *
     * @param serverSocket          ServerSocket object
     * @param registrationModule    RegistrationModule object
     * @param monitor   server status monitor
     */
    ChatSystem(ServerSocket serverSocket, RegistrationModule registrationModule, StatusMonitor monitor) {
        this.serverSocket = serverSocket;
        this.registrationModule = registrationModule;
        this.monitor = monitor;
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

        Status serverStatus;

        synchronized (monitor) {
            serverStatus = monitor.getStatus();
        }

        while (serverStatus == Status.ON) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("New Connection Accepted!");
                Connection connection = new Connection(socket, chats, registrationModule);
                connections.add(connection);

                Thread connectionThread = new Thread(connection);
                connectionThread.setName("Connection-" + connections.size());
                connectionThread.start();
            } catch (SocketTimeoutException e) {
                System.out.println("No new connections accepted during the last 5 seconds");
            } catch (IOException e) {
                System.out.println("An error occurred while accepting the socket connection");
            }
            synchronized (monitor) {
                serverStatus = monitor.getStatus();
            }
            connections = connections.stream().filter(x -> !x.isClosed()).collect(Collectors.toList());
        }

        System.out.println("Chat System has finished its execution");
    }
}
