package hw1.main;

import java.io.File;
import java.util.Map;

/**
 * Processes the Text File and checks an absence of duplicate words in it.
 *
 * @author Ilya Borovik
 */
public class UniqueWordsChecker extends TextReader {

    /** Constructor */
    public UniqueWordsChecker(File file, TextHandler textHandler, Map<String, Integer> dictionary,
                              StatusMonitor monitor) {
        super(file, textHandler, dictionary, monitor);
    }

    /**
     * Processes the text line and checks an absenceof duplicate words in it.
     * @param line  the text to be processed
     * @return      the result code of the operation
     *              0 in case of the absence of errors,
     *              and other values in case of their presence
     */
    @Override
    short processLine(String line) {
        String[] words = textHandler.splitTextIntoTokens(line);
        for (String word : words) {
            if (!word.isEmpty()) {

                /*
                 * Trade-Off:
                 * This section of code may stop other threads earlier if stopping condition
                 * (duplicates or illegal symbols) is met,
                 * but decreases performance in case of errorless input,
                 * because of monitor captures and checks on each word.
                 * In my opinion, not using this code is more preferable.
                 */
//                Status executionStatus;
//                synchronized (monitor) {
//                    executionStatus = monitor.getStatus();
//                }
//                if (executionStatus != Status.OK) {
//                    return -1;
//                }

                Integer result = dictionary.put(word.toLowerCase(), 1);

                if (result != null) {
                    System.out.printf(Thread.currentThread().getName() +
                            "\tError! Repetition of \"%s\" is found!!\n", word);

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
