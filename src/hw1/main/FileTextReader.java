package hw1.main;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Provides the core functionality of a File based Text Reader.
 *
 * @author Ilya Borovik
 */
public class FileTextReader extends TextReader {

    /**
     * Constructor
     *
     * @param   fileReader  FileReader object
     */
    public FileTextReader(FileReader fileReader) {
        super(new BufferedReader(fileReader));
    }
}
