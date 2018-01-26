package hw1.main;

import hw1.utils.RussianTextHandler;
import hw1.utils.TextHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Starts the application.
 * Processes the input with resources paths and
 * runs all threads that process the text inside resources.
 *
 * @author Ilya Borovik
 */
public class Main {
    /**
     * Starts the application.
     *
     * @param args the array of resources
     */
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        System.out.println("Application has started its execution");

        if (args.length == 0) {
            System.out.println("No resources passed as arguments. Please, try again.");
        } else {
            System.out.printf("Received %d resources\n", args.length);

            /* All the necessary objects used in the work */
            List<Thread> threads = new ArrayList<>();
            TextHandler textHandler = new RussianTextHandler();
            StatusMonitor monitor = new StatusMonitor();
            Map<String, Integer> dictionary = new ConcurrentHashMap<>();

            /*
             * synchronizedMap will work without race conditions for this task
             * (put() is the only operation used)
             */
            // Map<String, Integer> dictionary = Collections.synchronizedMap(new HashMap<>());

            /*
             * for an ordinary HashMap a mutex Object and synchronized block should be added
             * around put() operation
             */
            // Map<String, Integer> dictionary = new HashMap<>();

            System.out.println("Starting the TextReader Threads");
            for (String path : args) {
                Thread thread = new Thread(new UniqueWordsChecker(path, textHandler, dictionary, monitor));
                threads.add(thread);
                thread.start();
            }

            try {
                for (Thread thread : threads) {
                    thread.join();
                }
            } catch (InterruptedException e) {
                System.out.println("An error occurred. One working thread has interrupted the main app.\n"
                        + e.getMessage());
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
