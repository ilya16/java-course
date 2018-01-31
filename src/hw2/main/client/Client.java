package hw2.main.client;

import hw2.main.chat.Chat;
import hw2.main.chat.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
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

            Thread sender = new Thread(new MessageSender(socket, chatID, user, outputStream));
            Thread receiver = new Thread(new MessageReceiver(socket, inputStream));
            System.out.printf("======== Chat %d ========\n", chatID);

            sender.start();
            receiver.start();

            sender.join();
            receiver.join();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private static void authorize(ObjectInputStream inputStream, ObjectOutputStream outputStream) {
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

            try {
                outputStream.writeUTF(userAction);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.print("Username: ");
            username = in.next();
            System.out.print("Password: ");
            password = in.next();

            try {
                outputStream.writeUTF(username);
                outputStream.writeUTF(password);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
                try {
                    serverResponse = inputStream.readUTF();
                    System.out.println(serverResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (authorizationSuccess && "\\reg".equals(userAction)) {
                System.out.println("Successfully registered");
                authorizationSuccess = false;
                userAction = "\\auth";
            } else {
                userAction = "";
            }
        }
    }

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

    private static int selectChat(ObjectInputStream inputStream, ObjectOutputStream outputStream) throws IOException {
        /* getting chat */
        int chatID = 1;
        ArrayList<Integer> chats = null;
        try {
            chats = (ArrayList<Integer>)inputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        /*
         * Only one chat is available, hence user always cannot select
         * For more chat this part of the code should be changed with user prompt
         */
        if (chats != null) {
            chatID = chats.get(0);
        }

        outputStream.writeInt(chatID);
        outputStream.flush();
        return chatID;
    }
}
