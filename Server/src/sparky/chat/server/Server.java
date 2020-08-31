package sparky.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private final ArrayList<User> users = new ArrayList<>();
    private final ServerGUI serverGUI;
    private ServerSocket ss;

    Server(int port, ServerGUI serverGUI) {
        this.serverGUI = serverGUI;
        try {
            ss = new ServerSocket(port);
            sendToServer("Server is started");
            while (true) {
                Socket socket = ss.accept();
                User user = new User(socket, this);
                users.add(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void sendToServer(String msg) {
        System.out.println(msg);
        serverGUI.printMsg(msg);
    }
    void sendToAll(String msg, User fromUser) {
        if (msg.endsWith("null") || msg.endsWith("/exit")) {
            drop(fromUser);
            msg = fromUser.getName() + "has leaving us";
        }
        String finalMsg = msg;
        sendToServer(finalMsg);
        users.forEach(user -> user.printMsg(finalMsg));
    }
    void drop(User user) {
        users.remove(user);
        user.printMsg("[SERVER]You are disconnected");
        user.disconnect();
    }
}