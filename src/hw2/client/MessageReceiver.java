package hw2.client;

import hw2.chat.Message;
import hw2.utils.Status;
import hw2.utils.StatusMonitor;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketTimeoutException;

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
        Status chatStatus;

        while (true) {
            synchronized (monitor) {
                chatStatus = monitor.getStatus();
            }
            if (chatStatus == Status.SETTINGS) {
                try { Thread.sleep(100); }
                catch (InterruptedException e) {}
            } else if (chatStatus == Status.ON) {
                try {
                    message = (Message) receive(inputStream);

                    System.out.println(message);
                } catch (EOFException e) {
                    // Connection with the Server is lost, closing the app
                    // (The better solution is to send signals until connection is present)
                    synchronized (monitor) {
                        monitor.setStatus(Status.OFF);
                    }
                    return;
                }  catch (SocketTimeoutException e) {
                    // No new messages received during the last 100 milliseconds
                } catch (IOException e) {
                     System.out.println("An error occurred while receiving the message.");
                } catch (ClassNotFoundException e) {
                    System.out.println("An error occurred. Class Message is not found. Message was not received.");
                }
            } else {
                // Status.OFF
                return;
            }
        }
    }
}
