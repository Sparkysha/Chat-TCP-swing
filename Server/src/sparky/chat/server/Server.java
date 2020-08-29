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
                sendToAll(socket.getInetAddress() + " is coming...");
                connections.add(new Connection(socket, this));
            } catch (IOException e) {
                printExc(e);
                e.printStackTrace();
            }
        }
    }
    void sendToAll(String msg) {
        serverGUI.printMsg(msg);
        for (Connection connection : connections) {
            connection.sendMsg(msg);
        }
    }
    void removeConnection(Connection connection) {
        connections.remove(connection);
    }
    void printExc(Exception e) {
        String error = "[SERVER]Exception: " + e.getMessage();
        serverGUI.printMsg(error);
    }
}
