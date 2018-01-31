package hw2.client;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Provides the interface for Object sender.
 *
 * @author Ilya Borovik
 */
public interface Sender {
    /**
     * Sends the object through the ObjectOutputStream.
     *
     * @param object        Object to be sent
     * @param outputStream  stream through which object is sent
     *
     * @throws IOException  if an error occurs while sending the object
     */
    void send(Object object, ObjectOutputStream outputStream) throws IOException;
}
