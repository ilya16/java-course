package hw2.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Defines the module of the Client App that communicates
 * with the Server through the socket input and output streams.
 *
 * @author Ilya Borovik
 */
abstract class ClientModule {
    /** Input Stream of the connection with the Server */
    ObjectInputStream inputStream;

    /** Output Stream of the connection with the Server */
    ObjectOutputStream outputStream;

    ClientModule(ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }
}
