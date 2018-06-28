/*
 * Copyright (c) 2018.
 */

package ru.chat.server;

import ru.chat.network.SocketCommands;
import ru.chat.network.SocketConnection;
import ru.chat.network.SocketConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

public class ChatServer implements SocketConnectionListener {

    public static void main(String[] args) throws IOException {
//        System.out.println(SocketCommands.getOrDefault("qwe"));
        ChatServer chatServer = new ChatServer();
        chatServer.start(8888);
    }

    // список подключений
    private final List<SocketConnection> socketConnections = new LinkedList<>();

    private void parsingReceiveString(SocketConnection socketConnection, String str) {
        SocketCommands socketCommands;

        int i = str.indexOf(" ");
        if (i == -1) {
            socketCommands = SocketCommands.BROADCAST;
        } else {
            socketCommands = SocketCommands.getOrDefault(str.substring(0, i), SocketCommands.BROADCAST);
        }

        String msg = (socketCommands == null) ? str : str.substring(i + 1, str.length());

        switch (socketCommands) {
            case CHANGE_NICKNAME:
                changeNickName(socketConnection, msg);
                break;
            case SEND_MSG_TO_USER:
                sendMsgToClient(msg);
                break;
            case BROADCAST:
                broadcastMsg(msg, socketConnection);
                break;
            default:
                broadcastMsg(msg, socketConnection);
                break;
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
        broadcastMsg("Client connected: " + socketConnection, socketConnection);
    }

    @Override
    public synchronized void onReceiveString(SocketConnection socketConnection, String str) {
        parsingReceiveString(socketConnection, str);
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
        broadcastMsg(str, null);
    }

    private void broadcastMsg(String str, SocketConnection socketConnectionFrom) {
        System.out.println(str);
        for (SocketConnection socketConnection : socketConnections) {
            if (!socketConnection.equals(socketConnectionFrom)) {
                socketConnection.sendString(str);
            }
        }
    }

    private void changeNickName(SocketConnection socketConnection, String str) {


    }

    private void sendMsgToClient(String msg) {

    }
}


