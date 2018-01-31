package hw2.main.server;

import java.io.*;
import java.net.ServerSocket;

public class Server {
    public static void main(String[] args) {
        int portNumber = 9099;
        if (args.length == 0) {
            System.out.printf("Port number is not specified. Using default one: %d\n", portNumber);
        } else {
            portNumber = Integer.valueOf(args[0]);
        }
            System.out.printf("Using port number: %d\n", portNumber);

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.printf("An error occurred while opening the server socket: \n\t%s\n" +
                            "Please, relaunch the Server app", e.getMessage());
            return;
        }

//        HashMap<String, User> users = new HashMap<>();
//        users.put("admin", new User(1, "admin", "java-course"));
//        writeUsersToDisk(users);

        RegistrationModule registrationModule = new RegistrationModule();
        ChatSystem chatSystem = new ChatSystem(serverSocket, registrationModule);

        /* one chat room */
        chatSystem.createChat();

        Thread chatSystemThread = new Thread(chatSystem);
        chatSystemThread.start();

        try {
            chatSystemThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
