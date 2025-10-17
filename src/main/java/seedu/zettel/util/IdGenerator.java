package seedu.zettel.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for generating deterministic 8-character hexadecimal IDs
 * based on note content and creation timestamp.
 */
public class IdGenerator {
    private static final int ID_LENGTH = 8;
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int BYTES_FOR_ID = 4; // 4 bytes = 8 hex characters

    /**
     * Generates an 8-character hexadecimal ID by hashing the input string.
     * Uses SHA-256 algorithm and takes the first 4 bytes of the hash.
     *
     * @param input The string to hash (typically title + timestamp)
     * @return 8-character lowercase hexadecimal ID
     */
    public static String generateId(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert first 4 bytes to hex (8 characters)
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < BYTES_FOR_ID; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fallback to timestamp-based ID if SHA-256 unavailable
            // This should never happen in standard JVM environments
            return generateFallbackId(input);
        }
    }

    /**
     * Generates a fallback ID based on the hashCode of the input.
     * Used only if SHA-256 is unavailable.
     *
     * @param input The string to generate ID from
     * @return 8-character hexadecimal ID
     */
    private static String generateFallbackId(String input) {
        int hashCode = input.hashCode();
        long timestamp = System.currentTimeMillis();
        long combined = ((long) hashCode << 32) | (timestamp & 0xFFFFFFFFL);
        String hex = Long.toHexString(combined);

        // Ensure exactly 8 characters
        if (hex.length() >= ID_LENGTH) {
            return hex.substring(0, ID_LENGTH);
        } else {
            return String.format("%8s", hex).replace(' ', '0');
        }
    }
}
