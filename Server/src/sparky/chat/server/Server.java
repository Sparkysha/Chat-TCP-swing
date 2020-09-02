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
    //Остановка
    void stopServer() {
        if (ss == null) return; //При повторном нажатии
        serverIsStarted = false;
        sendToAll("[SERVER]Server stopping...", null); //Уведомить всех об остановке
        sendToAll("/goOut", null); //Отправить всем флаг для получения респонса "/exit"
        while (true) { //Ждать пока все не отключатся
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (users.isEmpty()) {
                try {
                    ss.close();
                    ss = null;
                    sendToServer("Server stopped");
                } catch (IOException e) {
                    sendToServer("[ERROR]ServerSocket close: " + e.getMessage());
                } finally {
                    break;
                }
            }
        }
    }
    //Сокращение
    void sendToServer(String msg) {
        System.out.println(msg);
        serverGUI.printMsg(msg);
    }
    //Отправка всем
    synchronized void sendToAll(String msg, User fromUser) {
        if (msg.endsWith("null") || msg.endsWith("/exit")) { //Если юзер отключился или желает отключиться
            sendToServer(msg);
            drop(fromUser);
            users.forEach(user -> user.printMsg("[SERVER]" + fromUser.getName() + "has leaving us")); //Уведомление оставшихся
        } else {
            sendToServer(msg);
            users.forEach(user -> user.printMsg(msg)); //Отправить всем
        }
    }
    //Отключение юзера
    void drop(User user) {
        users.remove(user);
        user.printMsg("[SERVER]You are disconnected");
        user.disconnect();
    }
}