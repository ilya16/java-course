package hw1.main;

import java.io.*;
import java.util.Map;
import java.util.Scanner;

/**
 * Processes the Text File resource.
 * Works in a Thread and synchronizes the monitor object with other Threads.
 *
 * @author Ilya Borovik
 */
public abstract class TextReader implements Runnable {

    /** Text Handler that processes the text in the resource */
    protected TextHandler textHandler;

    /** Collection used to store processed tokens */
    protected final Map<String, Integer> dictionary;

    /** Monitor that stores the status of the whole job */
    protected final StatusMonitor monitor;

    /** File Resource */
    private File file;

    /**
     * Constructor
     * @param file          the resource
     * @param textHandler   the text processor object
     * @param dictionary    the map for storing results
     * @param monitor       the status monitor of the the whole job
     */
    public TextReader(File file, TextHandler textHandler, Map<String, Integer> dictionary, StatusMonitor monitor) {
        this.file = file;
        this.textHandler = textHandler;
        this.dictionary = dictionary;
        this.monitor = monitor;
    }

    /**
     * Runs the Thread with TextReader
     */
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " started working on the resource \t" +
                file.getPath());
        try (Scanner in = new Scanner(file)) {

            Status executionStatus;

            synchronized (monitor) {
                executionStatus = monitor.getStatus();
            }

            while (in.hasNextLine() && executionStatus == Status.OK) {
                String line = in.nextLine();

//                System.out.println(Thread.currentThread().getName() + " is processing the line: " + line);

                if (textHandler.validateString(line)) {
                    short result = processLine(line);
                    if (result != 0) {
                        return;
                    }

                } else {
                    System.out.println("Text contains non russian words.");

                    synchronized (monitor) {
                        monitor.setStatus(Status.FOREIGN_SYMBOL_FOUND);
                    }

                    return;
                }

                synchronized (monitor) {
                    executionStatus = monitor.getStatus();
                }
            }

            System.out.println(Thread.currentThread().getName() + " has finished its work");

        } catch (FileNotFoundException e) {
            System.out.printf("File \"%s\" is not found\n", file.getPath());

            /*
             * The following lines should be uncommented, if all working Threads should be stopped
             * if at least one File is not found
             */
//            synchronized (monitor) {
//                monitor.setStatus(Status.FILE_NOT_FOUND);
//            }
        }

    }

    /**
     * Processes the text line.
     * Each class that extends TextReader should define the logic of the work with the text.
     * @param line  the text to be processed
     * @return      the result code of the operation
     *              0 in case of the absence of errors,
     *              and other values in case of their presence
     */
    abstract short processLine(String line);
}
