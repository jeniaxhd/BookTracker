package sk.upjs.paz;/*package sk.upjs.paz;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {
    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();

            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }
}
*/


import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordHasher {
    private static final SecureRandom RNG = new SecureRandom();
    private static final int ITER = 65_536;
    private static final int KEY_LEN = 256;

    private PasswordHasher() {
    }

    public static String hash(String password) {
        byte[] salt = new byte[16];
        RNG.nextBytes(salt);
        byte[] dk = pbkdf2(password.toCharArray(), salt, ITER, KEY_LEN);
        return ITER + ":" + b64(salt) + ":" + b64(dk);
    }

    public static boolean verify(String password, String stored) {
        if (password == null || stored == null || stored.isBlank()) return false;

        String[] parts = stored.split(":");
        if (parts.length != 3) return false;

        int iter;
        try {
            iter = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            return false;
        }

        byte[] salt = b64d(parts[1]);
        byte[] expected = b64d(parts[2]);

        byte[] actual = pbkdf2(password.toCharArray(), salt, iter, expected.length * 8);
        return constantTimeEquals(expected, actual);
    }


    private static byte[] pbkdf2(char[] pass, byte[] salt, int iter, int keyLenBits) {
        try {
            PBEKeySpec spec = new PBEKeySpec(pass, salt, iter, keyLenBits);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int r = 0;
        for (int i = 0; i < a.length; i++) r |= (a[i] ^ b[i]);
        return r == 0;
    }

    private static String b64(byte[] x) {
        return Base64.getEncoder().encodeToString(x);
    }

    private static byte[] b64d(String s) {
        return Base64.getDecoder().decode(s);
    }
}
