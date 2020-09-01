package sparky.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private final ArrayList<User> users = new ArrayList<>();
    private final ServerGUI serverGUI;
    private ServerSocket ss;
    private boolean serverIsStarted;

    Server(ServerGUI serverGUI) {
        this.serverGUI = serverGUI;
    }
    void startServer(int port) {
        serverIsStarted = true;
        try {
            ss = new ServerSocket(port);
            sendToServer("Server is started on port " + port);
        } catch (IOException e) {
            sendToServer("[ERROR]Server is not started");
        }
        while (serverIsStarted) {
            try {
                Socket socket = ss.accept();
                if (serverIsStarted)
                    users.add(new User(socket, Server.this));
            } catch (IOException e) {
                sendToServer("[EXCEPTION]Accept: " + e.getMessage());
            }
        }
    }
    void stopServer() {
        serverIsStarted = false;
        users.forEach((user -> user.disconnect()));
        users.clear();
        if (ss != null && !ss.isClosed()) {
            try {
                ss.close();
            } catch (IOException e) {
                sendToServer("[EXCEPTION]ServerSocket close: " + e.getMessage());
            }
        }
        ss = null;
        sendToServer("Server stopped");
    }
    void sendToServer(String msg) {
        System.out.println(msg);
        serverGUI.printMsg(msg);
    }
    void sendToAll(String msg, User fromUser) {
        if (msg.endsWith("null") || msg.endsWith("/exit")) {
            drop(fromUser);
            users.forEach(user -> user.printMsg("[SERVER]" + fromUser.getName() + "has leaving us"));
        } else {
            sendToServer(msg);
            users.forEach(user -> user.printMsg(msg));
        }
    }
    void drop(User user) {
        users.remove(user);
        user.printMsg("[SERVER]You are disconnected");
        user.disconnect();
    }
}