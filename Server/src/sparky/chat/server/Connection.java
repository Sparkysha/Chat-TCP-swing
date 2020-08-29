package sparky.chat.server;

import java.io.*;
import java.net.Socket;

public class Connection {
    private Socket socket;
    private Thread thread;
    private Server server;
    private BufferedWriter writer;
    private BufferedReader reader;

    Connection(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            server.printExc(e);
            e.printStackTrace();
        }
        thread = new Thread(() -> {
            while (!thread.isInterrupted()) {
                try {
                    String msg = reader.readLine();
                    System.out.println(msg);
                    if (msg == null || msg.equals("/exit")) {
                        sendMsg("[SERVER]You are disconnecting...");
                        drop();
                    }
                    else
                        server.sendToAll(msg);
                } catch (IOException e) {
                    server.printExc(e);
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
            server.printExc(e);
            e.printStackTrace();
        }
    }
    void drop() {
        thread.interrupt();
        try {
            reader.close();
            writer.close();
            server.removeConnection(this);
            socket.close();
        } catch (IOException  e) {
            server.printExc(e);
            e.printStackTrace();
        }
    }
}
