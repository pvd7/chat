/*
 * Copyright (c) 2018.
 */

package ru.chat.server;

import ru.chat.network.SocketCommand;
import ru.chat.network.SocketConnection;
import ru.chat.network.SocketConnectionListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.Timer;

import ru.chat.network.utils.StringUtils;

/**
 * Сервер чата
 */
public class ChatServer implements SocketConnectionListener {

    public static void main(String[] args) throws IOException {
        ChatServer chatServer = new ChatServer();
        chatServer.start(8888);
    }

    private ServerSocket serverSocket;
    // список подключений
    private final List<SocketConnection> socketConnections = new LinkedList<>();

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

    /**
     * Разбор полученных сообщений
     * в зависимотси от управляющих команд вызывает разные методы обработки сообщения
     *
     * @param socketConnection подключение откуда прилетело сообщение
     * @param str              текст сообщения
     */
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

    /**
     * Запускает сервер чата
     *
     * @param port порт сервера
     * @throws IOException исключение
     */
    public void start(int port) throws IOException {
        System.out.println("Server running...");
        serverSocket = new ServerSocket(port);
        while (!serverSocket.isClosed()) {
            try {
                SocketConnection socketConnection = new SocketConnection(serverSocket.accept(), this);
                socketConnection.start();
            } catch (IOException e) {
                System.out.println("Connection ecxeption: " + e);
            }
        }
    }

    /**
     * Отправляет широковещательное сообщение
     *
     * @param str тест сообщения
     */
    private void broadcastMsg(String str) {
        broadcastMsg(str, null);
    }

    /**
     * Отправляет широковещательно сообщение, исключая подключение от куда оно пришло
     *
     * @param str                  текст сообщения
     * @param socketConnectionFrom подключение откуда прилетело сообщение
     */
    private void broadcastMsg(String str, SocketConnection socketConnectionFrom) {
        sendMsg(str, null, socketConnectionFrom);

//        String msg = prepareSendString(str, socketConnectionFrom);
//        System.out.println(msg);
//
//        for (SocketConnection socketConnection : socketConnections) {
//            if (!socketConnection.equals(socketConnectionFrom)) {
//                socketConnection.sendString(msg);
//            }
//        }
    }

    /**
     * Изменяет Ник пользователя на сервере
     *
     * @param socketConnection подключение которое меняет Ник
     * @param str              исходное сообщение
     */
    private void changeNickName(SocketConnection socketConnection, String str) {
        String msg = null;
        String newNick = StringUtils.getFirstWord(str.trim());
        String currentNick = socketConnection.getUserNick();

        if (currentNick == null) {
            msg = "New user: " + newNick;
        } else if (!currentNick.equals(newNick)) {
            msg = socketConnection.getUserNick() + " changed nick to " + newNick;
        }

        if (msg != null) {
            socketConnection.setUserNick(newNick);
//            socketConnection.sendString(SocketCommand.SEND_MSG_TO_USER.getName() + " commit");
            broadcastMsg(msg);
        }
    }

    /**
     * Отправляет сообщение пользователю
     *
     * @param str                  исходное сообещение, содержит Ник кому переслать это сообщение
     * @param socketConnectionFrom подключени откуда прилетело сообщение
     */
    private void sendMsgToUser(String str, SocketConnection socketConnectionFrom) {
        String userNickTo = StringUtils.getFirstWord(str.trim());
        sendMsg(str, userNickTo, socketConnectionFrom);
//        for (SocketConnection socketConnection : socketConnections) {
//            if ((userNickTo != null) && (userNickTo.equals(socketConnection.getUserNick()))) {
//                String msg = prepareSendString(str, socketConnectionFrom);
//                System.out.println(msg);
//                socketConnection.sendString(msg);
//                break;
//            }
//        }
    }

    private void sendMsg(String str, String userNickTo, SocketConnection socketConnectionFrom) {
        String msg = prepareSendString(str, socketConnectionFrom);
        System.out.println(msg);
        boolean toUser = !StringUtils.isEmpty(userNickTo);

        for (SocketConnection socketConnection : socketConnections) {
            if (!socketConnection.equals(socketConnectionFrom)) {
                if (toUser) {
                    if (userNickTo.equals(socketConnection.getUserNick())) {
                        socketConnection.sendString(msg);
                        break;
                    }
                } else {
                    socketConnection.sendString(msg);
                }
            }
        }
//        if ((userNickTo != null) && (userNickTo.equals(socketConnection.getUserNick()))) {
//            String msg = prepareSendString(str, socketConnectionFrom);
//            System.out.println(msg);
//            socketConnection.sendString(msg);
//            break;
//        }
    }

    private String prepareSendString(String str, SocketConnection socketConnectionFrom) {
        return DATE_FORMAT.format(System.currentTimeMillis()) + " "
                + (((socketConnectionFrom != null) && (!StringUtils.isEmpty(socketConnectionFrom.getUserNick()))) ? socketConnectionFrom.getUserNick() : "")
                + ": " + str;
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
        socketConnections.remove(socketConnection.getUserNick());
        broadcastMsg("Client disconnected: " + socketConnection);
    }

    @Override
    public synchronized void onException(SocketConnection socketConnection, Exception e) {
        System.out.println("SocketConnection exception: " + e);
    }

}


