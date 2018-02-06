package hw3.main.readers;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Provides the core functionality of a Text Reader from any resource.
 *
 * @author Ilya Borovik
 */
public abstract class TextReader implements ResourceReader, AutoCloseable {

    /** BufferedReader that is used to read the text in the resource */
    private BufferedReader bufferedReader;

    /**
     * Constructor
     *
     * @param bufferedReader    BufferedReader object
     */
    TextReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    /**
     * Reads a line from the text resource using bufferedReader.
     *
     * @return  a String containing the contents of the line,
     *          or null if the end of the stream has been reached
     *
     * @throws  IOException if an I/O error occurs
     */
    @Override
    public String readLine() throws IOException {
        return bufferedReader.readLine();
    }

    /**
     * Reads a portion of text of a certain <t>length</t> from the resource using bufferedReader.
     *
     * @return  a String containing the contents of the line of size <t>length</t>,
     *          or null if the end of the stream has been reached
     *
     * @throws  IOException if an I/O error occurs
     */
    @Override
    public String read(int length) throws IOException {
        char[] buffer = new char[length];
        int read = bufferedReader.read(buffer, 0, length);
        String result = String.valueOf(buffer);
        if (read > 0)
            return result;
        else
            return null;
    }

    /**
     * Closes the <t>bufferedReader</t> and all underlying resources
     *
     * @throws  Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        bufferedReader.close();
    }
}
