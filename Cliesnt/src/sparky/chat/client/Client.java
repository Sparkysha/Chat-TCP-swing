package sparky.chat.client;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private Thread thread;
    private BufferedReader reader;
    private BufferedWriter writer;
    private final ClientGUI clientGUI;

    Client(String ip, int port, ClientGUI clientGUI, String name) {
        this.clientGUI = clientGUI;
        try {
            socket = new Socket(ip, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            printExcep(e);
        }
        Thread stopServer = new Thread(() -> { //Если сервер отключается (что бы не вызвать лишнее исключение)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                printExcep(e);
            }
            clientGUI.disconnect.doClick();
        });
        if (socket != null) {
            sendMsg("/name" + name); //Отправка имени
            thread = new Thread(() -> {
                clientGUI.setClient(Client.this);
                while (!thread.isInterrupted()) {
                    try {
                        String msg = reader.readLine();
                        if (msg == null || msg.equals("/goOut")) stopServer.start(); //Сервер отключился(ается)
                        else if (msg.startsWith("/ru")) clientGUI.showUsers(msg.substring(3).replace( ": ", "\n")); //Список юзеров обновился
                        else clientGUI.printMsg(msg);
                    } catch (IOException e) {
                        printExcep(e);
                    }
                }
            });
            thread.start();
        }
    }

    void sendMsg(String msg) {
        try {
            writer.write(msg);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            printExcep(e);
        }
    }
    void drop() {
        thread.interrupt();
        sendMsg("/exit");
        try {
            if (reader != null)
                reader.close();
            if (writer != null)
                writer.close();
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            printExcep(e);
            e.printStackTrace();
        }
    }
    void printExcep(Exception e) {
        String error = "[CLIENT]Exception: " + e.getMessage();
        clientGUI.printMsg(error);
    }
}
