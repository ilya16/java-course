package hw2.client;

import hw2.chat.User;
import hw2.utils.Status;
import hw2.utils.StatusMonitor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

/**
 * Provides settings functionality to a user.
 *
 * @author Ilya Borovik
 */
public class SettingsModule extends ClientModule implements Runnable {

    /** Lock used during the interaction with Settings module */
    private final StatusMonitor monitor;

    /** Sender of the messages */
    private User user;

    /**
     * Constructor
     *
     * @param inputStream   stream through which messages are received
     * @param outputStream  stream through which messages are sent
     * @param monitor       status monitor
     * @param user          sender of the messages
     */
    SettingsModule(ObjectInputStream inputStream, ObjectOutputStream outputStream,
                   StatusMonitor monitor, User user) {
        super(inputStream, outputStream);
        this.monitor = monitor;
        this.user = user;
    }

    /**
     * Starts the Setting processor.
     */
    @Override
    public void run() {
        Status chatStatus;

        while (true) {
            synchronized (monitor) {
                chatStatus = monitor.getStatus();
            }
            if (chatStatus == Status.OFF) {
                return;
            } else if (chatStatus != Status.SETTINGS) {
                try { Thread.sleep(100); }
                catch (InterruptedException e) {}
            } else {
                boolean updateSuccess = false;
                String userAction;
                String username = user.getUsername();
                String newUsername = "";

                System.out.println("======== Settings ========");
                System.out.println("Type \\username to update your username\n" +
                        "Type \\password to update your password\n" +
                        "Otherwise, you'll be redirected back to the chat");

                Scanner in = new Scanner(System.in);
                System.out.print("Your action: ");
                userAction = in.next();

                try {
                    outputStream.writeUTF(userAction);
                    outputStream.flush();

                    if ("\\username".equals(userAction)) {
                        System.out.println("Change of the username is happening");
                        System.out.printf("Old username: %s\n", username);

                        while (!updateSuccess) {
                            System.out.print("New username: ");

                            newUsername = in.next();
                            outputStream.writeUTF(newUsername);
                            outputStream.flush();

                            updateSuccess = inputStream.readBoolean();

                            /* sending error response */
                            outputStream.writeBoolean(updateSuccess);
                            outputStream.flush();

                            if (!updateSuccess) {
                                System.out.printf("Error: username \"%s\" is already taken\n" +
                                                "Try another one or enter current to proceed further.",
                                        newUsername);
                            }
                        }

                        System.out.printf("Username was successfully changed to \"%s\"\n", newUsername);

                    } else if ("\\password".equals(userAction)) {
                        System.out.println("Change of the password is happening");
                        System.out.print("New password: ");

                        String newPassword = in.next();
                        outputStream.writeUTF(newPassword);
                        outputStream.flush();

                        updateSuccess = inputStream.readBoolean();

                        if (updateSuccess) {
                            System.out.println("Password was successfully updated");
                        }
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred while sending the data. Connection is lost.");
                }

                synchronized (monitor) {
                    monitor.setStatus(Status.ON);
                }
            }
        }
    }
}
