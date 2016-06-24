package com.novoda.github.reports.util;

public class StringHelper {

    public static boolean isNullOrEmpty(String string) {
        return (string == null || string.isEmpty());
    }

    public static String emojiToString(int emojiCode) {
        return String.valueOf(Character.toChars(emojiCode));
    }

}
