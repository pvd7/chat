/*
 * Copyright (c) 2018.
 */

package ru.chat.client;

import ru.chat.network.SocketCommand;
import ru.chat.network.SocketConnection;
import ru.chat.network.SocketConnectionListener;

import javax.swing.*;
import java.io.IOException;
import java.text.SimpleDateFormat;

import static ru.chat.network.utils.StringUtils.getFirstWord;

public class ChatWindow extends JFrame implements SocketConnectionListener {

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

    private JPanel contentPane;
    private JTextField tfMsg;
    private JButton btnSendMsg;
    private JTextField tfNick;
    private JTextArea taMsgs;
    private JButton btnChangeNickname;

    private static final String HOST = "localhost";
    private static final int PORT = 8888;

    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 400;

    private SocketConnection socketConnection;
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

    public ChatWindow() {
        setContentPane(contentPane);
        setTitle("Chat");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        tfMsg.requestFocus();

        btnSendMsg.addActionListener(e -> onSendMsg());
        tfMsg.addActionListener(e -> onSendMsg());
        btnChangeNickname.addActionListener(e -> onChangeNickname());
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
        printMsg(DATE_FORMAT.format(System.currentTimeMillis()) + " " + tfNick.getText() + ": " + tfMsg.getText());

        String cmd = getFirstWord(tfMsg.getText());
        SocketCommand socketCommand = SocketCommand.find(cmd);
        String msg;
        if (socketCommand == null)
            msg = SocketCommand.BROADCAST.getName() + " " + tfMsg.getText();
        else
            msg = tfMsg.getText();
        socketConnection.sendString(msg);

        tfMsg.setText(null);
        tfMsg.requestFocus();
    }

    private void onChangeNickname() {
        socketConnection.sendString(SocketCommand.CHANGE_NICKNAME.getName() + " " + tfNick.getText());
    }

    @Override
    public void onConnected(SocketConnection socketConnection) {
        printMsg("Connection ready: " + socketConnection);
        onChangeNickname();
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
