/*
 * Copyright (c) 2018.
 */

package ru.chat.network;

/**
 * слой абстракции, который описывает какие события могут быть в подключении
 */
public interface SocketConnectionListener {

    void onConnected(SocketConnection socketConnection);
    void onReceiveString(SocketConnection socketConnection, String str);
    void onDisconnected(SocketConnection socketConnection);
    void onException(SocketConnection socketConnection, Exception e);
}
