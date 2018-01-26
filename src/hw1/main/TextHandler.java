package hw1.main;

/**
 * Provides the interface for processing the text.
 *
 * @author Ilya Borovik
 */
interface TextHandler {
    /**
     * Splits the text into tokens according to the SPLIT_TOKEN_PATTERN
     * Each class that implements TextHandler should define the rules of splitting.
     * @param text  the text to be splitted
     * @return      the array of resulting tokens
     */
    String[] splitTextIntoTokens(String text);

    /**
     * Validates the text according to the rules defined in Child classes
     * Each class that implements TextHandler should define the rules of validating.
     * @param text  the text to be validated
     * @return      the boolean value of result of the validation process
     */
    boolean validateString(String text);
}
