package com.aseds.aithssainesbaiti.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Utility class for hashing operations.
 * This class is designed as final and not for extension.
 * All methods are static and provide complete hashing functionality.
 */
public final class HashUtils {

    /**
     * Hexadecimal mask for byte conversion.
     */
    private static final int HEX_MASK = 0xff;

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private HashUtils() {
        // Utility class should not be instantiated
    }

    /**
     * Hashes a string using SHA-256 algorithm.
     *
     * @param input the string to be hashed
     * @return the hashed string in hexadecimal format
     * @throws RuntimeException if hashing operation fails
     */
    public static String hash(final String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(
                    input.getBytes(StandardCharsets.UTF_8)
            );
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(HEX_MASK & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
