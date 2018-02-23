package hw2.client;

import hw2.utils.Status;
import hw2.utils.StatusMonitor;
import hw2.chat.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Implements the logic of the Client application.
 *
 * @author Ilya Borovik
 */
public class Client {
    /**
     * Starts the Client application.
     *
     * @param args array with one element - port number
     */
    public static void main(String[] args) {
        int portNumber = 9099;
        if (args.length == 0) {
            System.out.printf("Port number is not specified. Using default one: %d\n", portNumber);
        } else {
            portNumber = Integer.valueOf(args[0]);
            System.out.printf("Using port number: %d\n", portNumber);
        }

        Socket socket;
        try {
            socket = new Socket("127.0.0.1", portNumber);
        } catch (IOException e) {
            System.out.println("Connection with the Server can not be established. Please, try again");
            return;
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {

            System.out.println("Welcome to the Chat System!");
            System.out.println("Type \\auth for authorization using existing account\n" +
                    "Type \\reg for the registration");

            authorize(inputStream, outputStream);
            User user = getUserInfo(inputStream, outputStream);
            int chatID = selectChat(inputStream, outputStream);

            System.out.println("Redirecting you to the chat...");

            StatusMonitor statusMonitor = new StatusMonitor(Status.OFF);
            MessageSender messageSender = new MessageSender(outputStream, chatID, user, statusMonitor);
            MessageReceiver messageReceiver = new MessageReceiver(inputStream, statusMonitor);

            /* part of the settings task */
            messageSender.setSettings(new Settings(inputStream, outputStream, user));

            Thread sender = new Thread(messageSender);
            Thread receiver = new Thread(messageReceiver);

            System.out.printf("======== Chat %d ========\n", chatID);
            System.out.println("> type \\settings to change your registration information\n" +
                    "> type \\exit to close the application");

            sender.start();
            receiver.start();

            sender.join();
            receiver.join();
        } catch (IOException e) {
            System.out.println("An error occurred while communication with Server. Connection is lost");
        } catch (InterruptedException e) {
        }
    }

    /**
     * Authorizes the user in the system.
     * Includes both registration and authorization
     *
     * @param inputStream   ObjectInputStream object
     * @param outputStream  ObjectOutputStream object
     *
     * @throws IOException  if an error occurred while communicating through the socket
     */
    private static void authorize(ObjectInputStream inputStream, ObjectOutputStream outputStream) throws IOException {
        Scanner in = new Scanner(System.in);

        boolean authorizationSuccess = false;
        String userAction = "";
        String username;
        String password;
        String serverResponse;

        while (!authorizationSuccess) {
            while (!"\\auth".equals(userAction) && !"\\reg".equals(userAction)) {
                System.out.print("Your action: ");
                userAction = in.next();
            }

            if ("\\auth".equals(userAction)) {
                System.out.println("Authentication");
            } else {
                System.out.println("Registration");
            }

            outputStream.writeUTF(userAction);
            outputStream.flush();

            System.out.print("Username: ");
            username = in.next();
            System.out.print("Password: ");
            password = in.next();

            outputStream.writeUTF(username);
            outputStream.writeUTF(password);
            outputStream.flush();

            if ("\\auth".equals(userAction)) {
                System.out.println("Authorizing...");
            } else {
                System.out.println("Registration is being processed...");
            }

            try {
                authorizationSuccess = inputStream.readBoolean();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!authorizationSuccess) {
                /* Error occurred */
                serverResponse = inputStream.readUTF();
                System.out.println(serverResponse);
            }

            if (authorizationSuccess && "\\reg".equals(userAction)) {
                System.out.println("Successfully registered");
                authorizationSuccess = false;
                /* next action */
                userAction = "\\auth";
            } else {
                userAction = "";
            }
        }
    }

    /**
     * Gets information about the user from the Server.
     *
     * @param inputStream   ObjectInputStream object
     * @param outputStream  ObjectOutputStream object
     *
     * @return              User object
     *
     * @throws IOException  if an error occurred while communicating through the socket
     */
    private static User getUserInfo(ObjectInputStream inputStream, ObjectOutputStream outputStream) throws IOException {
        /* getting user */
        User user = null;
        while (user == null) {
            try {
                user = (User) inputStream.readObject();
                outputStream.writeBoolean(true);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                outputStream.writeBoolean(false);
            } catch (IOException e) {
                e.printStackTrace();
                outputStream.writeBoolean(false);
            }
            finally {
                outputStream.flush();
            }
        }
        return user;
    }

    /**
     * Process the user's choice of the chat (room).
     *
     * @param inputStream   ObjectInputStream object
     * @param outputStream  ObjectOutputStream object
     *
     * @return              chat identifier
     *
     * @throws IOException  if an error occurred while communicating through the socket
     */
    private static int selectChat(ObjectInputStream inputStream, ObjectOutputStream outputStream) throws IOException {
        /* getting chat */
        int chatID = 1;
        ArrayList<Integer> chats = null;
        try {
            chats = (ArrayList<Integer>)inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        /*
         * Only one chat is available, hence user cannot select the id.
         * For more chat this part of the code should be changed with user prompt.
         */
        if (chats != null) {
            chatID = chats.get(0);
        }

        outputStream.writeInt(chatID);
        outputStream.flush();
        return chatID;
    }
}
