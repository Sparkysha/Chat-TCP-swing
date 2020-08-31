package sparky.chat.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private boolean serverIsStarted = false;
    private ServerGUI serverGUI;
    private ArrayList<Socket> connections = new ArrayList<>();
    private ServerSocket ss;

    Server(int port, ServerGUI serverGUI) {
        serverIsStarted = true;
        this.serverGUI = serverGUI;
        try {
            ss = new ServerSocket(port);
            System.out.println("Server is started");
            serverGUI.printMsg("SERVER starting");
            while (serverIsStarted) {
                Socket socket = ss.accept();
                connections.add(socket);
                new Thread(() -> {
                    try(BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
                        sendToAll(socket.getInetAddress() + " is coming", socket, writer);
                        while (!Thread.currentThread().isInterrupted()) {
                            String msg = reader.readLine();
                            System.out.println(msg);
                            if (msg == null || msg.equals("/exit")) {
                                Thread.currentThread().interrupt();
                                sendMsg("[SERVER]You are disconnecting...", writer);
                                removeConnection(socket, writer);
                            }
                            else
                                sendToAll(msg, socket, writer);
                        }
                    } catch (IOException e) {
                        printExc(e);
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            printExc(e);
            e.printStackTrace();
        }
    }
    synchronized void sendToAll(String msg, Socket socket, BufferedWriter writer) {
        serverGUI.printMsg(msg);
        connections.forEach(socket1 -> sendMsg(msg, writer));
    }
    void removeConnection(Socket socket, BufferedWriter writer) {
        connections.remove(socket);
        sendToAll(socket.getInetAddress() + " has leaving us", socket, writer);
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                printExc(e);
                e.printStackTrace();
            }
        }
    }
    synchronized void printExc(Exception e) {
        String error = "[SERVER]Exception: " + e.getMessage();
        serverGUI.printMsg(error);
    }
    void sendMsg(String msg, BufferedWriter writer) {
        try {
            writer.write(msg);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            printExc(e);
            e.printStackTrace();
        }
    }
}
