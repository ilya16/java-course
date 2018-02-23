package hw2.server;

import hw2.chat.Chat;
import hw2.chat.Message;
import hw2.chat.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Serves the connection between the Server and the Client (User).
 *
 * @author Ilya Borovik
 */
public class Connection implements Runnable {

    /** Socket connection object */
    private Socket socket;

    /** List of available chats */
    private List<Chat> chats;

    /** Registration module with all corresponding tools */
    private RegistrationModule registrationModule;

    /** Closed status of the connection with the Client */
    private boolean closed = false;

    /**
     * Constructor
     *
     * @param socket                Socket object
     * @param chats                 list of chats
     * @param registrationModule    Registration module object
     */
    Connection(Socket socket, List<Chat> chats, RegistrationModule registrationModule) {
        this.socket = socket;
        this.chats = chats;
        this.registrationModule = registrationModule;
    }

    /**
     * Starts the connection processor.
     */
    @Override
    public void run() {
        String username = "";
        try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {

            /* authorizing user */
            User user = authorizeUser(inputStream, outputStream);

            /* sending information about the user to the Client */
            sendUserInfo(inputStream, outputStream, user);
            username = user.getUsername();

            /* getting the information about selected chat */
            Chat chat = getChat(inputStream, outputStream);

            /* adding user to the chat */
            chat.addUser(user);
            chat.addOutputStream(outputStream);

            /* starting processing messages from the user */
            processUserMessages(inputStream, outputStream, user, chat);

        } catch (IOException e) {
            System.out.printf("%s\tConnection with the user %s was lost. Socket is closed\n",
                    Thread.currentThread().getName(), username);
            closed = true;
        } catch (ClassNotFoundException e) {
            System.out.printf("%s\tAn error occurred while object was read. Class was not found ar runtime\n",
                    Thread.currentThread().getName());
        }
    }

    /**
     * Returns the closed state of the socket.
     * @return  true if connection is closed
     */
    boolean isClosed() {
        return closed;
    }

    /**
     * Authorizes the user in the system.
     * Includes both registration and authorization
     *
     * @param inputStream   ObjectInputStream object
     * @param outputStream  ObjectOutputStream object
     *
     * @return              authorized user
     *
     * @throws IOException  if an error occurred while communicating through the socket
     */
    private User authorizeUser(ObjectInputStream inputStream, ObjectOutputStream outputStream) throws IOException {
        boolean authorizationSuccess = false;
        String userAction;
        String username;
        String password;
        User user = null;

        System.out.printf("%s\tGetting user information\n",
                Thread.currentThread().getName());

        while (!authorizationSuccess) {
            userAction = inputStream.readUTF();
            System.out.printf("%s\tGot user action: %s\n",
                    Thread.currentThread().getName(), userAction);

            username = inputStream.readUTF();
            password = inputStream.readUTF();

            if ("\\auth".equals(userAction)) {
                System.out.printf("%s\tAuthorization of \"%s\" is happening\n",
                        Thread.currentThread().getName(), username);
                try {
                    user = registrationModule.authorizeUser(username, password);
                    /* sending success response */
                    outputStream.writeBoolean(true);
                    outputStream.flush();
                    authorizationSuccess = true;

                } catch (AuthorizationException e) {
                    System.out.printf("%s\tAn Error occurred during the Authorization: %s\n",
                            Thread.currentThread().getName(), e.getMessage());
                    /* sending error response */
                    outputStream.writeBoolean(false);
                    outputStream.flush();
                    outputStream.writeUTF(e.getMessage());
                    outputStream.flush();
                }
            } else {
                System.out.printf("%s\tRegistration of \"%s\" is happening\n",
                        Thread.currentThread().getName(), username);
                try {
                    user = registrationModule.registerUser(username, password);
                    /* sending success response */
                    outputStream.writeBoolean(true);
                    outputStream.flush();
                } catch (AuthorizationException e) {
                    System.out.printf("%s\tAn Error occurred during the Registration: %s\n",
                            Thread.currentThread().getName(), e.getMessage());
                    /* sending error response */
                    outputStream.writeBoolean(false);
                    outputStream.flush();
                    outputStream.writeUTF(e.getMessage());
                    outputStream.flush();
                }
            }
        }
        return user;
    }

    /**
     * Sends information about the user to the Client application.
     *
     * @param inputStream   ObjectInputStream object
     * @param outputStream  ObjectOutputStream object
     * @param user          User object
     *
     * @return              success of the dispatch
     *
     * @throws IOException  if an error occurred while communicating through the socket
     */
    private boolean sendUserInfo(ObjectInputStream inputStream, ObjectOutputStream outputStream, User user) throws IOException {
        /* sending info about user */
        boolean dispatchIsSuccessful = false;
        while (!dispatchIsSuccessful) {
            outputStream.writeObject(user);
            outputStream.flush();
            System.out.printf("%s\tTrying to sent to a %s information about him/her\n",
                    Thread.currentThread().getName(), user.getUsername());

            dispatchIsSuccessful = inputStream.readBoolean();
        }
        System.out.printf("%s\tUser info was successfully sent to %s\n",
                Thread.currentThread().getName(), user.getUsername());
        return true;
    }

    /**
     * Sends information about available chats to the Client application.
     * Receives the chatID from the user.
     *
     * @param inputStream   ObjectInputStream object
     * @param outputStream  ObjectOutputStream object
     *
     * @return              selected Chat object
     *
     * @throws IOException  if an error occurred while communicating through the socket
     */
    private Chat getChat(ObjectInputStream inputStream, ObjectOutputStream outputStream) throws IOException {
        ArrayList<Integer> chatIDs = new ArrayList<>();
        chats.forEach(chat->chatIDs.add(chat.getChatID()));
        outputStream.writeObject(chatIDs);
        outputStream.flush();
        System.out.printf("%s\tInformation about available chats is sent\n",
                Thread.currentThread().getName());

        int chatID = inputStream.readInt();
        System.out.printf("%s\tChat %d was selected\n",
                Thread.currentThread().getName(), chatID);

        return chats.get(chatID - 1);
    }

    /**
     * Processes messages sent by the user.
     * Redirects them to all members of the chat.
     *
     * @param inputStream   ObjectInputStream object
     * @param outputStream  ObjectOutputStream object
     * @param user          User object that is associated with current socket
     * @param chat          Chat in which communication happens
     *
     * @throws IOException              if an error occurred while communicating through the socket
     * @throws ClassNotFoundException   if Message class is not found in the source codes
     */
    private void processUserMessages(ObjectInputStream inputStream, ObjectOutputStream outputStream,
                                   User user, Chat chat) throws IOException, ClassNotFoundException {
        Message message;
        while (true) {
            message = (Message)inputStream.readObject();
            message.setDate(new Date());

            if ("\\exit".equals(message.getText())) {
                outputStream.writeObject(message);
                outputStream.flush();
                chat.removeOutputStream(outputStream);
                chat.removeUser(user);
                System.out.printf("%s\tUser %s has left the Chat %d\n",
                        Thread.currentThread().getName(), user.getUsername(), chat.getChatID());
                return;
            } else if ("\\settings".equals(message.getText())) {
                System.out.printf("%s\tUser %s has went to the Settings Module\n",
                        Thread.currentThread().getName(), user.getUsername());
                updateUserInfo(inputStream, outputStream, user);
            } else {
                System.out.printf("%s\tMessage from %s: %s\n",
                        Thread.currentThread().getName(), message.getSender().getUsername(), message.getText());

                chat.sendMessage(message, outputStream);

                System.out.printf("%s\tSent to The Chat!\n",
                        Thread.currentThread().getName());
            }
        }
    }

    /**
     * Processes the updates of user information from the Client application.
     *
     * @param inputStream   ObjectInputStream object
     * @param outputStream  ObjectOutputStream object
     * @param user          User object
     *
     * @return              success of the update
     *
     * @throws IOException  if an error occurred while communicating through the socket
     */
    private boolean updateUserInfo(ObjectInputStream inputStream,
                                ObjectOutputStream outputStream, User user) throws IOException {
        boolean updateSuccess = false;
        String userAction;
        String username = user.getUsername();
        String newUsername = "";

        System.out.printf("%s\tGetting setting action\n",
                Thread.currentThread().getName());

        while (!updateSuccess) {
            userAction = inputStream.readUTF();
            System.out.printf("%s\tGot user action: %s\n",
                    Thread.currentThread().getName(), userAction);

            if ("\\username".equals(userAction)) {
                System.out.printf("%s\tChange of the username of \"%s\" is happening\n",
                        Thread.currentThread().getName(), username);

                while (!updateSuccess) {
                    newUsername = inputStream.readUTF();

                    updateSuccess = !username.equals(newUsername)
                            && !registrationModule.usernameExists(newUsername);

                    outputStream.writeBoolean(updateSuccess);
                    outputStream.flush();

                    if (!updateSuccess) {
                        System.out.printf("%s\tError: username \"%s\" is already taken\n",
                                Thread.currentThread().getName(), newUsername);
                    }
                }

                registrationModule.updateUsername(username, newUsername);
                System.out.printf("%s\tUsername was successfully changed to \"%s\"\n",
                        Thread.currentThread().getName(), newUsername);

            } else if ("\\password".equals(userAction)) {
                System.out.printf("%s\tChange of the password of \"%s\" is happening\n",
                        Thread.currentThread().getName(), username);

                String newPassword = inputStream.readUTF();

                registrationModule.updatePassword(username, newPassword);

                outputStream.writeBoolean(true);
                outputStream.flush();

                System.out.printf("%s\tPassword of a user %s was successfully updated\n",
                        Thread.currentThread().getName(), username);
            }
        }
        return true;
    }
}
