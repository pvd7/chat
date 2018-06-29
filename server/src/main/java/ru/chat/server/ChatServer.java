/*
 * Copyright (c) 2018.
 */

package ru.chat.server;

import ru.chat.network.SocketCommand;
import ru.chat.network.SocketConnection;
import ru.chat.network.SocketConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.chat.network.utils.StringUtils.getFirstWord;

public class ChatServer implements SocketConnectionListener {

    public static void main(String[] args) throws IOException {
        ChatServer chatServer = new ChatServer();
        chatServer.start(8888);
    }

    // список подключений
    private final List<SocketConnection> socketConnections = new LinkedList<>();
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

    private void parsingReceiveString(SocketConnection socketConnection, String str) {
        String cmd;

        for (SocketCommand socketCommand : SocketCommand.values()) {
            cmd = socketCommand.getName();
            if (str.startsWith(cmd)) {
                String msg = str.substring(cmd.length()).trim();
                switch (socketCommand) {
                    case CHANGE_NICKNAME:
                        changeNickName(socketConnection, msg);
                        break;
                    case SEND_MSG_TO_USER:
                        sendMsgToUser(msg, socketConnection);
                        break;
                    case BROADCAST:
                        broadcastMsg(msg, socketConnection);
                        break;
                    default:
                        broadcastMsg(msg, socketConnection);
                        break;
                }
                break;
            }
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
        String msg = null;
        String newNick = getFirstWord(str.trim());
        String currentNick = socketConnection.getUserNick();

        if (currentNick == null) {
            msg = "New user: " + newNick;
        } else if (!currentNick.equals(newNick)) {
            msg = socketConnection.getUserNick() + " changed nick to " + newNick;
        }

        if (msg != null) {
            socketConnection.setUserNick(newNick);
            broadcastMsg(msg, socketConnection);
        }
    }

    private void sendMsgToUser(String str, SocketConnection socketConnectionFrom) {
        String userNick = getFirstWord(str.trim());
        for (SocketConnection socketConnection : socketConnections) {
            if ((userNick != null) && (userNick.equals(socketConnection.getUserNick()))) {
                String msg = DATE_FORMAT.format(System.currentTimeMillis()) + " "
                        + socketConnectionFrom.getUserNick() + ": "
                        + str.substring(userNick.length()).trim();
                socketConnection.sendString(msg);
                break;
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

}


//        String msg;
//        SocketCommand socketCommand;
//
//        int i = str.indexOf(" ");
//        if (i == -1) {
//            socketCommand = SocketCommand.BROADCAST;
//            msg = str;
//        } else {
//            socketCommand = SocketCommand.find(str.substring(0, i));
//            if (socketCommand == null) socketCommand = SocketCommand.BROADCAST;
//            msg = str.substring(i + 1, str.length());
//        }

//    private String getCommand(String str) {
//        int i = str.indexOf(" ");
//        return "";
//    }
