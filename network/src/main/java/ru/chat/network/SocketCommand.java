/*
 * Copyright (c) 2018.
 */

package ru.chat.network;

import java.util.HashMap;
import java.util.Map;

/**
 * Команды используемы при обмене сообщениями между клиентом и сервером
 */
public enum SocketCommand {
    CHANGE_NICKNAME("/cn"), // изменить Ник пользователя
    SEND_MSG_TO_USER("/smu"), // отправить сообщение пользователю
    BROADCAST("/bc"); // отправить широковещательное сообщение

    private String name;

    SocketCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * список команд и их имен, для быстрого поиска
     */
    private static Map<String, SocketCommand> commandsMap = new HashMap<>(3);

    // Заполняет commandsMap
    static {
        for (SocketCommand type : SocketCommand.values()) {
            commandsMap.put(type.name, type);
        }
    }

    /**
     * Ищет сокет-команду по имени
     *
     * @param name         имя команды
     * @param defaultValue по умолчанию
     * @return команда
     */
    public static SocketCommand getOrDefault(String name, SocketCommand defaultValue) {
        SocketCommand socketCommand = commandsMap.get(name);
        return (socketCommand == null) ? defaultValue : socketCommand;
    }

    /**
     * Ищет сокет-команду по имени
     *
     * @param name имя команды
     * @return команда
     */
    public static SocketCommand find(String name) {
        return commandsMap.get(name);
    }

}
