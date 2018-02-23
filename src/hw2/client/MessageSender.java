package hw2.client;

import hw2.chat.Message;
import hw2.utils.Status;
import hw2.utils.StatusMonitor;
import hw2.chat.User;

import java.io.IOException;
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
        Status chatStatus;

        while (!"\\exit".equals(messageText)) {
            messageText = scanner.nextLine();

            if (!"".equals(messageText)) {
                message = new Message(messageText, chatID, user);

                try {
                    send(message, outputStream);
                    System.out.println(message);

                    if ("\\settings".equals(messageText)) {
                        synchronized (monitor) {
                            monitor.setStatus(Status.SETTINGS);
                        }

                        do {
                            try { Thread.sleep(100); }
                            catch (InterruptedException e) {}
                            synchronized (monitor) {
                                chatStatus = monitor.getStatus();
                            }
                        } while (chatStatus == Status.SETTINGS);

                        System.out.printf("======== Chat %d ========\n", chatID);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("An error occurred while sending your message. " +
                            "Please, try to send it again.");
                }
            }
        }

        synchronized (monitor) {
            monitor.setStatus(Status.OFF);
        }
    }
}
