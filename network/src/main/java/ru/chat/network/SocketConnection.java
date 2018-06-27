/*
 * Copyright (c) 2018.
 */

package ru.chat.network;
import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Scanner;

public class SocketConnection {

    private final Socket socket;
    private final Thread thread;
    private final SocketConnectionListener eventListener;
    private final BufferedReader in;
//    private final Scanner in;
    private final BufferedWriter out;

    public SocketConnection(SocketConnectionListener eventListener, String host, int port) throws IOException {
        this(new Socket(host, port), eventListener);
    }

    public SocketConnection(Socket socket, SocketConnectionListener eventListener) throws IOException {
        this.socket = socket;
        this.eventListener = eventListener;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
//        this.in = new Scanner(socket.getInputStream());
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnected(SocketConnection.this);
                    String str;
                    while (!socket.isClosed()) {
                        str = in.readLine();
//                        str = in.nextLine();
                        eventListener.onReceiveString(SocketConnection.this, str);
                    }
                } catch (IOException e) {
                    eventListener.onException(SocketConnection.this, e);
                } finally {
                    eventListener.onDisconnected(SocketConnection.this);
                }
            }
        });
    }

    public void start() {
        thread.start();
    }

    public synchronized void sendString(String str) {
        try {
            out.write(str + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(SocketConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        try {
            socket.close();
            thread.interrupt();
        } catch (IOException e) {
            eventListener.onException(SocketConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "SocketConnection: " + socket.getInetAddress() + ":" + socket.getPort() ;
    }
}
