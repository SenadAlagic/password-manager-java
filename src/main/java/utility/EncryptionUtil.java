package utility;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class EncryptionUtil {
    private static final String MASTER_PASSWORD = "your_master_password"; // Placeholder for master password
    private static final int ITERATION_COUNT = 65536; // A good starting value
    private static final int KEY_LENGTH = 256; // AES 256-bit key
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding"; // Common and secure AES mode

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 16 bytes = 128 bits
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static byte[] generateKey(byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(MASTER_PASSWORD.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.err.println("Error generating key: " + e.getMessage());
            return null;
        }
    }

    public static String encryptPassword(String data, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            SecureRandom randomIv = new SecureRandom();
            byte[] iv = new byte[16];
            randomIv.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] cipherText = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            // Combine IV and ciphertext for storage
            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            System.err.println("Error encrypting data: " + e.getMessage());
            return null;
        }
    }
}
