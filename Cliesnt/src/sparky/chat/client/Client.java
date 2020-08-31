package sparky.chat.client;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private Thread thread;
    private BufferedReader reader;
    private BufferedWriter writer;
    private ClientGUI clientGUI;
    private String name;

    Client(String ip, int port, ClientGUI clientGUI, String name) {
        this.clientGUI = clientGUI;
        this.name = name;
        try {
            socket = new Socket(ip, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendMsg("/name" + name);
        thread = new Thread(() -> {
            clientGUI.setClient(Client.this);
            while (!thread.isInterrupted()) {
                try {
                    String msg = reader.readLine();
                    clientGUI.printMsg(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    void sendMsg(String msg) {
        try {
            writer.write(msg);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
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
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
