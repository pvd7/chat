/*
 * Copyright (c) 2018.
 */

package ru.chat.client;

import ru.chat.network.SocketConnection;
import ru.chat.network.SocketConnectionListener;

import javax.swing.*;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class ChatWindow extends JFrame implements SocketConnectionListener {

    private JPanel contentPane;
    private JTextField tfMsg;
    private JButton btnSend;
    private JTextField tfNick;
    private JTextArea taMsgs;

    private static final String HOST = "localhost";
    private static final int PORT = 8888;

    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 400;

    private SocketConnection socketConnection;
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ChatWindow dialog = new ChatWindow();
                dialog.setVisible(true);
                dialog.tfMsg.requestFocus();
                dialog.connect(HOST, PORT);
            }
        });
    }

    public ChatWindow() {
        setContentPane(contentPane);
        setTitle("Chat");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);

        setAlwaysOnTop(true);

        btnSend.addActionListener(e -> onSendMsg());
        tfMsg.addActionListener(e -> onSendMsg());

        tfMsg.requestFocus();
    }

    public void connect(String host, int port) {
        try {
            socketConnection = new SocketConnection(this, host, port);
            socketConnection.start();
        } catch (IOException e) {
            printMsg("Connection exception: " + e);
        }
    }

    private synchronized void printMsg(String str) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                taMsgs.append(str + "\n");
                taMsgs.setCaretPosition(taMsgs.getDocument().getLength());
            }
        });
    }

    private void onSendMsg() {
        String msg = DATE_FORMAT.format(System.currentTimeMillis()) + " " + tfNick.getText() + ": " + tfMsg.getText();
        printMsg(msg);
        socketConnection.sendString(msg);

        tfMsg.setText(null);
        tfMsg.requestFocus();
    }

    @Override
    public void onConnected(SocketConnection socketConnection) {
        printMsg("Connection ready: " + socketConnection);
    }

    @Override
    public void onReceiveString(SocketConnection socketConnection, String str) {
        printMsg(str);
    }

    @Override
    public void onDisconnected(SocketConnection socketConnection) {
        printMsg("Connection close");
    }

    @Override
    public void onException(SocketConnection socketConnection, Exception e) {
        printMsg("Connection exception: " + e);
    }
}
