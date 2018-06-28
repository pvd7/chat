/*
 * Copyright (c) 2018.
 */

package ru.chat.network;

import java.util.HashMap;
import java.util.Map;

public enum SocketCommands {
    CHANGE_NICKNAME("/cn"),
    SEND_MSG_TO_USER("/smu"),
    BROADCAST("/bc");

    private String name;

    private static Map<String, SocketCommands> commandsMap = new HashMap<>(3);

    static {
        for (SocketCommands type : SocketCommands.values()) {
            commandsMap.put(type.name, type);
        }
    }

    SocketCommands(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SocketCommands getOrDefault(String name, SocketCommands defaultValue) {
        SocketCommands socketCommands = commandsMap.get(name);
        return (socketCommands == null) ? defaultValue : socketCommands;
    }
}
