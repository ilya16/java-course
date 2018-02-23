package hw2.client;

import hw2.utils.Status;
import hw2.utils.StatusMonitor;
import hw2.chat.User;

import java.io.*;
import java.net.Socket;

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
            socket.setSoTimeout(100);
        } catch (IOException e) {
            System.out.println("Connection with the Server can not be established. Please, try again");
            return;
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {

            System.out.println("Welcome to the Chat System!");
            System.out.println("Type \\auth for authorization using existing account\n" +
                    "Type \\reg for the registration");

            /* modules of the client app */
            AuthorizationModule authorizationModule = new AuthorizationModule(inputStream, outputStream);
            SettingsModule settingsModule;

            /* user authorization */
            authorizationModule.authorize();
            User user = authorizationModule.getUserInfo();
            int chatID = authorizationModule.selectChat();

            System.out.println("Redirecting you to the chat...");

            StatusMonitor chatStatus = new StatusMonitor(Status.ON);
            MessageSender messageSender = new MessageSender(outputStream, chatID, user, chatStatus);
            MessageReceiver messageReceiver = new MessageReceiver(inputStream, chatStatus);

            settingsModule = new SettingsModule(inputStream, outputStream, chatStatus, user);

            Thread sender = new Thread(messageSender);
            Thread receiver = new Thread(messageReceiver);
            Thread settings = new Thread(settingsModule);

            System.out.printf("======== Chat %d ========\n", chatID);
            System.out.println("> type \\settings to change your registration information\n" +
                    "> type \\exit to close the application");

            sender.start();
            receiver.start();
            settings.start();

            sender.join();
            receiver.join();
            settings.join();

            System.out.println("Chat App has finished its execution.");
        } catch (IOException e) {
            System.out.println("An error occurred while communication with Server. Connection is lost");
        } catch (InterruptedException e) {}
    }
}
