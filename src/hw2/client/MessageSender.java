package hw2.client;

import hw2.chat.Message;
import hw2.chat.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

/**
 * Sends messages through the socket.
 *
 * @author Ilya Borovik
 */
public class MessageSender implements Sender, Runnable {

    /** Stream through which messages are sent */
    private ObjectOutputStream outputStream;

    /** Identifier of the chat to which message is sent */
    private int chatID;

    /** Sender of the messages */
    private User user;

    /** Settings object */
    private Settings settings;

    /** Lock used during the interaction with Settings module */
    private final StatusMonitor monitor;

    /**
     * Constructor
     *
     * @param outputStream  stream through which messages are sent
     * @param chatID        chat identifier
     * @param user          sender of the messages
     * @param monitor       status monitor
     */
    MessageSender(ObjectOutputStream outputStream, int chatID, User user, StatusMonitor monitor) {
        this.outputStream = outputStream;
        this.chatID = chatID;
        this.user = user;
        this.monitor = monitor;
    }

    /**
     * Sends the object through the ObjectOutputStream.
     *
     * @param object        Object to be sent
     * @param outputStream  stream through which object is sent
     *
     * @throws IOException  if an error occurs while sending the object
     */
    @Override
    public void send(Object object, ObjectOutputStream outputStream) throws IOException{
        outputStream.writeObject(object);
        outputStream.flush();
    }

    /**
     * Starts the MessageSender.
     */
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String messageText = "";
        Message message;

        while (!"\\exit".equals(messageText)) {
            messageText = scanner.nextLine();

            message = new Message(messageText, chatID, user);

            try {
                send(message, outputStream);
                System.out.println(message);

                if ("\\settings".equals(messageText)) {
                    synchronized (monitor) {
                        monitor.setStatus(Status.SETTINGS_ON);
                        Thread settingThread = new Thread(settings);
                        settingThread.start();
                        try {
                            settingThread.join();
                        } catch (InterruptedException e) {
                            System.out.println("An error occurred while getting to the Settings Module");
                        }
                        monitor.setStatus(Status.SETTINGS_OFF);
                        System.out.printf("======== Chat %d ========\n", chatID);
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred while sending your message. " +
                        "Please, try to send it again.");
            }
        }
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
