package org.bot.map;

public class Message {
    public static final char CH_EOT = '\u0000';
    private final char[] str;
    private int index = -1;

    public Message(String string) {
        str = string.toCharArray();
    }

    public char nextChar() {
        index++;
        if (index < str.length) {
            return str[index];
        }
        return CH_EOT;
    }
}
