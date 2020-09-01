package sparky.chat.client;

import javax.swing.*;
import java.awt.*;

public class ClientGUI extends JFrame {
    private final JTextArea log = new JTextArea();
    private final JPanel mainPanel = new JPanel();
    private final JPanel buttPanel = new JPanel();
    private final JTextField input = new JTextField();
    private final JButton send = new JButton("Send");
    private final JButton connect = new JButton("Connect");
    private final JButton disconnect = new JButton("Disconnect");

    private String ip;
    private int port;
    private String name;
    private Client client;

    private JDialog dialog;
    private final JLabel ipEnter = new JLabel("Enter IP:");
    private final JTextField ipInput = new JTextField("192.168.1.4");
    private final JLabel portEnter = new JLabel("Enter port:");
    private final JTextField portInput = new JTextField("8585");
    private final JLabel nameEnter = new JLabel("Your name:");
    private final JTextField nameInput = new JTextField("Guest");
    private final JButton ok = new JButton("OK");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientGUI());
    }
    private ClientGUI() {
        setSize(600,400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Chat Client");
        setVisible(true);
        setResizable(false);

        log.setEditable(false);
        log.setLineWrap(true);
        add(log);

        ok.addActionListener(actionEvent -> {
            ip = ipInput.getText();
            port = Integer.parseInt(portInput.getText());
            name = nameInput.getText();
            dialog.dispose();
        });
        connect.addActionListener(actionEvent -> {
            dialog = new JDialog(this, true);
            dialog.setSize(200, 150);
            dialog.setLocationRelativeTo(null);
            dialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            dialog.setLayout(new FlowLayout());
            dialog.add(ipEnter);
            dialog.add(ipInput);
            dialog.add(portEnter);
            dialog.add(portInput);
            dialog.add(nameEnter);
            dialog.add(nameInput);
            dialog.add(ok);
            dialog.setVisible(true);
            if (client == null)
                new Thread(() -> new Client(ip, port, ClientGUI.this, name)).start();
        });

        disconnect.addActionListener(actionEvent -> {
            if (client != null) {
                client.drop();
                client = null;
            }
        });

        send.addActionListener(actionEvent -> {
            String msg = input.getText();
            if (client != null && !msg.equals("") && !msg.equals("/exit"))
                client.sendMsg(msg);
            else if (client != null && msg.equals("/exit"))
                disconnect.doClick();
            else
                printMsg(msg);
            input.setText(null);
        });
        input.addActionListener(actionEvent -> send.doClick());

        buttPanel.add(send);
        buttPanel.add(connect);
        buttPanel.add(disconnect);

        mainPanel.setLayout(new GridLayout(1,2));
        mainPanel.add(input);
        mainPanel.add(buttPanel);

        add(mainPanel, BorderLayout.SOUTH);
    }

    void setClient(Client client) {
        this.client = client;
    }

    synchronized void printMsg(String msg) {
        SwingUtilities.invokeLater(() -> {
            log.append(msg + "\n");
            log.setCaretPosition(log.getDocument().getLength());
        });
    }
}
