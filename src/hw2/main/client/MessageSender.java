package hw2.main.client;

import hw2.main.chat.Message;
import hw2.main.chat.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Scanner;

public class MessageSender implements Sender, Runnable {
    Socket socket;
    int chatID;
    User user;
    ObjectOutputStream outputStream;

    public MessageSender(Socket socket, int chatID, User user, ObjectOutputStream outputStream) {
        this.socket = socket;
        this.chatID = chatID;
        this.user = user;
        this.outputStream = outputStream;
    }

    @Override
    public void send(Object object, ObjectOutputStream outputStream) throws IOException{
        outputStream.writeObject(object);
        outputStream.flush();
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String messageText = "";
        Message message;
        DateFormat dateFormat = DateFormat.getDateInstance();

        while (!"\\exit".equals(messageText)) {
            messageText = scanner.nextLine();
            message = new Message(messageText, chatID, user);

            try {
                send(message, outputStream);
                System.out.printf("%s\t%s:\t%s\n", dateFormat.format(message.getDate()),
                        message.getSender().getUsername(), message.getText());
            } catch (IOException e) {
                System.out.println("An error occurred while sending your message. " +
                        "Please, try to send it again.");
            }


        }
    }
}
