package sparky.chat.server;

import javax.swing.*;
import java.awt.*;

public class ServerGUI extends JFrame {
    private final JTextArea log = new JTextArea();
    private final JPanel panel = new JPanel();
    private final JButton start = new JButton("Start");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ServerGUI());
    }
    private ServerGUI() {
        setSize(600,400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Server");
        setVisible(true);
        setResizable(false);

        log.setEditable(false);
        log.setLineWrap(true);
        add(log);

        start.addActionListener(actionEvent -> new Thread(() -> new Server(8585, ServerGUI.this)).start());
        panel.add(start);
        add(panel, BorderLayout.SOUTH);
    }
    synchronized void printMsg(String msg) {
        SwingUtilities.invokeLater(() -> {
            log.append(msg + "\n");
            log.setCaretPosition(log.getDocument().getLength());
        });
    }
}
