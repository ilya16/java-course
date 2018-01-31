package hw2.client;

import hw2.chat.Message;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Receives messages through the socket.
 *
 * @author Ilya Borovik
 */
public class MessageReceiver implements Receiver, Runnable {

    /** Stream through which messages are received */
    private ObjectInputStream inputStream;

    /** Lock used during the interaction with Settings module */
    private final StatusMonitor monitor;

    /**
     * Constructor
     *
     * @param inputStream   stream through which messages are received
     * @param monitor       status monitor
     */
    MessageReceiver(ObjectInputStream inputStream, StatusMonitor monitor) {
        this.inputStream = inputStream;
        this.monitor = monitor;
    }

    /**
     * Receives the object through the ObjectInputStream.
     *
     * @param inputStream   stream through which object is received
     *
     * @throws IOException  if an error occurs while receiving the object
     */
    @Override
    public Object receive(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        return inputStream.readObject();
    }

    /**
     * Starts the MessageReceiver.
     */
    @Override
    public void run() {
        Message message;

        Status currentStatus;
        synchronized (monitor) {
            currentStatus = monitor.getStatus();
        }
        while (true) {
            try {
                if (currentStatus == Status.SETTINGS_ON) {
                    synchronized (monitor) {
                        currentStatus = monitor.getStatus();
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                } else {
                    message = (Message) receive(inputStream);

                    if ("\\exit".equals(message.getText())) {
                        return;
                    }

                    System.out.println(message);
                }

            } catch (EOFException e) {
                System.out.println("Connection with the Server is lost.");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    System.out.println("An error occurred");
                }
            } catch (IOException e) {
                System.out.println("An error occurred while receiving the message.");
            } catch (ClassNotFoundException e) {
                System.out.println("An error occurred. Class Message is not found. Message was not received.");
            }
        }
    }
}
