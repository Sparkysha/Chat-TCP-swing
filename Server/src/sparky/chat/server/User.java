package sparky.chat.server;

import java.io.*;
import java.net.Socket;

public class User {
    private Socket socket;
    private Server server;
    private Thread thread;
    private BufferedWriter writer;
    private BufferedReader reader;
    private String name;

    User(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        name = new String(socket.getInetAddress() + ":" + socket.getPort() + ": ").substring(1);
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        server.sendToAll(name + "is coming", this);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!thread.isInterrupted()) {
                    try {
                        String msg = reader.readLine();
                        server.sendToAll(name + msg, User.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
    void printMsg(String msg) {
        try {
            writer.write(msg);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void disconnect() {
        thread.interrupt();
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
    String getName() {
        return this.name;
    }
}
