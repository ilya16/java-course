package hw1.main;

import java.util.regex.Pattern;

/**
 * Provides the tools for processing the text.
 *
 * @author Ilya Borovik
 */
public abstract class TextHandler {

    /** Pattern for splitting the text into tokens of words */
    private static final String SPLIT_TOKEN_PATTERN = "[ .,—!?:;_()«»\\s]+";

    /**
     * Splits the text into tokens according to the SPLIT_TOKEN_PATTERN
     * @param text  the text to be splitted
     * @return      the array of resulting tokens
     */
    public String[] splitTextIntoTokens(String text) {
        return text.split(SPLIT_TOKEN_PATTERN);
    }

    /**
     * Validates the text according to the defined rules.
     * Each class that extends TextHandler should define the rules.
     * @param text  the text to be validated
     * @return      the boolean value of result of the validation process
     */
    protected abstract boolean isTextValid(String text);

    /**
     * Validates the text according to the rules defined in Child classes
     * @param text  the text to be validated
     * @return      the boolean value of result of the validation process
     */
    public boolean validateString(String text) {
        return isTextValid(text);
    }


}
