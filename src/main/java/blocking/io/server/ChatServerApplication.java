package blocking.io.server;

import java.net.ServerSocket;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class ChatServerApplication {

    private static final Logger LOGGER = Logger.getLogger(ChatServerApplication.class.getName());

    public static void main(String[] args) throws Exception {
        LOGGER.info("The chat server has started");
        var executorService = Executors.newFixedThreadPool(500);
        try (var serverSocket = new ServerSocket(59001)) {
            while (true) {
                executorService.execute(new ClientSocketHandler(serverSocket.accept()));
            }
        }
    }
}
