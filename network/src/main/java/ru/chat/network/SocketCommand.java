/*
 * Copyright (c) 2018.
 */

package ru.chat.network;

import java.util.HashMap;
import java.util.Map;

public enum SocketCommand {
    CHANGE_NICKNAME("/change_nickname"),
    SEND_MSG_TO_USER("/send_msg_to_user"),
    BROADCAST("/broadcast");

    private String name;

    SocketCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private static Map<String, SocketCommand> commandsMap = new HashMap<>(3);

    static {
        for (SocketCommand type : SocketCommand.values()) {
            commandsMap.put(type.name, type);
        }
    }
    public static SocketCommand getOrDefault(String name, SocketCommand defaultValue) {
        SocketCommand socketCommand = commandsMap.get(name);
        return (socketCommand == null) ? defaultValue : socketCommand;
    }

    public static SocketCommand find(String name) {
        return commandsMap.get(name);
    }

}
