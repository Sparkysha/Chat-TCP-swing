package sparky.chat.server;

import java.io.*;
import java.net.Socket;

public class User {
    private final Socket socket;
    private final Server server;
    private final Thread thread;
    private final BufferedWriter writer;
    private final BufferedReader reader;
    private final String name;

    User(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String getName = reader.readLine();
        if (getName.startsWith("/name") && getName.length() > 6) this.name = getName.substring(5) + ": ";
        else this.name = (socket.getInetAddress() + ":" + socket.getPort() + ": ").substring(1);
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
