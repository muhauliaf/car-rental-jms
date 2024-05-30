package edu.uchicago.mauliafirmansyah.utils;

import java.security.SecureRandom;
import java.util.Random;

public class Randomizer {
    private static final String ALPHABET_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String NUMERIC_CHARACTERS = "1234567890";

    public static Randomizer getInstance() {
        initInstance();
        return instance;
    }

    public Random rand() {
        return instance.random;
    }

    public String randString(int length) {
        StringBuilder randomString = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            randomString.append(ALPHABET_CHARACTERS.charAt(rand().nextInt(ALPHABET_CHARACTERS.length())));
        }
        return randomString.toString();
    }

    public String randNumberString(int length) {
        StringBuilder randomString = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            randomString.append(NUMERIC_CHARACTERS.charAt(rand().nextInt(NUMERIC_CHARACTERS.length())));
        }
        return randomString.toString();
    }

    private static void initInstance() {
        if (instance == null) {
            instance = new Randomizer();
        }
    }

    public Random random;
    private static Randomizer instance = null;

    private Randomizer() {
        random = new SecureRandom();
    }
}
