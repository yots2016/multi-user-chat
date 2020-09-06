package blocking.io.client;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import static blocking.io.client.util.ScreenResourcesUtils.releaseScreenResources;
import static blocking.io.client.util.SocketUtils.closeSocket;

public class ChatClient implements Runnable {

    private static final int SOCKET_PORT = 59001;

    private static final String J_FRAME_TITLE = "USER";
    private static final int TEXT_AREA_ROWS = 16;
    private static final int TEXT_FIELD_COLUMNS = 50;
    private static final int TEXT_AREA_COLUMNS = 50;

    private Socket socket;
    private PrintWriter printWriter;

    private JFrame jFrame;
    private JTextField textField;
    private JTextArea textArea;
    int defaultCloseOperation;
    boolean isFrameVisible;

    public ChatClient(String serverAddress, int defaultCloseOperation, boolean isFrameVisible) {
        this.defaultCloseOperation = defaultCloseOperation;
        this.isFrameVisible = isFrameVisible;
        initSocket(serverAddress);
        initPrintWriter();
        initInterfaceElements();
        configInterfaceElements();
    }

    private void initSocket(String serverAddress) {
        try {
            this.socket = new Socket(serverAddress, SOCKET_PORT);
        } catch (IOException e) {
            var message = "An I/O error occurs when creating the socket";
            throw new RuntimeException(message, e);
        }
    }

    private void initPrintWriter() {
        try {
            var outputStream = socket.getOutputStream();
            this.printWriter = new PrintWriter(outputStream, true);
        } catch (IOException e) {
            var message = "An I/O error occurs when creating the output stream or if the socket is not connected";
            throw new RuntimeException(message, e);
        }
    }

    private void initInterfaceElements() {
        this.textField = new JTextField(TEXT_FIELD_COLUMNS);
        this.textArea = new JTextArea(TEXT_AREA_ROWS, TEXT_AREA_COLUMNS);
        this.jFrame = new JFrame(J_FRAME_TITLE);

    }

    private void configFrame() {
        var contentPane = this.jFrame.getContentPane();
        contentPane.add(textField, BorderLayout.SOUTH);
        contentPane.add(new JScrollPane(textArea), BorderLayout.CENTER);
        this.jFrame.pack();
        this.jFrame.setDefaultCloseOperation(this.defaultCloseOperation);
        this.jFrame.setVisible(this.isFrameVisible);
    }

    private void configTextArea() {
        textArea.setEditable(false);
    }

    private void configTextField() {
        textField.setEditable(false);
        textField.addActionListener(actionEvent -> {
            printWriter.println(textField.getText());
            textField.setText("");
        });
    }

    private void configInterfaceElements() {
        configTextField();
        configTextArea();
        configFrame();
    }

    @Override
    public void run() {
        try (var scanner = new Scanner(socket.getInputStream())) {
            while (scanner.hasNextLine()) {
                var line = scanner.nextLine();
                if (line.startsWith("SUBMITNAME")) {
                    this.printWriter.println(getUserName());
                } else if (line.startsWith("NAMEACCEPTED")) {
                    this.jFrame.setTitle("USER: " + line.substring(13));
                    this.textField.setEditable(true);
                } else if (line.startsWith("MESSAGE")) {
                    this.textArea.append(line.substring(8) + "\n");
                }
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException("The IP address of the host could not be determined", e);
        } catch (IOException e) {
            throw new RuntimeException("An I/O error occurs", e);
        } finally {
            releaseScreenResources(this.jFrame);
            closeSocket(this.socket);
        }
    }

    private String getUserName() {
        return JOptionPane.showInputDialog(jFrame, "Enter a user name:", "A user name entering",
                JOptionPane.PLAIN_MESSAGE);
    }
}