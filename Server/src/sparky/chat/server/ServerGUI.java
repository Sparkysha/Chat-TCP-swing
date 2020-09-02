package sparky.chat.server;

import javax.swing.*;
import java.awt.*;

public class ServerGUI extends JFrame {
    private int port = 8585;
    private Server server;
    private final JTextArea log = new JTextArea();
    private final JTextArea online = new JTextArea();
    private final JPanel panel = new JPanel();
    private final JButton start = new JButton("Start");
    private final JButton stop = new JButton("Stop");

    private JDialog dialog;
    private final JTextField portInput = new JTextField();
    private final JLabel label = new JLabel("Enter port:");
    private final JButton ok = new JButton("OK");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerGUI::new);
    }
    private ServerGUI() {
        setSize(600,400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Server");
        setResizable(false);
        setVisible(true);

        log.setEditable(false);
        log.setLineWrap(true);
        add(new JScrollPane(log));

        online.setEditable(false);
        online.setLineWrap(true);
        add(new JScrollPane(online), BorderLayout.EAST);

        portInput.setText("8585");
        ok.addActionListener(actionEvent -> {
            port = Integer.parseInt(portInput.getText());
            dialog.dispose();
        });
        start.addActionListener(actionEvent -> {
            dialog = new JDialog(this, true);
            dialog.setSize(150, 150);
            dialog.setLocationRelativeTo(null);
            dialog.add(label, BorderLayout.NORTH);
            dialog.add(portInput, BorderLayout.CENTER);
            dialog.add(ok, BorderLayout.SOUTH);
            dialog.setVisible(true);
            new Thread(() -> server.startServer(port)).start();
        });
        panel.add(start);
        stop.addActionListener(actionEvent -> server.stopServer());
        panel.add(stop);
        add(panel, BorderLayout.SOUTH);
        server = new Server(this);
    }
    synchronized void printMsg(String msg) {
        SwingUtilities.invokeLater(() -> log.append(msg + "\n"));
    }
    void showUsers(String msg) {
        SwingUtilities.invokeLater(() -> {
            online.setText(null);
            online.append(msg);
        });
    }
}
