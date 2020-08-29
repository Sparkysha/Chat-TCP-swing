package sparky.chat.server;

import javax.swing.*;
import java.awt.*;

public class ServerGUI extends JFrame {
    private int port = 8585;
    private final JTextArea log = new JTextArea();
    private final JPanel panel = new JPanel();
    private final JButton start = new JButton("Start");

    private final JTextField portInput = new JTextField();
    private final JLabel label = new JLabel("Enter port:");
    private final JButton ok = new JButton("OK");

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

        portInput.setText("8585");
        ok.addActionListener(actionEvent -> {
            port = Integer.valueOf(portInput.getText());
            dispose();
        });
        start.addActionListener(actionEvent -> {
            JDialog dialog = new JDialog(this, true);
            dialog.setSize(150, 150);
            dialog.setLocationRelativeTo(null);
            dialog.add(label, BorderLayout.NORTH);
            dialog.add(portInput, BorderLayout.CENTER);
            dialog.add(ok, BorderLayout.SOUTH);
            dialog.setVisible(true);
            new Thread(() -> new Server(port, ServerGUI.this)).start();
        });
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
