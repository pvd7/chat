/*
 * Copyright (c) 2018.
 */

package ru.chat.client;

import ru.chat.network.SocketConnection;
import ru.chat.network.SocketConnectionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatWindow extends JFrame implements SocketConnectionListener {

    private JPanel contentPane;
    private JTextField tfMsg;
    private JButton btnSend;
    private JTextField tfNick;
    private JTextArea taMessages;
    private JButton buttonOK;

    private static final String HOST = "localhost";
    private static final int PORT = 8888;
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 400;


    public static void main(String[] args) {
        ChatWindow dialog = new ChatWindow();
        dialog.setVisible(true);
    }

    public ChatWindow() {
        setContentPane(contentPane);
        setTitle("Chat");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
    }

    public void connect(String host) {

    }

    private synchronized void printMsg(String str) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                taMessages.append(str);
            }
        });
    }

    @Override
    public void onConnected(SocketConnection socketConnection) {

    }

    @Override
    public void onReceiveString(SocketConnection socketConnection, String str) {

    }

    @Override
    public void onDisconnected(SocketConnection socketConnection) {

    }

    @Override
    public void onException(SocketConnection socketConnection, Exception e) {

    }
}
