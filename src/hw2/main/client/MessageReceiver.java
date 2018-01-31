package hw2.main.client;

import hw2.main.chat.Message;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;

public class MessageReceiver implements Receiver, Runnable {
    Socket socket;
    ObjectInputStream inputStream;

    public MessageReceiver(Socket socket, ObjectInputStream inputStream) {
        this.socket = socket;
        this.inputStream = inputStream;
    }

    @Override
    public Object receive(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        return inputStream.readObject();
    }

    @Override
    public void run() {
        Message message;
        DateFormat dateFormat = DateFormat.getDateInstance();

        while (true) {
            try {
                message = (Message)receive(inputStream);

                if ("\\exit".equals(message.getText())) {
                    return;
                }

                System.out.printf("%s\t%s:\t%s\n", dateFormat.format(message.getDate()),
                        message.getSender().getUsername(), message.getText());

            } catch (EOFException e) {
                System.out.println("Connection with the Server is lost.");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    System.out.println("An error occurred");
                }
            } catch (IOException e) {
                System.out.println("An error occurred while receiving the message.");
            } catch (ClassNotFoundException e) {
                System.out.println("An error occurred. Class Message is not found. Message was not received.");
            }
        }
    }
}
