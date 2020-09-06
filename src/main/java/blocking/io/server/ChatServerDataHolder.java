package blocking.io.server;

import java.io.PrintWriter;
import java.util.Set;
import java.util.concurrent.*;

public class ChatServerDataHolder {

    private static final Set<String> USERS_NAMES = ConcurrentHashMap.newKeySet();

    private static final Set<PrintWriter> PRINT_WRITERS = ConcurrentHashMap.newKeySet();

    private ChatServerDataHolder() {
    }

    public static Set<String> getUsersNames() {
        return USERS_NAMES;
    }

    public static Set<PrintWriter> getPrintWriters() {
        return PRINT_WRITERS;
    }
}