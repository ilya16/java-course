package hw2.server;

import java.io.*;
import java.net.ServerSocket;

/**
 * Implements the logic of the Server application.
 *
 * @author Ilya Borovik
 */
public class Server {
    /**
     * Starts the Server application.
     *
     * @param args array with one element - port number
     */
    public static void main(String[] args) {

        /* getting port number */
        int portNumber = 9099;
        if (args.length != 1) {
            System.out.printf("Port number is not specified. Using default one: %d\n", portNumber);
        } else {
            portNumber = Integer.valueOf(args[0]);
        }
            System.out.printf("Using port number: %d\n", portNumber);

        /* launching the Server Socket */
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.printf("An error occurred while opening the server socket: \n\t%s\n" +
                            "Please, relaunch the Server app.", e.getMessage());
            return;
        }

        /* initialization of registration module with users and chat system */
        RegistrationModule registrationModule = new RegistrationModule("src/hw2/data/users.dat");
        ChatSystem chatSystem = new ChatSystem(serverSocket, registrationModule);

        /* one chat room */
        chatSystem.createChat();

        /* starting the chat system */
        Thread chatSystemThread = new Thread(chatSystem);
        chatSystemThread.start();

        try {
            chatSystemThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Server has stopped its execution.");
    }
}
