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
//    private final Map<String, SocketConnection> connectionMap = new HashMap<>();
    private final List<SocketConnection> connectionList = new LinkedList<>();

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
        connectionList.add(socketConnection);
        broadcastMsg("Client connected: " + socketConnection);
    }

    @Override
    public synchronized void onReceiveString(SocketConnection socketConnection, String str) {
        broadcastMsg(socketConnection, str);
    }

    @Override
    public synchronized void onDisconnected(SocketConnection socketConnection) {
        connectionList.remove(socketConnection);
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
        for (SocketConnection socketConnection : connectionList) {
            if (!socketConnection.equals(socketConnectionFrom)) {
                socketConnection.sendString(str);
            }
        }
    }

}
