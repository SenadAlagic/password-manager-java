package dev.senad;

import com.google.gson.Gson;
import secureEntry.SecureEntry;
import utility.EncryptionUtil;
import utility.InputValidator;
import utility.PasswordUtil;
import vault.Vault;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static utility.PasswordUtil.grabMasterPassword;

public class Main {
    private static final List<SecureEntry> secureEntries = new ArrayList<>();

    public static void main(String[] args) {
        initializeVault();
        runningLoop();
    }

    public static void runningLoop() {
        boolean running = true;
        Scanner input = new Scanner(System.in);
        while (running) {
            String command = InputValidator.secureInput(input, ">> ");
            switch (command) {
                case "exit":
                    running = false;
                    break;
                case "create":
                    createSecureEntry();
                    break;
                case "list":
                    listSecureEntries();
                    break;
                case "test-decrypt":
                    readSecureEntry();
                    break;
                default:
                    System.out.println("Unknown command.");
                    break;
            }
        }
    }

    public static void initializeVault() {
        try {
            String fileName = "vault.txt";
            Path path = Paths.get(fileName);
            if (Files.notExists(path)) {
                Files.writeString(path, "[]");
                System.out.println("File created successfully.");
            } else {
                String content = Files.readString(path);
                Gson gson = new Gson();
                Vault vault = gson.fromJson(content, Vault.class);
                if (vault != null) {
                    decryptVault(vault);
                    System.out.println("Vault data loaded successfully.");
                } else {
                    System.out.println("Vault file is empty or contains invalid data.");
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred while creating the file: " + e.getMessage());
        }
    }

    public static void createSecureEntry() {
        char[] masterPassword = grabMasterPassword(true);
        if (masterPassword == null) {
            System.out.println("Master password confirmation failed.");
            return;
        }
        Scanner input = new Scanner(System.in);

        String website = InputValidator.secureInput(input, "Website: ");
        String username = InputValidator.secureInput(input, "Username: ");
        String password = InputValidator.secureInput(input, "Password (enter 'generate' to autogenerate): ");

        if (password.trim().equalsIgnoreCase("generate")) {
            password = PasswordUtil.generateRandomPassword(16, true, true, true, true); // Generate a password (you can adjust length)
            System.out.println("Generated Password: " + password);
        }

        String salt = EncryptionUtil.generateSalt();
        byte[] decodedSalt = Base64.getDecoder().decode(salt);
        byte[] key = EncryptionUtil.generateKey(masterPassword, decodedSalt);
        Arrays.fill(masterPassword, '\0');
        String encryptedPassword = EncryptionUtil.encrypt(password, key);

        SecureEntry secureEntry = new SecureEntry(website, username, encryptedPassword, salt);
        secureEntries.add(secureEntry);
        String fileName = "vault.txt";
        Path path = Paths.get(fileName);
        if (!Files.exists(path)) {
            System.out.println("Error: File does not exist.");
            return;
        }
        Gson gson = new Gson();
        String json = gson.toJson(secureEntries);
        encryptVault(json);
        System.out.println("Secure entry saved to vault.");
    }

    public static void listSecureEntries() {
        if (secureEntries.isEmpty()) {
            System.out.println("No secure entries found.");
        } else {
            for (SecureEntry entry : secureEntries)
                System.out.println(entry.toString());
        }
    }

    public static void readSecureEntry() {
        Scanner input = new Scanner(System.in);

        char[] masterPassword = grabMasterPassword(false);
        if (masterPassword == null) {
            System.out.println("Master password confirmation failed.");
            return;
        }
        String websiteName = InputValidator.secureInput(input, "Website name: ");
        Optional<SecureEntry> entry = secureEntries.stream().filter(located -> located.website.equals(websiteName)).findFirst();
        if (entry.isEmpty()) {
            System.out.println("Invalid website name.");
            return;
        }
        String decryptedPassword = EncryptionUtil.decrypt(masterPassword, entry.get().salt, entry.get().encryptedPassword);
        Arrays.fill(masterPassword, '\0');
        System.out.println("Decrypted Password: " + decryptedPassword);
    }

    public static void encryptVault(String jsonEntries) {
        char[] masterPassword = grabMasterPassword(true);
        if (masterPassword == null) {
            System.out.println("Master password confirmation failed.");
            return;
        }
        String salt = EncryptionUtil.generateSalt();
        byte[] decodedSalt = Base64.getDecoder().decode(salt);
        byte[] key = EncryptionUtil.generateKey(masterPassword, decodedSalt);
        Arrays.fill(masterPassword, '\0');
        String encryptedJsonEntries = EncryptionUtil.encrypt(jsonEntries, key);
        Vault vault = new Vault(salt, encryptedJsonEntries);
        Gson gson = new Gson();
        String json = gson.toJson(vault);
        String fileName = "vault.txt";
        Path path = Paths.get(fileName);
        try {
            Files.write(path, json.getBytes());
        } catch (IOException e) {
            System.out.println("Error writing vault to file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void decryptVault(Vault vault) {
        char[] masterPassword = grabMasterPassword(false);
        if (masterPassword == null) {
            System.out.println("Master password confirmation failed.");
            return;
        }
        String payload = EncryptionUtil.decrypt(masterPassword, vault.salt, vault.encryptedPayload);
        Arrays.fill(masterPassword, '\0');
        Gson gson = new Gson();
        SecureEntry[] entries = gson.fromJson(payload, SecureEntry[].class);
        secureEntries.addAll(Arrays.asList(entries));
    }

}