package hw2.main.server;

import hw2.main.chat.Chat;
import hw2.main.chat.Message;
import hw2.main.chat.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Connection implements Runnable {
    Socket socket;
    ArrayList<Chat> chats;
    RegistrationModule registrationModule;


    public Connection(Socket socket, ArrayList<Chat> chats, RegistrationModule registrationModule) {
        this.socket = socket;
        this.chats = chats;
        this.registrationModule = registrationModule;
    }

    @Override
    public void run() {
        String username = "";
        try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {

            User user = authorizeUser(inputStream, outputStream);
            sendUserInfo(inputStream, outputStream, user);
            username = user.getUsername();

            Chat chat = getChat(inputStream, outputStream);

            chat.addUser(user);
            chat.addOutputStream(outputStream);

            processUserMessages(inputStream, outputStream, user, chat);
        } catch (IOException e) {
            System.out.printf("%s\tConnection with the user %s was lost. Socket is closed",
                    Thread.currentThread().getName(), username);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

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
                    outputStream.writeBoolean(true);
                    outputStream.flush();
                    authorizationSuccess = true;

                } catch (AuthorizationException e) {
                    System.out.printf("%s\tAn Error occurred during the Authorization: %s\n",
                            Thread.currentThread().getName(), e.getMessage());
                    /* sending error description */
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
                    outputStream.writeBoolean(true);
                    outputStream.flush();
                } catch (AuthorizationException e) {
                    System.out.printf("%s\tAn Error occurred during the Registration: %s\n",
                            Thread.currentThread().getName(), e.getMessage());
                    /* sending error description */
                    outputStream.writeBoolean(false);
                    outputStream.flush();
                    outputStream.writeUTF(e.getMessage());
                    outputStream.flush();
                }
            }
        }
        return user;
    }

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

    private void processUserMessages(ObjectInputStream inputStream, ObjectOutputStream outputStream,
                                   User user, Chat chat) throws IOException, ClassNotFoundException {
        Message message;
        while (true) {
            message = (Message)inputStream.readObject();

            if ("\\exit".equals(message.getText())) {
                outputStream.writeObject(message);
                outputStream.flush();
                chat.removeOutputStream(outputStream);
                System.out.printf("%s\tUser %s has left the Chat %d\n",
                        Thread.currentThread().getName(), user.getUsername(), chat.getChatID());
                return;
            } else {
                System.out.printf("%s\tMessage from %s: %s\n",
                        Thread.currentThread().getName(), message.getSender().getUsername(), message.getText());

                chat.sendMessage(message, outputStream);

                System.out.printf("%s\tSent to The Chat!\n",
                        Thread.currentThread().getName());
            }
        }
    }
}
