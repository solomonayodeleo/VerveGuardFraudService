package com.interswitch.verveguardfraudservice.utils;

import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Utils {
    @Value("${verveguard.security.hash-salt}")
    private static String hashSalt;

    public static String hashCardNumber(String cardNumber) {
        try {
            String inputWithSalt = cardNumber + hashSalt;

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(inputWithSalt.getBytes(StandardCharsets.UTF_8));

            // Convert the raw bytes into a readable Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing card number", e);
        }
    }

}
