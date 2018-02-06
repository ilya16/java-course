package hw3.main.workers;

/**
 * Provides the interface for Classes that process text.
 *
 * @author Ilya Borovik
 */
public interface TextProcessor {
    /**
     * Processes the text line.
     * Each class that implements TextProcessor should define the logic of the work with the text.
     * @param text  the text to be processed
     * @return      the result code of the operation
     *              0 in case of the absence of errors,
     *              and other values in case of their presence
     */
    int processText(String text);
}
