package com.rafadev.teamops.shared;

import java.security.SecureRandom;

public final class ShortCodeGenerator {
    private static final String ALPH = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom RND = new SecureRandom();

    private ShortCodeGenerator() {}

    public static String generate(String prefix, int len) {
        StringBuilder sb = new StringBuilder(prefix).append("-");
        for (int i = 0; i < len; i++) sb.append(ALPH.charAt(RND.nextInt(ALPH.length())));
        return sb.toString();
    }
}
