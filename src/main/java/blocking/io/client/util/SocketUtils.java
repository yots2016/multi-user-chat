package blocking.io.client.util;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class SocketUtils {

    private static final Logger LOGGER = Logger.getLogger(SocketUtils.class.getName());

    private SocketUtils() {
    }

    public static void closeSocket(Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            var message = "An I/O error occurs when closing this socket";
            LOGGER.severe(message);
        }
    }
}
