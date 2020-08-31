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
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!thread.isInterrupted()) {
                        try {
                            server.sendToAll(reader.readLine(), Connection.this);
                        } catch (IOException e) {
                            server.printExc(e);
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        } catch (IOException e) {
            server.printExc(e);
        } finally {
            try {
                if (writer != null)
                    writer.close();
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                server.printExc(e);
            }
        }
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
    void disconnect() {
        thread.interrupt();
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                server.printExc(e);
            }
        }
    }
}
