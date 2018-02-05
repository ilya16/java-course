package hw3.main.readers;

import java.io.IOException;

/**
 * Provides the interface for the Text Readers.
 *
 * @author Ilya Borovik
 */
public interface ResourceReader {

    /**
     * Reads a line from the resource.
     * Each class that implements TextReader should define
     * the notion of a line in the resource and return exactly one line.
     *
     * @return      a String containing the contents of the line, not including
     *              any line-termination characters, or null if the end of the
     *              stream has been reached
     *
     * @exception   IOException  if an I/O error occurs
     */
    String readLine() throws IOException;

    /**
     * Reads a portion of a resource content of a given <t>length</t>.
     *
     * @param length    the size of the String to be read
     *
     * @return          a String containing the contents of the line of size <t>length</t>,
     *                  or null if the end of the stream has been reached
     *
     * @exception  IOException  if an I/O error occurs
     */
    String read(int length) throws IOException;
}
