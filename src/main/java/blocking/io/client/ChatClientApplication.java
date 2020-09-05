package blocking.io.client;

import javax.swing.*;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class ChatClientApplication {

    private static final Logger LOGGER = Logger.getLogger(ChatClientApplication.class.getName());

    private static final int EXIT_FAILURE = 1;

    private static final int INDEX_OF_SERVER_ADDRESS_ELEMENT = 0;

    public static void main(String[] args) {
        if (args.length != 1) {
            LOGGER.severe("The server IP address was not specified as a command-line argument");
            System.exit(EXIT_FAILURE);
        }
        var chatClient = new ChatClient(args[INDEX_OF_SERVER_ADDRESS_ELEMENT], WindowConstants.EXIT_ON_CLOSE, true);
        var chatClientThread = new Thread(chatClient, "chatClientThread");
        chatClientThread.start();
        threadJoin(chatClientThread);
    }

    private static void threadJoin(Thread chatClientThread) {
        try {
            chatClientThread.join();
        } catch (InterruptedException e) {
            LOGGER.severe("The main thread has been unexpectedly interrupted");
            System.exit(1);
        }
    }
}
