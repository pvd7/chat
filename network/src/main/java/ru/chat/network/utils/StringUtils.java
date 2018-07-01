/*
 * Copyright (c) 2018.
 */

package ru.chat.network.utils;

public class StringUtils {

    public static String getFirstWord(String str) {
        int i = str.indexOf(" ");
        if (i == -1) {
            return str;
        } else {
            return str.substring(0, i);
        }
    }

    public static boolean isEmpty(String str) {
        return (str == null) || str.equals("");
    }
}
