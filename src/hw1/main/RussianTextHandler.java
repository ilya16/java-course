package hw1.main;

import java.util.regex.Pattern;

/**
 * Provides the tools for processing the Russian language text.
 *
 * @author Ilya Borovik
 */
public class RussianTextHandler extends TextHandler {

    /** Pattern for finding any non Russian language symbols in the text */
    private static final String NONRUSSIAN_TEXT_PATTERN = "[^а-яёА-ЯЁ0-9\\s.,—\\-!?:;_()«»'\"]+";

    /**
     * Validates the text according to the Russian language rules.
     * @param text  the text to be validated
     * @return      the boolean value of result of the validation process
     *              <tt>true</tt>, if text is in Russian, <tt>false</tt> otherwise
     */
    @Override
    public boolean validateString(String text) {
        return !Pattern.compile(NONRUSSIAN_TEXT_PATTERN).matcher(text).matches();
    }
}
