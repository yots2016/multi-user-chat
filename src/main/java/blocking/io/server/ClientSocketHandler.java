package blocking.io.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;

public class ClientSocketHandler implements Runnable{

    private static final Logger LOGGER = Logger.getLogger(ClientSocketHandler.class.getName());

    private final Set<String> usersNames = ChatServerDataHolder.getUsersNames();
    private final Set<PrintWriter> printWriters = ChatServerDataHolder.getPrintWriters();

    private String userName;
    private final Socket socket;
    private PrintWriter printWriter;

    public ClientSocketHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            Scanner scanner = new Scanner(socket.getInputStream());
            printWriter = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                printWriter.println("SUBMITNAME");
                userName = scanner.nextLine();
                if (userName == null) {
                    return;
                }
                synchronized (usersNames) {
                    if (!userName.isBlank() && !usersNames.contains(userName)) {
                        usersNames.add(userName);
                        break;
                    }
                }
            }

            printWriter.println("NAMEACCEPTED " + userName);
            for (PrintWriter writer : printWriters) {
                writer.printf("MESSAGE %s has joined%n", userName);
            }
            printWriters.add(printWriter);

            while (true) {
                String input = scanner.nextLine();
                if (input.toLowerCase().startsWith("/quit")) {
                    return;
                }
                for (PrintWriter writer : printWriters) {
                    writer.println("MESSAGE " + userName + ": " + input);
                }
            }
        } catch (Exception e) {
            LOGGER.info(e.toString());
        } finally {
            if (printWriter != null) {
                printWriters.remove(printWriter);
            }
            if (userName != null) {
                var message = String.format("%s is leaving", userName);
                LOGGER.info(message);
                usersNames.remove(userName);
                for (PrintWriter writer : printWriters) {
                    writer.println("MESSAGE " + userName + " has left");
                }
            }
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
}
