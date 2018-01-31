package hw2.main.client;

import java.io.IOException;
import java.io.ObjectOutputStream;

public interface Sender {
    void send(Object object, ObjectOutputStream outputStream) throws IOException;
}
