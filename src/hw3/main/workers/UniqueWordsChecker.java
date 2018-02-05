package hw3.main.workers;

import hw3.main.utils.TextHandler;

import java.io.File;
import java.util.Map;

/**
 * Processes the Text File and checks an absence of duplicate words in it.
 *
 * @author Ilya Borovik
 */
public class UniqueWordsChecker extends TaskRunner {

    /**
     * Constructor
     *
     * @param file          the resource
     * @param textHandler   the text processor object
     * @param dictionary    the map for storing results
     * @param monitor       the status monitor of the the whole job
     */
    public UniqueWordsChecker(File file, TextHandler textHandler,
                              Map<String, Integer> dictionary, StatusMonitor monitor) {
        super(file, textHandler, dictionary, monitor);
    }

    /**
     * Constructor
     *
     * @param path          the path of the resource
     * @param textHandler   the text processor object
     * @param dictionary    the map for storing results
     * @param monitor       the status monitor of the the whole job
     */
    public UniqueWordsChecker(String path, TextHandler textHandler,
                              Map<String, Integer> dictionary, StatusMonitor monitor) {
        super(new File(path),  textHandler, dictionary, monitor);
    }

    /**
     * Processes the text line and checks an absence of duplicate words in it.
     *
     * @param text  the text to be processed
     *
     * @return      the result code of the operation
     *              0 in case of the absence of errors,
     *              and other values in case of their presence
     */
    @Override
    int processText(String text) {
        String[] words = textHandler.splitTextIntoTokens(text);
        for (String word : words) {
            if (!word.isEmpty()) {

                /*
                 * synchronized block is not needed here, because
                 * it's normal for a Thread to process a few words
                 * and to read an old value of monitor status in
                 * few iterations, when another Thread has found an error.
                 */
                if (monitor.getStatus() != Status.OK) {
                    return -1;
                }

                Integer result = dictionary.put(word.toLowerCase(), 1);

                if (result != null) {
                    System.out.printf("%s\tError! Repetition of \"%s\" is found!\n",
                            Thread.currentThread().getName(), word);

                    synchronized (monitor) {
                        monitor.setStatus(Status.DUPLICATE_FOUND);
                    }

                    return -1;
                }

            }
        }
        return 0;
    }
}
