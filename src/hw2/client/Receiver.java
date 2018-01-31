package hw2.client;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Provides the interface for Object receiver.
 *
 * @author Ilya Borovik
 */
public interface Receiver {
    /**
     * Receives the object through the ObjectInputStream.
     *
     * @param inputStream   stream through which object is received
     *
     * @throws IOException  if an error occurs while receiving the object
     */
    Object receive(ObjectInputStream inputStream) throws IOException, ClassNotFoundException;
}
