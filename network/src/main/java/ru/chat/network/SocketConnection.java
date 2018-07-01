/*
 * Copyright (c) 2018.
 */

package ru.chat.network;

import ru.chat.network.utils.StringUtils;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Общий класс для сервера и клиента
 * отвечает за отправку и прием сообщений
 */
public class SocketConnection {

    private final Socket socket;
    private final Thread thread;
    private final SocketConnectionListener eventListener;
    private final BufferedReader in;
    private final BufferedWriter out;

    private String userNick;

    /**
     * Создает подключение
     *
     * @param eventListener слушаетель событий в подключении
     * @param host          адрес сервера
     * @param port          порт сервера
     * @throws IOException исключение
     */
    public SocketConnection(SocketConnectionListener eventListener, String host, int port) throws IOException {
        this(new Socket(host, port), eventListener);
    }

    /**
     * Инициализирует подключение
     *
     * @param socket        сокет
     * @param eventListener слушаетель событий в подключении
     * @throws IOException исключени
     */
    public SocketConnection(Socket socket, SocketConnectionListener eventListener) throws IOException {
        this.socket = socket;
        this.eventListener = eventListener;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));

        Timer timer = new Timer(120000, e -> {
            if (StringUtils.isEmpty(userNick)) {
                disconnect();
                System.out.println("disconect");
//                УРАААА мы в 1/4 финала!!!
            }
        });
        timer.start();

        this.thread = new Thread(() -> {
            try {
                eventListener.onConnected(SocketConnection.this);
                String str;
                while (!socket.isClosed()) {
                    str = in.readLine();
                    eventListener.onReceiveString(SocketConnection.this, str);
                }
            } catch (IOException e) {
                eventListener.onException(SocketConnection.this, e);
            } finally {
                eventListener.onDisconnected(SocketConnection.this);
            }
        });
    }

    /**
     * Стартует поток с сокетом подключения
     */
    public void start() {
        thread.start();
    }

    /**
     * Отправляет строку
     *
     * @param str строка
     */
    public synchronized void sendString(String str) {
        try {
            out.write(str + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(SocketConnection.this, e);
            disconnect();
        }
    }

    /**
     * Отклюячает подключение
     */
    private synchronized void disconnect() {
        try {
            socket.close();
            thread.interrupt();
        } catch (IOException e) {
            eventListener.onException(SocketConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "SocketConnection{ " + socket.getInetAddress() + ":" + socket.getPort() + " }";
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

}


