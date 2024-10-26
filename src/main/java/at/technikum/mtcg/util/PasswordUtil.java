package at.technikum.mtcg.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());  // Salt zum Hashen hinzufügen
            byte[] hashedPassword = md.digest(password.getBytes());
            return salt + ":" + Base64.getEncoder().encodeToString(hashedPassword); // Salt und Hash speichern
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing-Algorithmus nicht gefunden", e);
        }
    }
}