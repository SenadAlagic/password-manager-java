package utility;

import secureEntry.SecureEntry;

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
import java.util.Arrays;
import java.util.Base64;

public class EncryptionUtil {
    private static final int ITERATION_COUNT = 65536; // A good starting value
    private static final int KEY_LENGTH = 256; // AES 256-bit key
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding"; // Common and secure AES mode

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 16 bytes = 128 bits
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static byte[] generateKey(char[] masterPassword, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(masterPassword, salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.err.println("Error generating key: " + e.getMessage());
            return null;
        }
    }

    public static String encrypt(String data, byte[] key) {
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

    public static String decrypt(char[] masterPassword, String saltString, String data) {
        try {
            byte[] salt = Base64.getDecoder().decode(saltString);
            byte[] key = generateKey(masterPassword, salt);
            byte[] encryptedPasswordWithIv = Base64.getDecoder().decode(data);
            byte[] iv = Arrays.copyOfRange(encryptedPasswordWithIv, 0, 16);
            byte[] ciphertext = Arrays.copyOfRange(encryptedPasswordWithIv, 16, encryptedPasswordWithIv.length);

            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            byte[] decryptedBytes = cipher.doFinal(ciphertext);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Error decrypting data - potentially wrong master password");
            return null;
        }
    }
}
