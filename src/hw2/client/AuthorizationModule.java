package hw2.client;

import hw2.chat.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Implements user authorization methods.
 *
 * @author Ilya Borovik
 */
class AuthorizationModule extends ClientModule {

    /**
     * Constructor
     * @param inputStream   ObjectInputStream of the connection with the Server
     * @param outputStream  ObjectOutputStream of the connection with the Server
     */
    AuthorizationModule(ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        super(inputStream, outputStream);
    }

    /**
     * Authorizes the user in the system.
     * Includes both registration and authorization
     *
     * @throws IOException  if an error occurred while communicating through the socket
     */
    public void authorize() throws IOException {
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
     * @return              User object
     *
     * @throws IOException  if an error occurred while communicating through the socket
     */
    public User getUserInfo() throws IOException {
        /* getting user */
        User user = null;
        while (user == null) {
            try {
                user = (User) inputStream.readObject();
                outputStream.writeBoolean(true);
            } catch (ClassNotFoundException | IOException e) {
                // an error occurred during the communication
                outputStream.writeBoolean(false);
            } finally {
                outputStream.flush();
            }
        }
        return user;
    }

    /**
     * Process the user's choice of the chat (room).
     *
     * @return              chat identifier
     *
     * @throws IOException  if an error occurred while communicating through the socket
     */
    public int selectChat() throws IOException {
        /* getting chat */
        int chatID = 1;
        ArrayList<Integer> chats = null;
        try {
            chats = (ArrayList<Integer>)inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // an error occurred during the communication
            // default chat will be used
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
