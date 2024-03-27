package dev.codex.java.maven.plugin;

public class Strings {
    private Strings() {
        super();
    }

    public static String capitalize(String string) {
        char c = string.charAt(0);
        if (97 <= c && c <= 122) {
            return Character.toUpperCase(c) + string.substring(1);
        }
        return string;
    }
}