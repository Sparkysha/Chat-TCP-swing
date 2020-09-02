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
    private String onlineUsers = "";

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
        while (serverIsStarted) { //Прослушка новых подключений
            try {
                Socket socket = ss.accept();
                if (serverIsStarted) {
                    User user = new User(socket, Server.this);
                    users.add(user);
                    onlineUsers += user.getName(); //Строка для ридлайна
                    sendToAll("/ru" + onlineUsers, null); //Отправить всем
                    serverGUI.showUsers(onlineUsers.replace( ": ", "\n")); //Обновить у себя
                }
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
                sendToServer("[EXCEPTION]Thread of stop has been interrupt: " + e.getMessage());
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
            users.forEach(user -> user.sendMsg("[SERVER]" + fromUser.getName() + " has leaving us")); //Уведомление оставшихся
            users.forEach(user -> user.sendMsg("/ru" + onlineUsers)); //Отправка обновленного списка
        } else {
            sendToServer(msg);
            users.forEach(user -> user.sendMsg(msg)); //Отправить всем
        }
    }
    //Отключение юзера
    synchronized void drop(User user) {
        users.remove(user);
        user.sendMsg("[SERVER]You are disconnected");
        if (onlineUsers.contains(user.getName())) { //Удаление из списка онлайн
            onlineUsers = onlineUsers.replaceFirst(user.getName(), "");
            serverGUI.showUsers(onlineUsers.replace( ": ", "\n"));
        }
        user.disconnect(); //Закрытие сокета
    }
}