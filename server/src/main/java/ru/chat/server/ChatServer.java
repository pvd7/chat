/*
 * Copyright (c) 2018.
 */

package ru.chat.server;

import ru.chat.network.SocketConnection;
import ru.chat.network.SocketConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

public class ChatServer implements SocketConnectionListener {

    public static void main(String[] args) throws IOException {
        ChatServer chatServer = new ChatServer();
        chatServer.start(8888);
    }

    // список подключений
    private final List<SocketConnection> socketConnections = new LinkedList<>();

    // проверяет сообщение на наличие служебных комманд
    private boolean checkCommands(String msg) {
        return msg.startsWith("/auth")
               || (msg.startsWith("/w"));
    }

    private boolean catchCommands(String msg) {
        if (msg.startsWith("/w")) {
            String[] parts = str.split("\\s");

            return true;
        } else {
            return false;
        }
    }

    public void start(int port) throws IOException {
        System.out.println("Server running...");
        ServerSocket serverSocket = new ServerSocket(port);
        while (!serverSocket.isClosed()) {
            try {
                SocketConnection socketConnection = new SocketConnection(serverSocket.accept(), this);
                socketConnection.start();
            } catch (IOException e) {
                System.out.println("Connection ecxeption: " + e);
            }
        }
    }

    @Override
    public synchronized void onConnected(SocketConnection socketConnection) {
        socketConnections.add(socketConnection);
        broadcastMsg(socketConnection,"Client connected: " + socketConnection);
    }

    @Override
    public synchronized void onReceiveString(SocketConnection socketConnection, String str) {
        if (!catchCommands(str)) {
            broadcastMsg(socketConnection, str);
        }
    }

    @Override
    public synchronized void onDisconnected(SocketConnection socketConnection) {
        socketConnections.remove(socketConnection);
        broadcastMsg("Client disconnected: " + socketConnection);
    }

    @Override
    public synchronized void onException(SocketConnection socketConnection, Exception e) {
        System.out.println("SocketConnection exception: " + e);
    }

    private void broadcastMsg(String str) {
        broadcastMsg(null, str);
    }

    private void broadcastMsg(SocketConnection socketConnectionFrom, String str) {
        System.out.println(str);
        for (SocketConnection socketConnection : socketConnections) {
            if (!socketConnection.equals(socketConnectionFrom)) {
                socketConnection.sendString(str);
            }
        }
    }

}
