package hw2.main.client;

import java.io.IOException;
import java.io.ObjectInputStream;

public interface Receiver {
    Object receive(ObjectInputStream inputStream) throws IOException, ClassNotFoundException;
}
