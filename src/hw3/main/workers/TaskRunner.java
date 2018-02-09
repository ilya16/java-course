package hw3.main.workers;

import hw3.main.readers.FileTextReader;
import hw3.main.readers.TextReader;
import hw3.main.utils.TextHandler;

import java.io.*;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Processes the File resource with the text.
 * Works in a Thread and synchronizes the monitor object with other Threads.
 *
 * @author Ilya Borovik
 */
public class TaskRunner implements Runnable {

    /** Name of the task (Class name) */
    private final String taskName;

    /** Text Handler that validates the text in the resource */
    private TextHandler textHandler;

    /** Collection used to store processed tokens */
    private final Map<String, Integer> dictionary;

    /** Monitor that stores the status of the whole job */
    private final StatusMonitor monitor;

    /** File Resource */
    private File file;

    /**
     * Constructor
     *
     * @param taskName      the name of task (TextProcessor class)
     * @param file          the resource
     * @param textHandler   the text handler object
     * @param dictionary    the map for storing results
     * @param monitor       the status monitor of the the whole job
     */
    public TaskRunner(String taskName, File file, TextHandler textHandler,
                      Map<String, Integer> dictionary, StatusMonitor monitor) {
        this.taskName = taskName;
        this.file = file;
        this.textHandler = textHandler;
        this.dictionary = dictionary;
        this.monitor = monitor;
    }

    /**
     * Constructor
     *
     * @param taskName      the name of task (TextProcessor class)
     * @param path          the path of the resource
     * @param textHandler   the text handler object
     * @param dictionary    the map for storing results
     * @param monitor       the status monitor of the the whole job
     */
    public TaskRunner(String taskName, String path, TextHandler textHandler,
                      Map<String, Integer> dictionary, StatusMonitor monitor) {
        this(taskName, new File(path),  textHandler, dictionary, monitor);
    }

    /**
     * Runs the Thread with TextReader
     */
    @Override
    public void run() {
        System.out.printf("%s started working on the resource \t%s\n",
                Thread.currentThread().getName(), file.getPath());

        /* Proxy Object */
        TextProcessor textProcessor =
                (TextProcessor) Proxy.newProxyInstance(
                        TextProcessor.class.getClassLoader(),
                        new Class[]{TextProcessor.class},
                        new TextProcessorInvHandler(taskName, textHandler, dictionary, monitor)
                );

        try (TextReader reader = new FileTextReader(new FileReader(file))) {

            Status executionStatus;

            synchronized (monitor) {
                executionStatus = monitor.getStatus();
            }

            String text;
            Predicate<String> successfulTextProcessing = x -> textProcessor.processText(x) == 0;

            while ((text = reader.readLine()) != null && executionStatus == Status.OK) {
                // System.out.println(Thread.currentThread().getName() + " is processing the text: " + text);

                if (textHandler.validateString(text)) {

                    if (!successfulTextProcessing.test(text)) {
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

            synchronized (monitor) {
                monitor.setStatus(Status.EXCEPTION_THROWN);
            }
        } catch (ClassNotFoundException e) {
            System.out.printf("%s\tClass with TextProcessor is not found at runtime. %s",
                    Thread.currentThread().getName(), e.getMessage());

            synchronized (monitor) {
                monitor.setStatus(Status.EXCEPTION_THROWN);
            }
        } catch (Exception e) {
            System.out.printf("%s\tAn Exception occurred. Thread is finishing its execution %s\n",
                    Thread.currentThread().getName(), e.getMessage());

            synchronized (monitor) {
                monitor.setStatus(Status.EXCEPTION_THROWN);
            }
        } finally {
            System.out.println(Thread.currentThread().getName() + " has finished its work");
        }

    }
}
