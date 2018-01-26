package hw1.utils;

import java.util.regex.Pattern;

/**
 * Provides the tools for processing the Russian language text.
 *
 * @author Ilya Borovik
 */
public class RussianTextHandler implements TextHandler {
    /** Pattern for splitting the text into tokens of words */
    private static final String SPLIT_TO_WORDS_PATTERN = "[ .,—!?:;_()«»\\s]+";

    /** Pattern for finding any non Russian language symbols in the text */
    private static final String RUSSIAN_TEXT_PATTERN = "[а-яёА-ЯЁ0-9\\s.,—\\-!?:;_()«»'\"]+";

    /**
     * Splits the text into words according to the SPLIT_TOKEN_PATTERN
     * @param text  the text to be split
     * @return      the array of resulting tokens
     */
    @Override
    public String[] splitTextIntoTokens(String text) {
        return text.split(SPLIT_TO_WORDS_PATTERN);
    }

    /**
     * Validates the text according to the Russian language rules.
     * @param text  the text to be validated
     * @return      the boolean value of result of the validation process
     *              <tt>true</tt>, if text is in Russian, <tt>false</tt> otherwise
     */
    @Override
    public boolean validateString(String text) {
        return Pattern.compile(RUSSIAN_TEXT_PATTERN).matcher(text).matches();
    }
}
