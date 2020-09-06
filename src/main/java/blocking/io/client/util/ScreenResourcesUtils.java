package blocking.io.client.util;

import javax.swing.*;

public class ScreenResourcesUtils {

    private ScreenResourcesUtils() {
    }

    public static void releaseScreenResources(JFrame jFrame) {
        jFrame.setVisible(false);
        jFrame.dispose();
    }
}
