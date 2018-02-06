package hw3.main.workers;

import hw3.main.utils.TextHandler;

import java.util.Map;

/**
 * Processes the Text and checks for an absence of duplicate words in it.
 *
 * @author Ilya Borovik
 */
public class UniqueWordsChecker implements TextProcessor {

    /** Text Handler that processes the text in the resource */
    private TextHandler textHandler;

    /** Collection used to store processed tokens */
    private final Map<String, Integer> dictionary;

    /** Monitor that stores the status of the whole job */
    private final StatusMonitor monitor;

    /**
     * Constructor
     *
     * @param textHandler   the text processor object
     * @param dictionary    the map for storing results
     * @param monitor       the status monitor of the the whole job
     */
    public UniqueWordsChecker(TextHandler textHandler, Map<String, Integer> dictionary, StatusMonitor monitor) {
        this.textHandler = textHandler;
        this.dictionary = dictionary;
        this.monitor = monitor;
    }

    /**
     * Processes the text line and checks for an absence of duplicate words in it.
     *
     * @param text  the text to be processed
     *
     * @return      the result code of the operation
     *              0 in the case of the absence of duplicates,
     *              and other values in case of their presence
     */
    @Override
    public int processText(String text) {
        String[] words = textHandler.splitTextIntoTokens(text);
        for (String word : words) {
            if (!word.isEmpty()) {

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
