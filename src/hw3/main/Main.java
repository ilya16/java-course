package hw3.main;

import hw3.main.loaders.CustomLoader;
import hw3.main.utils.RussianTextHandler;
import hw3.main.utils.TextHandler;
import hw3.main.workers.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Starts the application.
 * Processes the input with resources paths and
 * runs all threads that process the text inside resources.
 *
 * To specify the paths to the resources run main with arguments:
 *      path_to_resource_1 path_to_resource_2 ... path_to_resource_N
 *
 * To specify the lib path with jar file run main with arguments:
 *      -lib path_to_jar path_to_resource_1 path_to_resource_2 ... path_to_resource_N
 *
 * Note: Task specification is not supported yet. 'UniqueWordsChecker' is used.
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

        String taskName = "UniqueWordsChecker";
        String libPath = "src/hw3/lib/";
        String[] resources = args;

        if (args.length > 0) {
            if ("-lib".equals(args[0]) && args.length > 2) {
                libPath = args[1];
                resources = Arrays.copyOfRange(args, 2, args.length);
            }
        }

        System.out.printf("Lib path is: %s\n", libPath);
        if (resources.length == 0) {
            System.out.println("No resources passed as arguments. Please, try again.");
        } else {
            System.out.printf("Received %d resource(-s)\n", resources.length);
        }

        /* All the necessary objects used in the work */
        List<Thread> threads = new ArrayList<>();

        TextHandler textHandler = new RussianTextHandler();
        StatusMonitor monitor = new StatusMonitor();
        Map<String, Integer> dictionary = new ConcurrentHashMap<>();

        CustomLoader customLoader = new CustomLoader(libPath);

        System.out.println("\nStarting the TextReader Threads");
        for (String path : resources) {
            Thread thread = new Thread(
                    new TaskRunner(taskName, path, textHandler, dictionary, monitor)
            );
            thread.setContextClassLoader(customLoader);
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
            System.out.println("\nText processing has been successfully finished.");
        } else {
            System.out.println("\nProgram has finished its execution with an error: " + monitor.getStatus() +
                    "\nTry to fix the arguments passed or the files themselves.");
        }

        System.out.printf("Total work time: %d ms\n", System.currentTimeMillis() - startTime);
    }
}
