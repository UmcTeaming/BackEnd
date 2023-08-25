package com.teaming.TeamingServer.common;

import java.util.Random;

public class KeyGenerator {
    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 6; i++) {
            key.append((rnd.nextInt(10)));
        }

        return key.toString();
    }
}
