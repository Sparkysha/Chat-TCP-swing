package sparky.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private boolean serverIsStarted = false;
    private ServerGUI serverGUI;
    private ArrayList<Connection> connections = new ArrayList<>();
    private ServerSocket ss;

    Server(int port, ServerGUI serverGUI) {
        serverIsStarted = true;
        this.serverGUI = serverGUI;
        try {
            ss = new ServerSocket(port);
            System.out.println("Server is started");
            serverGUI.printMsg("SERVER starting");
        } catch (IOException e) {
            printExc(e);
            e.printStackTrace();
        }
        while (serverIsStarted) {
            try {
                Socket socket = ss.accept();
                Connection connection = new Connection(socket, this);
                sendToAll(socket.getInetAddress() + " is coming...", connection);
                connections.add(connection);
            } catch (IOException e) {
                printExc(e);
                e.printStackTrace();
            }
        }
    }
    synchronized void sendToAll(String msg, Connection connect) {
        serverGUI.printMsg(msg);
        if (msg == null || msg.equals("/exit")) {
            connect.disconnect();
            connect.sendMsg("[SERVER]You are outing...");
            removeConnection(connect);
            sendToAll("is leaving us...", connect);
        }
        for (Connection connection : connections) {
            connection.sendMsg(msg);
        }
    }
    void removeConnection(Connection connection) {
        connections.remove(connection);
    }
    synchronized void printExc(Exception e) {
        String error = "[SERVER]Exception: " + e.getMessage();
        serverGUI.printMsg(error);
    }
}
