package hw1.main;

import hw1.readers.FileTextReader;
import hw1.readers.TextReader;
import hw1.utils.TextHandler;

import java.io.*;
import java.util.Map;

/**
 * Processes the File resource with the text.
 * Works in a Thread and synchronizes the monitor object with other Threads.
 *
 * @author Ilya Borovik
 */
public abstract class TaskRunner implements Runnable {

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
    public TaskRunner(File file, TextHandler textHandler,
                      Map<String, Integer> dictionary, StatusMonitor monitor) {
        this.file = file;
        this.textHandler = textHandler;
        this.dictionary = dictionary;
        this.monitor = monitor;
    }

    /**
     * Constructor
     * @param path          the path of the resource
     * @param textHandler   the text processor object
     * @param dictionary    the map for storing results
     * @param monitor       the status monitor of the the whole job
     */
    public TaskRunner(String path, TextHandler textHandler,
                      Map<String, Integer> dictionary, StatusMonitor monitor) {
        this(new File(path),  textHandler, dictionary, monitor);
    }

    /**
     * Runs the Thread with TextReader
     */
    @Override
    public void run() {
        System.out.printf("%s started working on the resource \t%s\n",
                Thread.currentThread().getName(), file.getPath());
        try (TextReader reader = new FileTextReader(new FileReader(file))) {

            Status executionStatus;

            synchronized (monitor) {
                executionStatus = monitor.getStatus();
            }

            String text;

            while ((text = reader.readLine()) != null && executionStatus == Status.OK) {
                 // System.out.println(Thread.currentThread().getName() + " is processing the text: " + text);

                if (textHandler.validateString(text)) {
                    int result = processText(text);
                    if (result != 0) {
                        return;
                    }

                } else {
                    System.out.printf("%s\t Error! Text in file \"%s\" contains non valid (foreign) symbols.\n",
                            Thread.currentThread().getName(), file.getPath());

                    synchronized (monitor) {
                        monitor.setStatus(Status.NONVALID_SYMBOL_FOUND);
                    }

                    return;
                }

                synchronized (monitor) {
                    executionStatus = monitor.getStatus();
                }
            }
        } catch (FileNotFoundException e) {
            System.out.printf("%s\tFile \"%s\" is not found. " +
                    "Trying to work with other resources if they are present\n",
                    Thread.currentThread().getName(), file.getPath());

            /*
             * The following lines should be uncommented, if all working Threads should be stopped
             * if at least one File is not found
             */
            /*synchronized (monitor) {
                monitor.setStatus(Status.FILE_NOT_FOUND);
            }*/

        } catch (IOException e) {
            System.out.printf("%s\tAn IOException occurred while reading the file \"%s\". %s",
                    Thread.currentThread().getName(), file.getPath(), e.getMessage());
        } catch (Exception e) {
            System.out.printf("%s\tAn Exception occurred. Resource cannot be closed. %s",
                    Thread.currentThread().getName(), e.getMessage());
        } finally {
            System.out.println(Thread.currentThread().getName() + " has finished its work");
        }

    }

    /**
     * Processes the text line.
     * Each class that extends FileReader should define the logic of the work with the text.
     * @param text  the text to be processed
     * @return      the result code of the operation
     *              0 in case of the absence of errors,
     *              and other values in case of their presence
     */
    abstract int processText (String text);
}
