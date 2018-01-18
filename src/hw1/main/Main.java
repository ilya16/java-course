package hw1.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Starts the application.
 * Processes the input with resources paths and
 * runs all threads that process the text inside.
 *
 * @author Ilya Borovik
 */
public class Main {
    /**
     * Starts the application
     * @param args the array of resources
     */
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        System.out.println("Application has started its execution");

        if (args.length == 0) {
            System.out.println("No resources passed as arguments. Please, try again.");
        } else {
            System.out.printf("Got %d resources\n", args.length);

            /* All the necessary objects used in the work */
            ArrayList<Thread> threads = new ArrayList<>();
            Map<String, Integer> dictionary = new ConcurrentHashMap<>();
            TextHandler textHandler = new RussianTextHandler();
            StatusMonitor monitor = new StatusMonitor();


            System.out.println("Starting the working Threads");
            for (String path : args) {
                Thread thread = new Thread(new UniqueWordsChecker(new File(path), textHandler, dictionary, monitor));
                threads.add(thread);
                thread.start();
            }

            try {
                for (Thread thread : threads) {
                    thread.join();
                }
            } catch (InterruptedException e) {
                System.out.println("Error occurred: " + e.getMessage());
            }

            /* Observing results */
            if (monitor.getStatus() == Status.OK) {
                System.out.println("Text processing has been successfully finished.");
            } else {
                System.out.println("Program has finished its execution with an error: " + monitor.getStatus() +
                        "\nTry to fix the arguments passed or the files themselves.");
            }
        }

        System.out.printf("Total work time: %d ms\n", System.currentTimeMillis() - startTime);
    }
}
