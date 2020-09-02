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
        String getName = reader.readLine(); //Получение имени
        if (getName.startsWith("/name") && getName.length() > 6) this.name = getName.substring(5) + ": ";
        else this.name = (socket.getInetAddress() + ":" + socket.getPort() + ": ").substring(1); //ИП:ПОРТ если имя не введено
        printMsg("[SERVER]You are connected");
        server.sendToAll(name + "is coming", this); //Уведомление всех о новом подключении
        thread = new Thread(new Runnable() { //Прослушка этого сокета
            @Override
            public void run() {
                while (!thread.isInterrupted()) {
                    try {
                        String msg = reader.readLine();
                        if (!thread.isInterrupted()) //Если сокет не закрыт
                            server.sendToAll(name + msg, User.this);
                    } catch (IOException e) {
                        server.sendToServer("[EXCEPTION]Reader listening: " + e.getMessage());
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
            server.sendToServer("[EXCEPTION]Write to user " + e.getMessage());
            e.printStackTrace();
        }
    }
    //Закрытие сокета
    void disconnect() {
        thread.interrupt();
        try {
            if (reader != null)
                reader.close();
            if (writer != null)
                writer.close();
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            server.sendToServer("[EXCEPTION]Read close: " + e.getMessage());
        }
    }
    String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return name;
    }
}
