package hw2.server;

import hw2.utils.Status;
import hw2.utils.StatusMonitor;

import java.util.Scanner;

/**
 * Gets commands from the console and manages the Server status.
 *
 * @author Ilya Borovik
 */
public class ServerConsole implements Runnable {

    /** Lock and Monitor used during the interaction with other Server modules */
    private final StatusMonitor monitor;

    /**
     * Constructor
     * @param monitor   server status monitor
     */
    ServerConsole(StatusMonitor monitor) {
        this.monitor = monitor;
    }

    /**
     * Starts the Server Console.
     */
    @Override
    public void run() {
        try (Scanner in = new Scanner(System.in)) {
            String command;
            Status serverStatus = Status.ON;

            while (serverStatus == Status.ON) {
                synchronized (monitor) {
                    serverStatus = monitor.getStatus();
                }

                command = in.next();

                if ("\\exit".equals(command)) {
                    synchronized (monitor) {
                        monitor.setStatus(Status.OFF);
                    }
                    serverStatus = Status.OFF;
                    System.out.println("Server Status is set to OFF. Server App is finishing execution...");
                }
            }
        }
    }
}
